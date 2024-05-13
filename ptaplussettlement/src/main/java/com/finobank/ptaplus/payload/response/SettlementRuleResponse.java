package com.finobank.ptaplus.payload.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementRuleResponse {
    private String batchId;
    private List<PartnerSettlementRule> partnerSettlementRules;
}
