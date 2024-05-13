package com.finobank.ptaplus;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.finobank.ptaplus.SettlementUtils.CorelationId;
import com.finobank.ptaplus.payload.CbsGlIftResult;
import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.request.SettlementWrapperRequest;
import com.finobank.ptaplus.payload.response.PartnerSettlementRule;
import com.finobank.ptaplus.payload.updateglresponse.UpdateGl;
import com.finobank.ptaplus.repository.logical.SettlementAuditRepository;
import com.finobank.ptaplus.repository.logical.SettlementLeg;
import com.finobank.ptaplus.repository.logical.model.SettlementAuditTable;
import com.finobank.ptaplus.repository.main.TransactionLegMain;
import com.finobank.ptaplus.resouce.SettlementResource;
import com.finobank.ptaplus.routes.CbsIFTResultPojoProcessor;
import com.finobank.ptaplus.service.CBSSettlementWrapper;

import io.quarkus.test.junit.QuarkusTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
public class fileTest {
    @Inject
    SettlementResource settlementResource;

    @Inject
    CorelationId corelationId;

    @Inject
    TransactionLegMain transactionLegMain;

    @Inject
    com.finobank.ptaplus.service.EodTransactions eodTransactions;

    @Inject 
    CBSSettlementWrapper cbsSettlementWrapper;

    @Inject
    SettlementLeg settlementLeg;

    @Inject
    SettlementAuditRepository settlementAuditRepository;

    @Inject
    CbsIFTResultPojoProcessor cbsIFTResultPojoProcessor;

    PartnerSettlementRule partnerSettlementRule;

    SettlementRequest settlementRequest;
    SettlementRequest settlementRequest1;
    SettlementRequest settlementRequest2;
    SettlementRequest settlementRequest3;
    SettlementRequest settlementRequest4;
    SettlementRequest settlementRequest5;

    @BeforeEach
    void setup() {
        settlementRequest = new SettlementRequest();
        settlementRequest.setAppId("APPPTA");
        settlementRequest.setBatchId("batch2");
        settlementRequest.setUserClass("ptauser");
        settlementRequest.setApiExecutionMode(true);
        settlementRequest.setSettlementOfTheDay("1");

        settlementRequest5 = new SettlementRequest();
        settlementRequest5.setAppId("APPPTA");
        settlementRequest5.setBatchId("batch1");
        settlementRequest5.setUserClass("ptauser");
        settlementRequest5.setApiExecutionMode(false);
        settlementRequest5.setSettlementOfTheDay("1");

        settlementRequest4 = new SettlementRequest();
        settlementRequest4.setAppId("APPPTA");
        settlementRequest4.setBatchId("batch3");
        settlementRequest4.setUserClass("ptauser");
        settlementRequest4.setApiExecutionMode(true);
        settlementRequest4.setSettlementOfTheDay("1");

        settlementRequest1 = new SettlementRequest();
        settlementRequest1.setAppId("APPPTA");
        settlementRequest1.setBatchId("batch2");
        settlementRequest1.setUserClass("ptauser");
        // settlementRequest.setApiExecutionMode(true);

        settlementRequest2 = new SettlementRequest();
        settlementRequest2.setAppId("APPPTA");
        settlementRequest2.setBatchId("batch2");
        settlementRequest2.setUserClass("ptauser");
        settlementRequest2.setApiExecutionMode(true);
        settlementRequest2.setSettlementOfTheDay("1");

        settlementRequest3 = new SettlementRequest();
        settlementRequest3.setAppId("APPPTA");
        settlementRequest3.setBatchId("batch2");
        settlementRequest3.setUserClass("ptauser");
        settlementRequest3.setApiExecutionMode(false);
        settlementRequest3.setSettlementOfTheDay("1");

        partnerSettlementRule = new PartnerSettlementRule();
        partnerSettlementRule.setPtaPartnerGl("88333555211");
        partnerSettlementRule.setPartnerId("partner11");

    }

    @Order(1)
    @Test
    public void SettlementP2mPayRequestNull() {
        Response response = settlementResource.SettlementP2mPay(null);
        // System.out.println("2222222222222222222222222222222222222"+response.getStatus());
        assertEquals(200, response.getStatus());
    }

