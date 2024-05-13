package com.finobank.ptaplus;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.resouce.SettlementResource;
import com.finobank.ptaplus.routes.UploadSettlementIFTRoute;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class minioTest extends CamelQuarkusTestSupport {

    @Inject
    UploadSettlementIFTRoute uploadSettlementIFTRoute;

    @Inject
    SettlementResource settlementResource;

    SettlementRequest settlementRequest;


    @BeforeEach
    void setup(){
        settlementRequest = new SettlementRequest();
        settlementRequest.setAppId("APPPTA");
        settlementRequest.setBatchId("batch2");
        settlementRequest.setUserClass("ptauser");
        settlementRequest.setApiExecutionMode(true);
        settlementRequest.setSettlementOfTheDay("1");
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return this.context;
    }   

    @Order(2)
    @Test
    public void SettlementP2mPaySuccess(){
        
        
        Response response = settlementResource.SettlementP2mPay(settlementRequest);
        // System.out.println("111111111111111111111111111111111111111111"+response.getStatusInfo());
        assertEquals(200,response.getStatus());
        assertEquals("OK",response.getStatusInfo().toString());
        // settlementResource.SettlementP2mPay(settlementRequest);
    }

}
