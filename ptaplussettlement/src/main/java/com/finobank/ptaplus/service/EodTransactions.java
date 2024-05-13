package com.finobank.ptaplus.service;

import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.response.PartnerSettlementRule;
import com.finobank.ptaplus.payload.response.SettlementResponse;
import com.finobank.ptaplus.repository.logical.GeneralLedgerBalanceRepository;
import com.finobank.ptaplus.repository.logical.TransactionLeg;
import com.finobank.ptaplus.repository.logical.model.GeneralLedgerBalance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class EodTransactions {

    @Inject
    TransactionUtils transactionUtils;

    @Inject
    TransactionLeg transactionLeg;
    @Inject
    GeneralLedgerBalanceRepository generalLedgerBalanceRepository;

    public Response perform(SettlementRequest settlementRequest, PartnerSettlementRule partnerSettlementRule) {
        List<GeneralLedgerBalance> gl_balance = generalLedgerBalanceRepository.getGlBalance(partnerSettlementRule.getPartnerId());
        if (gl_balance.size() == 0) {
            log.error("partnerGl {} is not found.", partnerSettlementRule.getPartnerId());
            return Response.status(404).build();
        }
        if (gl_balance.get(0).availableBalance().compareTo(BigDecimal.ZERO)==0) {
            log.error("partnerGl {} doesn't have any transaction.", partnerSettlementRule.getPartnerId());
            return Response.status(202).build();
        }
        log.info("running EOD settlement for partnerGl {}", partnerSettlementRule.getPartnerId());
        transactionUtils.createPostTransaction(settlementRequest, partnerSettlementRule, gl_balance.get(0).availableBalance(),
                true);

        return Response.ok().build();
    }

    public void updateTheTransactionStatus(String partnerGl) {
        // transactionLeg.updateSettlementFlag(partnerGl);

    }

}
