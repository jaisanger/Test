package com.finobank.pta.settlementConfig;

import java.util.ArrayList;
import java.util.List;


public class BatchConfigRule {
    
    private String batchId;
    private List<PartnerSettlementRule> partnerSettlementRules;

    public BatchConfigRule(String batchId, List<String> partnerId,
            List<PartnerSettlementRule> partnerSettlementRules) {
        this.batchId = batchId;
        this.partnerSettlementRules = partnerSettlementRules;
    }
    public BatchConfigRule() {
        partnerSettlementRules= new ArrayList<>();
    }
    public String getbatchId() {
        return batchId;
    }
    public void setbatchId(String batchId) {
        this.batchId = batchId;
    }
    public List<PartnerSettlementRule> getPartnerSettlementRules() {
        return partnerSettlementRules;
    }
    public void setPartnerSettlementRules(List<PartnerSettlementRule> partnerSettlementRules) {
        this.partnerSettlementRules = partnerSettlementRules;
    }  
    public PartnerSettlementRule addPartnerSettlementRules(String partnerId){
        PartnerSettlementRule partnerSettlementRule= new PartnerSettlementRule(partnerId);
        this.partnerSettlementRules.add(partnerSettlementRule);
        return partnerSettlementRule;
    }


    

}
