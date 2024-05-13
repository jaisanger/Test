package com.finobank.ptaplus.repository.logical;

import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.finobank.ptaplus.repository.logical.model.CBSSettlementTable;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class SettlementLeg implements PanacheRepository<CBSSettlementTable> {

    @ConfigProperty(name = "unsettledSettlementQuery")
    String unsettledSettlementQuery;
    @ConfigProperty(name = "updateSuccessCBSSettlementStatusQuery")
    String updateSuccessCBSSettlementStatusQuery;
    @ConfigProperty(name ="updateFailerCBSSettlementStatusQuery")
    String updateFailerCBSSettlementStatusQuery;

    @Inject
    EntityManager entityManager;

    public List<CBSSettlementTable> getUnsettledTransactions(String partnerGl) {
        PanacheQuery<CBSSettlementTable> query = find(unsettledSettlementQuery,Long.parseLong(partnerGl));
        return query.list();
    }
    // Query query2 = session.createQuery("from Employee e where e.id in (:ids)").setParameterList("ids", query1.list());

    @Transactional
    public void addSettlementLeg(CBSSettlementTable settlementAudit) {
        entityManager.persist(settlementAudit);

    }

    @Transactional
    public void updateSuccessSettleStatus(String transaction_refno, String settlementId, String cbsReferenceNo,String postingPair) {
        try{String stringquery=updateSuccessCBSSettlementStatusQuery.replace("@{txn_ref_No}", transaction_refno).replace("{@SettledTime}",
        Calendar.getInstance().getTime().toString()).replace("{@settlementId}", settlementId)
                .replace("{@cbsReferenceNo}", cbsReferenceNo)+ " and posting_pair="+postingPair;
        System.out.println(stringquery);
        Query query = entityManager.createNativeQuery(stringquery);
        query.executeUpdate();
    }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public void updateFailureSettleStatus(String transaction_refno, String settlementId,String cbsResponse,String postingPair) {
        Query query = entityManager.createNativeQuery(
            updateFailerCBSSettlementStatusQuery.replace("@{txn_ref_No}", transaction_refno).replace("{@SettledTime}",
                        Calendar.getInstance().getTime().toString()).replace("{@settlementId}", settlementId)
                                .replace("{@cbsresponse}", cbsResponse)+ " and posting_pair="+postingPair);
            System.out.println(query.toString());
            query.executeUpdate();
    }
    @Transactional
    public void updateSuccessSettleStatus(String transaction_refno, String settlementId, String cbsReferenceNo) {
        Query query = entityManager.createNativeQuery(
                updateSuccessCBSSettlementStatusQuery.replace("@{txn_ref_No}", transaction_refno).replace("{@SettledTime}",
                        Calendar.getInstance().getTime().toString()).replace("{@settlementId}", settlementId)
                                .replace("{@cbsReferenceNo}", cbsReferenceNo));
        query.executeUpdate();
    }

    @Transactional
    public void updateFailureSettleStatus(String transaction_refno, String settlementId,String cbsResponse) {
        Query query = entityManager.createNativeQuery(
            updateFailerCBSSettlementStatusQuery.replace("@{txn_ref_No}", transaction_refno).replace("{@SettledTime}",
                        Calendar.getInstance().getTime().toString()).replace("{@settlementId}", settlementId)
                                .replace("{@cbsresponse}", cbsResponse));
        query.executeUpdate();
    }

}
