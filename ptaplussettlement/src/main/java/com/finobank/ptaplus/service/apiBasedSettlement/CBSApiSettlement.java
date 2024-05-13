package com.finobank.ptaplus.service.apiBasedSettlement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.finobank.ptaplus.SettlementUtils.CorelationId;
import com.finobank.ptaplus.SettlementUtils.SettlementStages;
import com.finobank.ptaplus.SettlementUtils.WriteAsJson;
import com.finobank.ptaplus.client.CbsTransactionRestClient;
import com.finobank.ptaplus.payload.request.CbsPostTransactionLegRequest;
import com.finobank.ptaplus.payload.request.CbsPostTransactionRequest;
import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.response.SettlementResponse;
import com.finobank.ptaplus.payload.response.SettlementRuleResponse;
import com.finobank.ptaplus.repository.logical.SettlementAuditRepository;
import com.finobank.ptaplus.repository.logical.SettlementLeg;
import com.finobank.ptaplus.repository.logical.model.CBSSettlementTable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CBSApiSettlement {

    @RestClient
    CbsTransactionRestClient cbsTransactionRestClient;

    @Inject
    SettlementLeg settlementLeg;

    @Inject
    CorelationId corelationId;

    @Inject
    SettlementAuditRepository settlementAuditRepository;

    @ConfigProperty(name = "settlementAppId")
    String settlementAppId;

    @ConfigProperty(name = "iftTransactionCommentFormat")
    String iftTransactionCommentFormat;

    @ConfigProperty(name = "settlementTranCategoryInComment")
    String settlementTranCategoryInComment;
    @ConfigProperty(name = "eodTranCategoryInComment")
    String eodTranCategoryInComment;

    @ConfigProperty(name = "costCentre")
    String costCentre;
    @ConfigProperty(name = "ApiModeRetryCount")
    int retryCount;

    @ConfigProperty(name = "settlementIftRfuFormat")
    String settlementIftRfuFormat;

    public Response perform(List<CBSSettlementTable> unsettledTransactions, SettlementRequest settlementRequest,
            Boolean isEod) {
        settlementAuditRepository.updateStatus(settlementRequest.getBatchId(),
                SettlementStages.CBS_TRANSACTIONS_RUNNING.toString(), settlementRequest.getSettlementOfTheDay());
        List<List<CBSSettlementTable>> batchs = getBatchs(unsettledTransactions);
        for (int i = 0; i < batchs.size(); i++) {
            cbsPosting(batchs.get(i), settlementRequest.getSettlementOfTheDay(), isEod);
        }
        log.info("Settlement at cbs Completed.");
        settlementAuditRepository.updateStatus(settlementRequest.getBatchId(), SettlementStages.SETTLEMENT.toString(),
                settlementRequest.getSettlementOfTheDay());
        return Response.ok(new SettlementResponse("0", "Settlement at cbs Completed.")).build();
    }

    private void cbsPosting(List<CBSSettlementTable> unsettledList, String settlementOftheDay, Boolean isEod) {
        List<CbsPostTransactionLegRequest> acctFundTransferLegs = new ArrayList<>();
        String referenceNo = unsettledList.get(0).getReferenceNo();
        acctFundTransferLegs = getAcctFundTransferlegs(unsettledList, referenceNo, settlementOftheDay, isEod);
        CbsPostTransactionRequest cbsPostTransactionRequest = CbsPostTransactionRequest.builder()
                .appId(settlementAppId)
                .valueDate(null)
                .analysisFlag(0)
                .reversalFlag(null)
                .isInclusive(0)
                .isClubbed(0)
                .referenceNo(referenceNo)
                .acctFundTransferLegs(acctFundTransferLegs)
                .build();

        makeSettlementPosting(cbsPostTransactionRequest, settlementOftheDay);

    }

    private void makeSettlementPosting(CbsPostTransactionRequest cbsPostTransactionRequest, String settlementOftheDay) {
        log.info("Requesting cbs settlement with payload :{}", WriteAsJson.log(cbsPostTransactionRequest));
        try {
            Response response = cbsTransactionRestClient.postTransaction(cbsPostTransactionRequest);
            HashMap<String, String> responseMap = response.readEntity(HashMap.class);
            log.info("CBS response for txn {} : {}", cbsPostTransactionRequest.getReferenceNo(),
                    WriteAsJson.log(responseMap));
            if (responseMap.get("returnCode").equals("0")) {
                settlementLeg.updateSuccessSettleStatus(cbsPostTransactionRequest.getReferenceNo(), settlementOftheDay,
                        responseMap.get("cbsTxnReferenceNo"));
            } else {
                retrySettlementPosting(cbsPostTransactionRequest, settlementOftheDay, retryCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to post cbs request. Exception: {}", e.getLocalizedMessage());
        }
    }

    private void retrySettlementPosting(CbsPostTransactionRequest cbsPostTransactionRequest, String settlementOftheDay,
            int retryCount) {
        log.info("retrying for transaction {}", cbsPostTransactionRequest.getReferenceNo());
        retryCount--;
        Response response = cbsTransactionRestClient.postTransaction(cbsPostTransactionRequest);
        HashMap<String, String> responseMap = response.readEntity(HashMap.class);
        log.info("CBS response for txn {} : {}", cbsPostTransactionRequest.getReferenceNo(),
                WriteAsJson.log(responseMap));
        if (responseMap.get("returnCode").equals("0")) {
            settlementLeg.updateSuccessSettleStatus(cbsPostTransactionRequest.getReferenceNo(), settlementOftheDay,
                    responseMap.get("cbsTxnReferenceNo"));
        } else {
            if (retryCount == 0) {
                settlementLeg.updateFailureSettleStatus(cbsPostTransactionRequest.getReferenceNo(), settlementOftheDay,
                        responseMap.get("responseMessage"));
            } else {
                retrySettlementPosting(cbsPostTransactionRequest, settlementOftheDay, retryCount);
            }
        }

    }

    private List<List<CBSSettlementTable>> getBatchs(List<CBSSettlementTable> unsettledList) {
        unsettledList = verifyUnsettledList(unsettledList);
        log.info("Running Settlement for {} transactions", unsettledList.size());
        List<List<CBSSettlementTable>> batchList = new ArrayList<>();
        for (int i = 0; i < unsettledList.size(); i++) {
            List<CBSSettlementTable> settlementAudits = new ArrayList<>();
            settlementAudits.add(unsettledList.get(i++));
            settlementAudits.add(unsettledList.get(i));
            batchList.add(settlementAudits);
        }
        log.info("No of requests for cbs settlement : {}", batchList.size());
        return batchList;
    }

    private List<CBSSettlementTable> verifyUnsettledList(List<CBSSettlementTable> unsettledList) {
        if (unsettledList.size() % 2 == 1) {
            log.error("Odd unsettled Transactions");
        }
        List<CBSSettlementTable> verifiedUnsettledList = new ArrayList<>();
        for (int i = 0; i < unsettledList.size(); i++) {
            if (i + 1 < unsettledList.size()) {
                if (unsettledList.get(i).getReferenceNo().equals(unsettledList.get(i + 1).getReferenceNo())) {
                    verifiedUnsettledList.add(unsettledList.get(i));
                    verifiedUnsettledList.add(unsettledList.get(++i));
                } else {
                    log.error("MisMatch in transactions, skiping transaction {}", unsettledList.get(i));
                }
            }
        }
        log.info("{} transaction to settle,{} has been skiped", verifiedUnsettledList.size(),
                unsettledList.size() - verifiedUnsettledList.size());
        return verifiedUnsettledList;
    }

    public String getDateIFTFile() {
        SimpleDateFormat IFT_FILE_CREATION_DATE_FORMATTER = new SimpleDateFormat("ddMMYYYY");
        Date DateIFTFile = new Date();
        return IFT_FILE_CREATION_DATE_FORMATTER.format(DateIFTFile);
    }

    private String getTransactionComment(CBSSettlementTable settlementAudit, String referenceNo,
            String settlementOftheDay, Boolean isEod) {
        String str = iftTransactionCommentFormat;
        return str
                .replace("@{tranCategory}",
                        isEod == false ? settlementTranCategoryInComment + settlementOftheDay
                                : eodTranCategoryInComment + settlementOftheDay)
                // what is this??
                .replace("@{cbsTransactionType}", settlementAudit.getTransactionType())
                .replace("@{DATE}", getDateIFTFile())
                .replace("@{cbsReferenceNo}", referenceNo);
    }
    // settlementIftRfuFormat=ZRFUT8#@{cbsReferenceNo}~TCMT#@{tranCategory}/@{cbsTransactionType}/@{cbsReferenceNo}/@{DATE}

    private String getSupportData(CBSSettlementTable settlementAudit, String referenceNo, String settlementOftheDay,
            Boolean isEod) {
        String supportData = settlementIftRfuFormat;
        return supportData.replace("@{cbsReferenceNo}", referenceNo)
                .replace("@{tranCategory}",
                        isEod == false ? settlementTranCategoryInComment + settlementOftheDay
                                : eodTranCategoryInComment + settlementOftheDay)
                .replace("@{cbsTransactionType}", settlementAudit.getTransactionType())
                .replace("@{DATE}", getDateIFTFile());
    }

    private List<CbsPostTransactionLegRequest> getAcctFundTransferlegs(List<CBSSettlementTable> unsettledList,
            String referenceNo, String settlementOftheDay, Boolean isEod) {
        List<CbsPostTransactionLegRequest> listofCbsPostTransactionLegRequests = new ArrayList<>();
        for (int i = 0; i < unsettledList.size(); i++) {
            CBSSettlementTable settlementAudit = unsettledList.get(i);
            String transactionComment = getTransactionComment(settlementAudit, referenceNo, settlementOftheDay, isEod);
            String supportData = getSupportData(settlementAudit, referenceNo, settlementOftheDay, isEod);
            CbsPostTransactionLegRequest cbsDRPostTransactionLegRequest = CbsPostTransactionLegRequest.builder()
                    .accountNumber(settlementAudit.getCreditGlAccount().toString())
                    .amount(settlementAudit.getPostingAmount())
                    .currency("INR")
                    .creditDebitFlag("D")
                    .transactionType(settlementAudit.getTransactionType())
                    .transactionComment(transactionComment.replace("@{accountNumber}",
                            settlementAudit.getDebitGlAccount().toString()))
                    .costCenter(costCentre)
                    .supportData(supportData)
                    .beneficiaryRefOrMmid(null)
                    .remitterMobile(null)
                    .beneficiaryAccountNo(null)
                    .beneficiaryIfsc(null)
                    .remarks(null)
                    .build();
            listofCbsPostTransactionLegRequests.add(cbsDRPostTransactionLegRequest);

            CbsPostTransactionLegRequest cbsCRPostTransactionLegRequest = CbsPostTransactionLegRequest.builder()
                    .accountNumber(settlementAudit.getDebitGlAccount().toString())
                    .amount(settlementAudit.getPostingAmount())
                    .currency("INR")
                    .creditDebitFlag("C")
                    .transactionType(settlementAudit.getTransactionType())
                    .transactionComment(transactionComment.replace("@{accountNumber}",
                            settlementAudit.getCreditGlAccount().toString()))
                    .costCenter(costCentre)
                    .supportData(supportData)
                    .beneficiaryRefOrMmid(null)
                    .remitterMobile(null)
                    .beneficiaryAccountNo(null)
                    .beneficiaryIfsc(null)
                    .remarks(null)
                    .build();
            listofCbsPostTransactionLegRequests.add(cbsCRPostTransactionLegRequest);
        }
        return listofCbsPostTransactionLegRequests;
    }

}
