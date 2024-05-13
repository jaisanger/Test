package com.finobank.ptaplus.routes;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.finobank.ptaplus.SettlementUtils.SettlementStages;
import com.finobank.ptaplus.payload.CbsGlIftResult;
import com.finobank.ptaplus.repository.logical.SettlementAuditRepository;
import com.finobank.ptaplus.repository.logical.SettlementLeg;
import com.finobank.ptaplus.repository.logical.model.SettlementAuditTable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CbsIFTResultPojoProcessor implements Processor {

    @Inject
    SettlementLeg settlementLeg;

    @Inject
    SettlementAuditRepository settlementAuditRepository;

    @ConfigProperty(name ="referenceJoiner")
    String referenceJoiner;


    @ActivateRequestContext
    @Override
    public void process(Exchange exchange) throws Exception {
        String resultfileName= (String) exchange.getIn().getHeader("CamelFileName");
        String fileName=resultfileName.substring(0,resultfileName.length() -4);
        SettlementAuditTable settlementAuditTable=settlementAuditRepository.getBatchId(fileName);
        settlementAuditRepository.updateStatus(settlementAuditTable.getBatchId(), SettlementStages.IFT_FILE_PROCESSING_RUNNING.toString(),settlementAuditTable.getSettlementId() );
        List<CbsGlIftResult> resultList = new ArrayList<>();
        try {
            @SuppressWarnings("unchecked")
            List<CbsGlIftResult> retResults = (List<CbsGlIftResult>) exchange.getIn().getBody();
            resultList.addAll(retResults);
        } catch (Exception exception) {
            log.error(exception.getLocalizedMessage());
            try {
                CbsGlIftResult singleResult = exchange.getIn().getBody(CbsGlIftResult.class);
                resultList.add(singleResult);
            } catch (Exception e) {
                log.error(exception.getLocalizedMessage());
            }
        }
        log.info("CorrelationId={}|Processor=CbsIFTResultPojoProcessor|List<CbsGlIftResult>=\n{}", null,
                resultList);

        int successPartner=0;
        int failedPartners=0;
        for (int i = 0; i < resultList.size(); i++) {
            String transactionComment = resultList.get(i).getCreditTransactionComment();
            String[] trancomment = transactionComment.split("/")[1].split(referenceJoiner);
            String transaction_ref_no=trancomment[0];
            String postingPair=trancomment[1];
            if(resultList.get(i).getReturnCode().equals("0")){
            settlementLeg.updateSuccessSettleStatus(transaction_ref_no,settlementAuditTable.getSettlementId(),transaction_ref_no+postingPair,postingPair);
            log.info("Updating transaction {} with posting pair {} status to settled.",transaction_ref_no, postingPair);
            successPartner++;
            }
            else{
                log.error("Transaction {} with posting pair {} failed due to {}", transaction_ref_no,postingPair,resultList.get(i).getFailureReason());
                settlementLeg.updateFailureSettleStatus(transaction_ref_no.split(referenceJoiner)[0],settlementAuditTable.getSettlementId(),resultList.get(i).getFailureReason(),postingPair);
                failedPartners++;
            }
        }
        SettlementAuditTable settlementAuditTable2 = SettlementAuditTable.builder()
                .resultFileName(resultfileName)
                .successCount(successPartner)
                .failedCount(failedPartners)
                .remarks(SettlementStages.IFT_FILE_PROCESSING_SUCCEEDED.toString())
                .batchId(settlementAuditTable.getBatchId())
                .settlementId(settlementAuditTable.getSettlementId()).build();
        settlementAuditRepository.addFileResponse(settlementAuditTable2);
        log.info("Updated all the transaction status.");
    }

}
