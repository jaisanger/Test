package com.finobank.ptaplus.service.finoSettlement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.finobank.ptaplus.SettlementUtils.SettlementStages;
import com.finobank.ptaplus.SettlementUtils.WriteAsJson;
import com.finobank.ptaplus.client.PostTransaction;
import com.finobank.ptaplus.client.SettlementBatchClient;
import com.finobank.ptaplus.payload.request.BatchConfigRule;
import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.request.SettlementRuleRequest;
import com.finobank.ptaplus.payload.response.PartnerSettlementRule;
import com.finobank.ptaplus.payload.response.SettlementResponse;
import com.finobank.ptaplus.payload.response.SettlementRuleResponse;
import com.finobank.ptaplus.repository.logical.SettlementAuditRepository;
import com.finobank.ptaplus.repository.logical.TransactionLeg;
import com.finobank.ptaplus.service.SettlementTransactions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class FinoSettlement {

    @RestClient
    PostTransaction postTransaction;

    @Inject
    TransactionLeg transactionLeg;

    @RestClient
    SettlementBatchClient settlementBatchClient;

    @Inject
    SettlementTransactions settlementTransactionUtils;

    @Inject
    SettlementAuditRepository settlementAuditRepository;

    public Response process(SettlementRequest settlementRequest) {
        Response response = null;
        try {
            if(!isRunnableBatch(settlementRequest,false)){
                return Response.ok(new SettlementResponse("1", "Batch is Already running or waiting for response.")).build();
            }
            SettlementRuleResponse settlementRuleResponse = getSettlementBatch(settlementRequest.getBatchId(),
                    settlementRequest.getSettlementOfTheDay());
            log.info("Settlement Batch Received from Rule Engine {} .", WriteAsJson.log(settlementRuleResponse));
            settlementAuditRepository.updateStatus(settlementRequest.getBatchId(),
                    SettlementStages.P2M_POSTING_RUNNING.toString(), settlementRequest.getSettlementOfTheDay());
            response = performSettlements(settlementRequest, settlementRuleResponse);
            settlementAuditRepository.updateStatus(settlementRequest.getBatchId(),
                    SettlementStages.P2M_POSTING_SUCCEEDED.toString(), settlementRequest.getSettlementOfTheDay());
        } catch (Exception e) {
            settlementAuditRepository.StopSettlement(settlementRequest.getBatchId(),
                    SettlementStages.P2M_POSTING_FAILED.toString(), settlementRequest.getSettlementOfTheDay());
            return Response.ok(new SettlementResponse("1", e.getMessage())).build();
        }
        return response;
    }

    private Response performSettlements(SettlementRequest settlementRequest,
            SettlementRuleResponse settlementRuleResponses) {
        int totalPartnerSettled = 0;
        for (PartnerSettlementRule partnerSettlementRule : settlementRuleResponses.getPartnerSettlementRules()) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE,
                        -Integer.parseInt(partnerSettlementRule.getThresholdTime()));
                Date settlementDateforParter = cal.getTime();
                log.info("Running settlement for partner : {} with partnerGl : {} ",
                        partnerSettlementRule.getPartnerId(),
                        partnerSettlementRule.getPtaPartnerGl());
                Response response = settlementTransactionUtils.perform(settlementRequest,
                        partnerSettlementRule, settlementDateforParter,false);
                if (response.getStatus() == 200) {
                    totalPartnerSettled++;
                    transactionLeg.updateSettledStatus(partnerSettlementRule.getPtaPartnerGl(),
                            partnerSettlementRule.getThresholdAmt(), settlementDateforParter,
                            settlementRequest.getSettlementOfTheDay(),false);
                } else {
                    log.error("Response from settlementTransactions {}.", response.getEntity());
                }

            } catch (Exception e) {
                log.error("Error occured During settlement of Partner {}", e.toString());
            }
        }
        // if (totalPartnerSettled == 0) {
        //     throw new WebApplicationException("No Transaction to Settle.");
        // }
        Response response = Response.ok(new SettlementResponse("0", totalPartnerSettled + " partner settled at P2M."))
                .build();
        return response;
    }

    private SettlementRuleResponse getSettlementBatch(String BatchId, String settlementOfTheDay) {
        SettlementRuleRequest settlementRuleRequest = new SettlementRuleRequest(BatchId);
        List<SettlementRuleRequest> batchRequest = new ArrayList<SettlementRuleRequest>();
        batchRequest.add(settlementRuleRequest);
        BatchConfigRule batchConfigRule = new BatchConfigRule(batchRequest);
        SettlementRuleResponse settlementRuleResponse = null;
        try {
            List<SettlementRuleResponse> settlementRuleResponses = settlementBatchClient.getBatch(batchConfigRule);
            if (settlementRuleResponses.size() == 0
                    || settlementRuleResponses.get(0).getPartnerSettlementRules().size() == 0) {
                throw new WebApplicationException();
            } else {
                settlementRuleResponse = settlementRuleResponses.get(0);
            }
        } catch (Exception e) {
            settlementAuditRepository.updateStatus(BatchId, SettlementStages.INITIALIZATION_FAILED.toString(),
                    settlementOfTheDay);
            log.error("error occured while getting batch from rule engine.{}", e.getLocalizedMessage());
            throw new WebApplicationException("Couldn't get batchConfig from rule.");
        }
        return settlementRuleResponse;
    }

    private boolean isRunnableBatch(SettlementRequest settlementRequest, boolean isEod) {
        if (settlementAuditRepository.isRunningBatch(settlementRequest.getBatchId())) {
            return false;
        }

        settlementAuditRepository.StartSettlement(settlementRequest.getBatchId(),
                settlementRequest.getSettlementOfTheDay(), settlementRequest.isApiExecutionMode(),isEod);
                return true;
    }
}
