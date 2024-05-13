package com.finobank.ptaplus.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerSettlementRule {
    private String partnerId;
    private String ptaPartnerGl;
    private String ptaIntermediaryGl;
    private String ptaPurchaseGl;
    private String cbsPartnerGl;
    private String cbsIntermediaryGl;
    private String cbsPurchaseGl;
    private String costCenter;
    private String ptaTranType;
    private String cbsTranType;
    private String thresholdAmt;
    private String thresholdTime; 

}
