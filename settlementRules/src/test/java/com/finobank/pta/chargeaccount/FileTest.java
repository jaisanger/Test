package com.finobank.pta.chargeaccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.runtime.rule.Match;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.SingletonStore;

import com.finobank.pta.settlementConfig.BatchConfigRule;
import com.finobank.pta.settlementConfig.BatchConfigUnit;
import com.finobank.pta.settlementConfig.EODBatchConfigUnit;
import com.finobank.pta.settlementConfig.MyAgendaEventListener;
import com.finobank.pta.settlementConfig.PartnerSettlementRule;

import org.kie.api.definition.rule.Rule;
import io.quarkus.test.junit.QuarkusTest;

@ApplicationScoped
@QuarkusTest
public class FileTest {

    @Inject
    MyAgendaEventListener listener;

    PartnerSettlementRule partnerSettlementRule;
    BatchConfigRule batchConfigRule = new BatchConfigRule();
    BatchConfigUnit batchConfigUnit = new BatchConfigUnit();
    EODBatchConfigUnit eodBatchConfigUnit = new EODBatchConfigUnit();

    @Test
    @Order(1)
    public void testAfterMatchFired() {

        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn("TestRuleName");

        assertEquals("TestRuleName", rule.getName());

        Match match = mock(Match.class);
        when(match.getRule()).thenReturn(rule);

        AfterMatchFiredEvent event = mock(AfterMatchFiredEvent.class);
        when(event.getMatch()).thenReturn(match);

        listener.afterMatchFired(event);
    }

    @Test
    @Order(2)
    public void testBeforeMatchFired() {

        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn("TestRuleName");

        assertEquals("TestRuleName", rule.getName());

        Match match = mock(Match.class);
        when(match.getRule()).thenReturn(rule);

        BeforeMatchFiredEvent event = mock(BeforeMatchFiredEvent.class);
        when(event.getMatch()).thenReturn(match);

        listener.beforeMatchFired(event);
    }

    @Test
    @Order(3)
    public void test(){
        ArrayList<PartnerSettlementRule> list = new ArrayList<PartnerSettlementRule>();
        partnerSettlementRule = new PartnerSettlementRule();
        partnerSettlementRule.setPartnerId("partner11");
        list.add(partnerSettlementRule);

        BatchConfigRule bConfigrule = new BatchConfigRule("batch2", null, list);
        batchConfigRule.addPartnerSettlementRules("partner11");
        batchConfigRule.setPartnerSettlementRules(list);
        batchConfigRule.getPartnerSettlementRules();
        batchConfigRule.setbatchId("batch2");
        batchConfigRule.getbatchId();

        assertEquals("batch2", batchConfigRule.getbatchId());
        assertEquals(list, batchConfigRule.getPartnerSettlementRules());
        
    }

    @Test
    @Order(4)
    public void test1(){
        batchConfigUnit.getClass();
        eodBatchConfigUnit.getClass();
    }
}
