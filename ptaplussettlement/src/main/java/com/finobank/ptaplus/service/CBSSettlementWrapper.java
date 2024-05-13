package com.finobank.ptaplus.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.finobank.ptaplus.SettlementUtils.SettlementStages;
import com.finobank.ptaplus.client.EodBatchRuleClient;
import com.finobank.ptaplus.client.SettlementBatchClient;
import com.finobank.ptaplus.payload.request.BatchConfigRule;
import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.request.SettlementRuleRequest;
import com.finobank.ptaplus.payload.response.PartnerSettlementRule;
import com.finobank.ptaplus.payload.response.SettlementResponse;
import com.finobank.ptaplus.payload.response.SettlementRuleResponse;
import com.finobank.ptaplus.repository.logical.SettlementAuditRepository;
import com.finobank.ptaplus.repository.logical.SettlementLeg;
import com.finobank.ptaplus.repository.logical.model.CBSSettlementTable;
import com.finobank.ptaplus.repository.logical.model.SettlementAuditTable;
import com.finobank.ptaplus.service.apiBasedSettlement.CBSApiSettlement;
import com.finobank.ptaplus.service.fileBasedSettlement.IFTFileGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CBSSettlementWrapper {

    @Inject
    SettlementLeg settlementLeg;

    @RestClient
    SettlementBatchClient settlementBatchClient;

    @RestClient
    EodBatchRuleClient eodBatchRuleClient;

    @Inject
    SettlementAuditRepository settlementAuditRepository;

    @Inject
    CBSApiSettlement cbsApiSettlement;

    @Inject
    IFTFileGenerator iftFileGenerator;

    public Response performSettlement(SettlementRequest settlementRequest) {
        Response response = null;
        try {
            if(!isRunnable(settlementRequest)){
                return Response.ok(new SettlementResponse("1","Not a running batch.")).build();
            }
            SettlementRuleResponse settlementRuleResponse = getSettlementBatch(settlementRequest.getBatchId(),
                    settlementRequest.getSettlementOfTheDay());
            response = initializeTransactions(settlementRequest, settlementRuleResponse,false);
        } catch (Exception e) {
            settlementAuditRepository.StopSettlement(settlementRequest.getBatchId(), SettlementStages.CBS_SETTLEMENT_FAILED.toString(),settlementRequest.getSettlementOfTheDay());
            log.error("Error Occured while performing cbs settlement ::{}", e.getMessage());
            response=Response.ok(new SettlementResponse("1", e.getMessage())).build();
        }
        return response;
    }

    public Response performEod(SettlementRequest settlementRequest) {
        Response response = null;
        try {
            if(!isRunnable(settlementRequest)){
                return Response.ok(new SettlementResponse("1","Not a running batch.")).build();
            }
        SettlementRuleResponse settlementRuleResponse = getEodSettlementBatch(settlementRequest.getBatchId(),
                    settlementRequest.getSettlementOfTheDay());
            response = initializeTransactions(settlementRequest, settlementRuleResponse,true);
        } catch (Exception e) {
            settlementAuditRepository.StopSettlement(settlementRequest.getBatchId(), SettlementStages.CBS_SETTLEMENT_FAILED.toString(),settlementRequest.getSettlementOfTheDay());
            log.error("Error Occured while performing cbs eod ::{}", e.getMessage());
            response=Response.ok(new SettlementResponse("1", e.getMessage())).build();
        }
        return response;
    }
    private Response initializeTransactions(SettlementRequest settlementRequest,
    SettlementRuleResponse settlementRuleResponses,boolean isEod) {
        if(settlementRequest.isApiExecutionMode()){
            return runForApiMode(settlementRequest, settlementRuleResponses, isEod);
        }
        else{
            return iftFileGenerator.runSettlement(settlementRequest, settlementRuleResponses, isEod);
        }
    }
    private Response runForApiMode(SettlementRequest settlementRequest,
            SettlementRuleResponse settlementRuleResponses,boolean isEod) {
        int totalPartnerSettled = 0;
        int failedPartners = 0;
        for (PartnerSettlementRule partnerSettlementRule : settlementRuleResponses.getPartnerSettlementRules()) {
            try {
                log.info("Running settlement for partner : {} with partnerGl : {} ",
                        partnerSettlementRule.getPartnerId(),
                        partnerSettlementRule.getPtaPartnerGl());
                List<CBSSettlementTable> unsettledTransactions = getUnsettledTrasactions(
                        partnerSettlementRule.getPtaPartnerGl());
                Response response = cbsApiSettlement.perform(unsettledTransactions, settlementRequest,
                        isEod);
                if (response.getStatus() == 200) {
                    totalPartnerSettled++;
                } else {
                    failedPartners++;
                }

            } catch (Exception e) {
                log.error("Error occured During settlement of PartnerGL {} :: {}",
                        partnerSettlementRule.getPtaPartnerGl(), e.getLocalizedMessage());
            }
        }

        SettlementAuditTable settlementAuditTable = SettlementAuditTable.builder()
                .successCount(totalPartnerSettled)
                .recordsCount(settlementRuleResponses.getPartnerSettlementRules().size())
                .rejectCount(settlementRuleResponses.getPartnerSettlementRules().size() - totalPartnerSettled
                        - failedPartners)
                .batchId(settlementRequest.getBatchId())
                .failedCount(failedPartners)
                .remarks(SettlementStages.P2M_POSTING_SUCCEEDED.toString())
                .settlementId(settlementRequest.getSettlementOfTheDay()).build();
        settlementAuditRepository.addSettlementResult(settlementAuditTable);
        return Response.ok(new SettlementResponse("0",totalPartnerSettled + " partner settled at CBS.")).build();
    }

    private List<CBSSettlementTable> getUnsettledTrasactions(String partnerGl) {
        List<CBSSettlementTable> cbsSettlementTables = new ArrayList<>();
        cbsSettlementTables = settlementLeg.getUnsettledTransactions(partnerGl);
        log.info("CBS Settlement Tables : {} ",cbsSettlementTables );
        if (cbsSettlementTables.size() == 0) {
            log.error("No Unsettled Transaction for {}", partnerGl);
            throw new WebApplicationException("no transaction to settle.");
        }
        return cbsSettlementTables;
    }

    private SettlementRuleResponse getEodSettlementBatch(String BatchId, String settlementOfTheDay) {
        SettlementRuleRequest settlementRuleRequest = new SettlementRuleRequest(BatchId);
        List<SettlementRuleRequest> batchRequest = new ArrayList<SettlementRuleRequest>();
        batchRequest.add(settlementRuleRequest);
        BatchConfigRule batchConfigRule = new BatchConfigRule(batchRequest);
        SettlementRuleResponse settlementRuleResponse = null;
        try {
            List<SettlementRuleResponse> settlementRuleResponses = eodBatchRuleClient.getBatch(batchConfigRule);
            if (settlementRuleResponses.size() == 0) {
                throw new WebApplicationException();
            } else {
                settlementRuleResponse = settlementRuleResponses.get(0);
            }
        } catch (Exception e) {
            settlementAuditRepository.StopSettlement(BatchId, SettlementStages.INITIALIZATION_FAILED.toString(),
                    settlementOfTheDay);
            log.error("error occured while getting batch from rule engine.{}", e.getLocalizedMessage());
            throw new WebApplicationException("Couldn't get batchConfig from rule.");
        }
        return settlementRuleResponse;
    }

    private SettlementRuleResponse getSettlementBatch(String BatchId, String settlementOfTheDay) {
        SettlementRuleRequest settlementRuleRequest = new SettlementRuleRequest(BatchId);
        List<SettlementRuleRequest> batchRequest = new ArrayList<SettlementRuleRequest>();
        batchRequest.add(settlementRuleRequest);
        BatchConfigRule batchConfigRule = new BatchConfigRule(batchRequest);
        SettlementRuleResponse settlementRuleResponse = null;
        try {
            List<SettlementRuleResponse> settlementRuleResponses = settlementBatchClient.getBatch(batchConfigRule);
            if (settlementRuleResponses.size() == 0) {
                throw new WebApplicationException();
            } else {
                settlementRuleResponse = settlementRuleResponses.get(0);
            }
        } catch (Exception e) {
            settlementAuditRepository.StopSettlement(BatchId, SettlementStages.INITIALIZATION_FAILED.toString(),
                    settlementOfTheDay);
            log.error("error occured while getting batch from rule engine.{}", e.getLocalizedMessage());
            throw new WebApplicationException("Couldn't get batchConfig from rule.");
        }
        return settlementRuleResponse;
    }

    private boolean isRunnable(SettlementRequest settlementRequest) {
        if (settlementAuditRepository.isRunningBatch(settlementRequest.getBatchId())) {
            settlementAuditRepository.updateStatus(settlementRequest.getBatchId(),
                    SettlementStages.CBS_SETTLEMENT_STARTED.toString(), settlementRequest.getSettlementOfTheDay());
            return true;
        } else {
            return false;
        }

    }

}