    @Order(2)
    @Test
    public void SettlementP2mPaySuccess() {
        Response response = settlementResource.SettlementP2mPay(settlementRequest);
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());
    }

    @Order(3)
    @Test
    public void SettlementP2mPayInvalidSettlementRequest() {
        Response response = settlementResource.SettlementP2mPay(settlementRequest1);
        // System.out.println("33333333333333333333333333333333333333333333333333"+response.getStatusInfo());
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());
    }

    @Order(4)
    @Test
    public void SettlementP2mPaySuccess1() {
        Response response = settlementResource.SettlementP2mPay(settlementRequest2);
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());
    }

    // @Order(5)
    // @Test
    // public void SettlementP2mPaySuccess5() {
    //     Response response = settlementResource.SettlementP2mPay(settlementRequest3);
    //     // System.out.println("111111111111111111111111111111111111111111"+response.getStatusInfo());
    //     assertEquals(200, response.getStatus());
    //     assertEquals("OK", response.getStatusInfo().toString());
    //     // settlementResource.SettlementP2mPay(settlementRequest);
    // }

    @Order(6)
    @Test
    public void EodSettlementCbsSuccess() throws InterruptedException {
        Thread.sleep(10000);
        Response response = settlementResource.EodSettlementP2mPay(settlementRequest4);
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());
    }
    
    @Order(7)
    @Test
    public void EodSettlementCbsExceptionE() {
        Response response = settlementResource.EodSettlementP2mPay(settlementRequest1);
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());
    }

    @Test
    public void SettlementCbs() {
        Response response = settlementResource.SettlementCbs(settlementRequest);
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());

    }

    

    // @Test
    // public void EodTransactions(){
    // eodTransactions = new com.finobank.ptaplus.service.EodTransactions();
    // eodTransactions.perform(settlementRequest, partnerSettlementRule);

    // }

    

    @Order(9)
    @Test
    public void SettlementP2mPaySuccess13() {
        Response response = settlementResource.SettlementP2mPay(settlementRequest5);
        // System.out.println("111111111111111111111111111111111111111111"+response.getStatusInfo());
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());
        // settlementResource.SettlementP2mPay(settlementRequest);
    }

    @Order(6)
    @Test
    public void EodSettlementCbsSuccess123() throws InterruptedException {
        Thread.sleep(10000);
        Response response = settlementResource.EodSettlementP2mPay(settlementRequest3);
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getStatusInfo().toString());
    }

    @Order(7)
    @Test
    public void UpdateGlPojo(){
        UpdateGl updategl = new UpdateGl();
        UpdateGl up = new UpdateGl(0, 0, null);
        updategl.setId(0);
        updategl.setGlNumber(0);
        updategl.setLastTransactionDate(null);
        assertEquals(0,updategl.getId());
        assertEquals(0, updategl.getGlNumber());
        assertEquals(null,updategl.getLastTransactionDate());
    }

    @Order(8)
    @Test
    public void SettlementRequestpojo(){
        ArrayList<String> list = new ArrayList<>();
        list.add("batch1");
        SettlementWrapperRequest wrapperRequest = new SettlementWrapperRequest();        
        wrapperRequest.setBatchId(list);
        wrapperRequest.setAppId("123");
        SettlementRequest settRequest = new SettlementRequest(wrapperRequest);
    }

    @Order(9)
    @Test
    public void CorelationId(){
        CbsGlIftResult cbsGlIftResult = new CbsGlIftResult();
        cbsGlIftResult.toCsvRecord();
    }

    @Order(10)
    @Test
    public void updateSettledStatus(){
        try{
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // Date specificDate = sdf.parse("2024-01-03"); 
            // LocalDate myObj = LocalDate.now();
            Date date = new Date();
            transactionLegMain.getUnsettledTransactions("1245","1234",date);
        }catch(Exception E){

        }
    }

    @Order(12)
    @Test
    public void updateSettledStatus1(){
        Date date = new Date();
        transactionLegMain.updateSettledStatus("1234567890", "123", date, "1234567");
        
    }

    @Order(13)
    @Test
    public void SettlementLegUpdateSuccesSettlementStatus(){
        settlementLeg.updateSuccessSettleStatus("12345", "1234", "1234", "1234");
        settlementLeg.updateSuccessSettleStatus("1234", "1234", "1234");
        settlementLeg.updateFailureSettleStatus("1234", "1234", "1234");
        settlementLeg.updateFailureSettleStatus("1234", "1234", "1234", "1234");
    }

    @Order(14)
    @Test
    public void settlementAuditRepository(){
        Date date = new Date();
        SettlementAuditTable settlementAuditTable = new SettlementAuditTable();
        settlementAuditTable.setBatchId("batch2");
        settlementAuditTable.setSuccessCount(1);
        settlementAuditTable.setFailedCount(2);
        settlementAuditTable.setRemarks("1234");
        settlementAuditTable.setSettlementId("1234567890");
        settlementAuditTable.setResultFileName("jai");
        settlementAuditTable.setDateStarted(date);
        settlementAuditRepository.addFileResponse(settlementAuditTable);
    }

    @Order(11)
    @Test
    public void performEodTest(){
        {

        cbsSettlementWrapper.performEod(settlementRequest);
        // cbsIFTResultPojoProcessor.process(null);
    }
}
}
