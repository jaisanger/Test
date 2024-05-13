package com.finobank.ptaplus.payload.request;

import java.util.List;

import com.finobank.ptaplus.payload.response.PartnerSettlementRule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchConfigRule {
    List<SettlementRuleRequest> settlementConfigRule;
    // PartnerSettlementRule partnerSettlementRule;
}
