package com.finobank.ptaplus.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.response.PartnerSettlementRule;
import com.finobank.ptaplus.payload.response.SettlementResponse;
import com.finobank.ptaplus.repository.logical.TransactionLeg;
import com.finobank.ptaplus.repository.logical.model.TransactionTable;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class SettlementTransactions {

    @Inject
    TransactionUtils transactionUtils;
    @Inject
    TransactionLeg transactionLeg;

    public Response perform(SettlementRequest settlementRequest, PartnerSettlementRule partnerSettlementRule,Date settlementDateforParter,Boolean isEod) {
        List<TransactionTable> unsettledTransaction = getUnsettledTransactions(partnerSettlementRule,settlementDateforParter);
        BigDecimal settleAmount = calculateSettleAmmount(unsettledTransaction);
        log.info("Amount to settle : {}",settleAmount);
        if (settleAmount.compareTo(BigDecimal.ZERO)==1) {
            try{
                transactionUtils.createPostTransaction(settlementRequest, partnerSettlementRule, settleAmount, isEod);
            }
            catch(Exception e){
                return Response.status(500).entity(e.toString()).build();
            }
        } else {
            return Response.status(202).entity(new SettlementResponse("1","No transaction to Settle")).build();
        }
        return Response.ok().build();
    }


    public List<TransactionTable> getUnsettledTransactions(PartnerSettlementRule partnerSettlementRule,Date settlementDateforParter) {
        List<TransactionTable> unsettledTransactions =  transactionLeg.getUnsettledTransactions(partnerSettlementRule.getPtaPartnerGl(),partnerSettlementRule.getThresholdAmt(),settlementDateforParter);
        if (unsettledTransactions.size() == 0) {
            log.error("No transaction to settle for partnerGl {}.", partnerSettlementRule.getPtaPartnerGl());
        } else {
            log.info("Running settlement for {} transactions in partnerGl {}.", unsettledTransactions.size(),partnerSettlementRule.getPtaPartnerGl());
        }
        return unsettledTransactions;
    }

    public BigDecimal calculateSettleAmmount(List<TransactionTable> unsettledTransactions) {
        BigDecimal settleAmount = new BigDecimal("0");
        for (int i = 0; i < unsettledTransactions.size(); i++) {
                settleAmount = settleAmount.add(unsettledTransactions.get(i).getPostingAmount());
        }
        return settleAmount;
    }
}
