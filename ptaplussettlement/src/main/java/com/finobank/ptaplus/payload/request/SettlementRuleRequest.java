package com.finobank.ptaplus.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SettlementRuleRequest {
    private String batchId;
}
