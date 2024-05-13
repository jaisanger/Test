package org.knafl.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.knafl.impl.GLStatus;
import org.knafl.impl.MyEntity;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(org.knafl.Extension.DummyLdapServer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class UpdateGLStatusTests {
    @Inject
    GLStatus glstatus;

    @Test
    @Order(1)
    void updateActiveLdapGLsList() throws NamingException {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd '05:00:00.0'");
        String date = sdf.format(currentDate);
        assertEquals("[MyEntity [id=19, glNumber=100001, lastTransactionDate=" + date + "]]",
                glstatus.updateActiveLdapGLsList().toString());
        Log.info("---------" + glstatus.getActiveListWithTodayTransaction().toString());
    }

    @Test
    @Order(2)
    void testStatusOfGLwithTransactionsToday() throws NamingException {

        assertEquals("[MyEntity [id=19, glNumber=100001, lastTransactionDate=null]]",
                glstatus.getActiveListWithTodayTransaction().toString());
    }

    @Test
    void testInactiveStatusOfGLwithNoTransactions() throws NamingException {

        assertEquals("[MyEntity [id=465, glNumber=883335551297, lastTransactionDate=null]]",
                glstatus.getInactiveList().toString());
    }

    @Test
    void testDormantStatusOfGLwithNoTransactions() throws NamingException {

        assertEquals(
                "[MyEntity [id=5, glNumber=88333555127, lastTransactionDate=null], MyEntity [id=6, glNumber=88333555128, lastTransactionDate=null], MyEntity [id=7, glNumber=88333555129, lastTransactionDate=null], MyEntity [id=3, glNumber=88333555130, lastTransactionDate=null]]",
                glstatus.getDormantList().toString());
    }

    @Test
    void testDormant_InactiveStatusOfGLwithNoTransactions() throws NamingException {

        assertEquals(
                "[MyEntity [id=2, glNumber=123456, lastTransactionDate=null], MyEntity [id=42, glNumber=18000, lastTransactionDate=null], MyEntity [id=1, glNumber=20026925999, lastTransactionDate=null]]",
                glstatus.getActiveListNoTransaction().toString());
    }

    public void setGlstatus(GLStatus glstatus) {
        this.glstatus = glstatus;
    }

}
