package org.acme;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.finobank.GreetingResource;
import com.finobank.pojo.SettlementWrapperRequest;

import io.agroal.api.AgroalDataSource;
import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class fileTest {
    @Inject
    GreetingResource greetingResource;

    @Inject
    AgroalDataSource dataSource;

    SettlementWrapperRequest settlementWrapperRequest;
    SettlementWrapperRequest settlementWrapperRequest1;

    @BeforeEach
    void setup() throws SQLException{
        

        ArrayList<String> list = new ArrayList<>();
        list.add("batch2");
        // settlementWrapperRequest = new SettlementWrapperRequest();
        settlementWrapperRequest = new SettlementWrapperRequest();
        settlementWrapperRequest.setAppId("1234");
        settlementWrapperRequest.setBatchId(list);
        settlementWrapperRequest.setApiExecutionMode(true);
        // settlementWrapperRequest.set
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("batch2");
        settlementWrapperRequest1 = new SettlementWrapperRequest();
        settlementWrapperRequest1.setAppId("1234");
        settlementWrapperRequest1.setBatchId(list1);
        settlementWrapperRequest1.setApiExecutionMode(true);
    }

    @Order(2)
    @Test
    public void jai() throws InterruptedException {
        Thread.sleep(10000);
        greetingResource.requestBatch(settlementWrapperRequest);
    }

    @Order(1)
    @Test
    public void jai1() throws InterruptedException {
        greetingResource.requestEOD(settlementWrapperRequest1);
    }
}
