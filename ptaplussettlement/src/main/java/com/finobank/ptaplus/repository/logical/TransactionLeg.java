package com.finobank.ptaplus.repository.logical;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.finobank.ptaplus.repository.logical.model.TransactionTable;
import com.google.protobuf.TextFormat.ParseException;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
@Transactional
public class TransactionLeg implements PanacheRepository<TransactionTable> {

    @ConfigProperty(name = "unSettledTransactionQuery")
    String unSettledTransactionQuery;
    @ConfigProperty(name = "updateTransactionQuery")
    String updateTransactionQuery;

    @Inject
    EntityManager entityManager;

    public List<TransactionTable> getUnsettledTransactions(String partnerGl, String thresholdamt, Date createdDate) {
        String stringquery = unSettledTransactionQuery.replace("@{partnerGl}", partnerGl)
                .replace("@{thramount}", thresholdamt).replace("@{settleDate}", createdDate.toString());
        try {
            return createFromRow((List<Object[]>) entityManager.createNativeQuery(stringquery).getResultList());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<TransactionTable> getTransactionByRefNo(String txn_ref_no) {
        PanacheQuery<TransactionTable> query = find("select f from TransactionTable f where f.referenceNo = ?1",
                txn_ref_no);
        return query.list();
    }

    @Transactional
    public void updateSettledStatus(String partnerGl, String thresholdamt, Date createdDate, String settlementId, Boolean isEod) {

        String updatestatusquery = updateTransactionQuery
                .replace("{@settlementTime}", Calendar.getInstance().getTime().toString())
                .replace("{@settlementId}", settlementId)
                .replace("{@partnerGl}", partnerGl)
                .replace("{@thresholdTime}", createdDate.toString())
                .replace("{@thresholdAmount}", thresholdamt)
                .replace("{@isEod}", String.valueOf(isEod));
        Query query = entityManager.createNativeQuery(updatestatusquery);
        query.executeUpdate();
    }

    private List<TransactionTable> createFromRow(List<Object[]> row)
            throws ParseException, NumberFormatException, java.text.ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        List<TransactionTable> unsettledTransactions = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            TransactionTable table = new TransactionTable();
            table = TransactionTable.builder()
                    .referenceNo(row.get(i)[0].toString())
                    .createdAt(sdf.parse(row.get(i)[1].toString()))
                    .postingAmount(new BigDecimal(row.get(i)[2].toString()))
                    .build();
            unsettledTransactions.add(table);
        }
        return unsettledTransactions;
    }

}
