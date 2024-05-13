package com.finobank.ptaplus.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.finobank.ptaplus.SettlementUtils.CorelationId;
import com.finobank.ptaplus.SettlementUtils.WriteAsJson;
import com.finobank.ptaplus.client.PostTransaction;
import com.finobank.ptaplus.payload.request.FundTransferLegRequest;
import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.request.TransactionRequest;
import com.finobank.ptaplus.payload.response.PartnerSettlementRule;
import com.finobank.ptaplus.repository.logical.SettlementLeg;
import com.finobank.ptaplus.repository.logical.TransactionLeg;
import com.finobank.ptaplus.repository.main.TransactionLegMain;
import com.finobank.ptaplus.repository.logical.model.CBSSettlementTable;
import com.finobank.ptaplus.repository.logical.model.TransactionTable;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TransactionUtils {

        @Inject
        TransactionLeg transactionLeg;

        @Inject
        TransactionLegMain transactionMainLeg;

        @Inject
        CorelationId corelationId;

        @Inject
        SettlementLeg settlementLeg;

        @RestClient
        PostTransaction postTransaction;

        @ConfigProperty(name = "settlementTransactionCategory")
        String settlementTransactionCategory;
        @ConfigProperty(name = "eodTransactionCategory")
        String eodTransactionCategory;

        @Transactional
        public void createPostTransaction(SettlementRequest settlementRequest,
                        PartnerSettlementRule partnerSettlementRule,
                        BigDecimal settleAmount, Boolean isEod) {
                TransactionRequest transactionRequest = createPostTransactionRequest(
                                settlementRequest, partnerSettlementRule, settleAmount, isEod);
                performTransaction(transactionRequest);
                addtransactionToCBSTable(transactionRequest.getReferenceNo(),isEod);
        }

        private void performTransaction(TransactionRequest transactionRequest) {
                log.info("Requesting transaction with payload {}.", transactionRequest.toString());
                try {
                        Response response = postTransaction.postTransaction(transactionRequest);
                        if (response.getStatus() != 200) {
                                log.error("Response from /transaction {}", response.getEntity());
                                throw new WebApplicationException();
                        }
                        log.info("Response from transaction {}", response.getEntity());
                } catch (Exception e) {
                        log.error("Error occured during performing transaction", e.toString());
                        throw new WebApplicationException("Error During performing transaction.");
                }
        }

        @Transactional
        public void addtransactionToCBSTable(String trn_ref_no,boolean isEod) {
                // GET DATA FROM MAIN TABLE
                List<TransactionTable> transactions = transactionMainLeg.getTransactionByRefNo(trn_ref_no);
                if (transactions.size() == 0) {
                        log.error("failed to add transaction with reference No {} to cbs_settlement_table", trn_ref_no);
                        throw new WebApplicationException("couldn't add the performed transaction to cbs table.");
                }
                for (int i = 0; i < transactions.size(); i++) {
                        CBSSettlementTable settlementTable = new CBSSettlementTable(transactions.get(i),isEod);
                        log.info("adding transaction with {} in settlement table.", settlementTable);
                        settlementLeg.addSettlementLeg(settlementTable);
                }
        }

        private TransactionRequest createPostTransactionRequest(SettlementRequest settlementRequest,
                        PartnerSettlementRule partnerSettlementRule,
                        BigDecimal settleAmount, Boolean isEod) {
                log.info("Creating Transaction Request for P2M.");
                TransactionRequest transactionRequest = TransactionRequest.builder()
                                .uniqueTransactionId(corelationId.getUniqueTransactionId())
                                .userClass(settlementRequest.getUserClass())
                                .transCategory(isEod ? eodTransactionCategory : settlementTransactionCategory)
                                .appId(settlementRequest.getAppId())
                                .iftFileId(null)
                                .referenceNo(corelationId.getReferenceNo())
                                .reversalFlag("F")
                                .chargeOverride(null)
                                .analysisFlag(null)
                                .isClubbed(null)
                                .isInclusive(null)
                                .valueDate(null)
                                .initiatingBranchCode(null)
                                .userId(null)
                                .partnerId(null)
                                .merchantsId(null)
                                .acctFundTransferLegs(getFundTransferLegs(partnerSettlementRule, settleAmount, isEod))
                                .build();
                return transactionRequest;

        }

        private List<FundTransferLegRequest> getFundTransferLegs(PartnerSettlementRule partnerSettlementRule,
                        BigDecimal settleAmount, Boolean isEod) {
                List<FundTransferLegRequest> fundTransferLegRequests = new ArrayList<>();
                fundTransferLegRequests.addAll(
                                acctFundTransferLegspartnerToIntermediry(partnerSettlementRule, settleAmount, isEod));
                fundTransferLegRequests.addAll(acctFundTransferLegsIntermediryToPurchaseGl(partnerSettlementRule,
                                settleAmount, isEod));
                return fundTransferLegRequests;
        }

        private List<FundTransferLegRequest> acctFundTransferLegsIntermediryToPurchaseGl(
                        PartnerSettlementRule partnerSettlementRule,
                        BigDecimal settleAmount, Boolean isEod) {

                List<FundTransferLegRequest> fundTransferLegRequests = new ArrayList<>();
                FundTransferLegRequest fundTransferLegRequest1 = FundTransferLegRequest.builder()
                                .postingPair(2)
                                .creditDebitFlag("D")
                                .transactionType(isEod ? eodTransactionCategory : settlementTransactionCategory)
                                .trantype(null)
                                .postingAmount(settleAmount)
                                .accountNumber(Long.valueOf(partnerSettlementRule.getPtaIntermediaryGl()))
                                .amount(settleAmount)
                                .currency("INR")
                                .transactionComment(null)
                                .costCentre(null)
                                .supportData(null)
                                .transferLegsSupportData(null)
                                .beneficiaryRefOrMmid(null)
                                .remitterMobile(null)
                                .remitterMmid(null)
                                .beneficiaryAccountNo(null)
                                .beneficiaryIfsc(null)
                                .remarks(null)
                                .restricted(false)
                                .restrictionId(null)
                                .restrictionString(null)
                                .productId("0")
                                .transactionAllowed(true)
                                .legId(3)
                                .isAccount(false)
                                .build();
                FundTransferLegRequest fundTransferLegRequest2 = FundTransferLegRequest.builder()
                                .postingPair(2)
                                .creditDebitFlag("C")
                                .transactionType(isEod ? eodTransactionCategory : settlementTransactionCategory)
                                .trantype(null)
                                .postingAmount(settleAmount)
                                .accountNumber(Long.valueOf(partnerSettlementRule.getPtaPurchaseGl()))
                                .amount(settleAmount)
                                .currency("INR")
                                .transactionComment(null)
                                .costCentre(null)
                                .supportData(null)
                                .transferLegsSupportData(null)
                                .beneficiaryRefOrMmid(null)
                                .remitterMobile(null)
                                .remitterMmid(null)
                                .beneficiaryAccountNo(null)
                                .beneficiaryIfsc(null)
                                .remarks(null)
                                .restricted(false)
                                .restrictionId(null)
                                .restrictionString(null)
                                .productId("0")
                                .transactionAllowed(true)
                                .legId(4)
                                .isAccount(false)
                                .build();
                fundTransferLegRequests.add(0, fundTransferLegRequest1);
                fundTransferLegRequests.add(1, fundTransferLegRequest2);
                return fundTransferLegRequests;
        }

        private List<FundTransferLegRequest> acctFundTransferLegspartnerToIntermediry(
                        PartnerSettlementRule partnerSettlementRule,
                        BigDecimal settleAmount, Boolean isEod) {
                List<FundTransferLegRequest> fundTransferLegRequests = new ArrayList<>();
                FundTransferLegRequest fundTransferLegRequest1 = FundTransferLegRequest.builder()
                                .postingPair(1)
                                .creditDebitFlag("C")
                                .transactionType(isEod ? eodTransactionCategory : settlementTransactionCategory)
                                .trantype(null)
                                .postingAmount(settleAmount)
                                .accountNumber(Long.valueOf(partnerSettlementRule.getPtaIntermediaryGl()))
                                .amount(settleAmount)
                                .currency("INR")
                                .transactionComment(null)
                                .costCentre(null)
                                .supportData(null)
                                .transferLegsSupportData(null)
                                .beneficiaryRefOrMmid(null)
                                .remitterMobile(null)
                                .remitterMmid(null)
                                .beneficiaryAccountNo(null)
                                .beneficiaryIfsc(null)
                                .remarks(null)
                                .restricted(false)
                                .restrictionId(null)
                                .restrictionString(null)
                                .productId("0")
                                .transactionAllowed(true)
                                .legId(1)
                                .isAccount(false)
                                .build();
                FundTransferLegRequest fundTransferLegRequest2 = FundTransferLegRequest.builder()
                                .postingPair(1)
                                .creditDebitFlag("D")
                                .transactionType(isEod ? eodTransactionCategory : settlementTransactionCategory)
                                .trantype(null)
                                .postingAmount(settleAmount)
                                .accountNumber(Long.valueOf(partnerSettlementRule.getPtaPartnerGl()))
                                .amount(settleAmount)
                                .currency("INR")
                                .transactionComment(null)
                                .costCentre(null)
                                .supportData(null)
                                .transferLegsSupportData(null)
                                .beneficiaryRefOrMmid(null)
                                .remitterMobile(null)
                                .remitterMmid(null)
                                .beneficiaryAccountNo(null)
                                .beneficiaryIfsc(null)
                                .remarks(null)
                                .restricted(false)
                                .restrictionId(null)
                                .restrictionString(null)
                                .productId("0")
                                .transactionAllowed(true)
                                .legId(2)
                                .isAccount(false)
                                .build();
                fundTransferLegRequests.add(0, fundTransferLegRequest1);
                fundTransferLegRequests.add(1, fundTransferLegRequest2);
                return fundTransferLegRequests;
        }

}
