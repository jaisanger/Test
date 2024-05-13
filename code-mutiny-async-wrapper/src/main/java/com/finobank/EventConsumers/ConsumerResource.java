package com.finobank.EventConsumers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hibernate.persister.entity.mutation.EntityTableMapping;

import com.finobank.pojo.SettlementWrapperPayload;
import com.finobank.pojo.SettlementWrapperRequest;
import com.finobank.service.SettlementResource;

import io.agroal.api.AgroalDataSource;
import io.quarkus.hibernate.orm.runtime.dev.HibernateOrmDevInfo.Query;
import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

public class ConsumerResource {

    @Inject
    AgroalDataSource dataSource;

    @RestClient
    SettlementResource service;

    SettlementWrapperPayload settlementWrapperPayload;

    @Inject
    EventBus bus;

    @ConfigProperty(name="batchSequence")
    String batchSequence;

    @ConfigProperty(name="eodSequence")
    String eodSequence;

    @ConsumeEvent(value = "settlebatches",blocking=true)
    String settleAllBatch(SettlementWrapperRequest settlementWrapperRequest) {
        if(settlementWrapperRequest.getBatchId().isEmpty()){
            Log.error("Null batchIds at Batch Id field of request");
        }
        String settlementoftheday=String.valueOf(getNextSequenceValue(batchSequence));
        Log.info("here value "+settlementoftheday);
        for(String batchId: settlementWrapperRequest.getBatchId()){
            settlementWrapperPayload=new SettlementWrapperPayload(batchId, settlementWrapperRequest);
            settlementWrapperPayload.setSettlementOfTheDay(settlementoftheday);
            bus.requestAndForget("settlebatch", settlementWrapperPayload);
        }
        return "Completed Batches";
    }

    @ConsumeEvent(value = "settleEOD",blocking=true)
    String settleeod(SettlementWrapperRequest settlementWrapperRequest) {


        if(settlementWrapperRequest.getBatchId().isEmpty()){
            Log.error("Null batchIds at Batch Id field of request");
        }
        String settlementoftheday=String.valueOf(getNextSequenceValue(eodSequence));
        Log.info("here value "+settlementoftheday);
        for(String batchId: settlementWrapperRequest.getBatchId()){
            settlementWrapperPayload=new SettlementWrapperPayload(batchId, settlementWrapperRequest);
            settlementWrapperPayload.setSettlementOfTheDay(settlementoftheday);
            bus.requestAndForget("settleeod", settlementWrapperPayload);
        } 
        resetSequenceValue(batchSequence);
        return "Completed EOD";
    }

    private void resetSequenceValue(String sequenceName) {
        try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
        String sql = "ALTER SEQUENCE " + sequenceName + " RESTART ";
        if(statement.executeUpdate(sql)> 0){
            return ;
        }
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            throw new RuntimeException("Error while fetching sequence value: " + e.getMessage(), e);
        }
    }

    @ConsumeEvent(value = "settlebatch",blocking=true)
    String settleBatch(SettlementWrapperPayload settlementWrapperPayload) {
        Log.info("payload to External Request: "+settlementWrapperPayload);
        service.settlebatch(settlementWrapperPayload);
        return "complete"; 
    }

    @ConsumeEvent(value = "settleeod",blocking=true)
    String settleEOD(SettlementWrapperPayload settlementWrapperPayload) {
        Log.info("payload to External Request: "+settlementWrapperPayload);
        service.settleeod(settlementWrapperPayload);
        return "complete"; 
    }

    @Transactional
    public Long getNextSequenceValue(String sequenceName) {
        // try{
        //  jakarta.persistence.Query query =entityManager.createNativeQuery("SELECT nextval('set_daily_settlement_seq_id')");
        //     Object object =query.getSingleResult();
        //     System.out.println(object);
        //     System.out.println(object.toString());
        // }
        // catch(Exception e){

        //     System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        //     e.printStackTrace();
        //     System.out.println("\n\nGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG\n");
        //     throw new RuntimeException("Error while fetching sequence value: " + e.getMessage(), e);
        // }


        // return 6L;

        try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
        // String sql = "SELECT nextval('" + sequenceName + "')";
        String sql ="SELECT nextval('set_daily_settlement_seq_id')";
        System.out.println(sql);
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println(resultSet);
        System.out.println(resultSet.toString());
        System.out.println(resultSet.getFetchSize());
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new IllegalStateException("Failed to retrieve the next sequence value.");
        }
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            throw new RuntimeException("Error while fetching sequence value: " + e.getMessage(), e);
        }
    }
}
