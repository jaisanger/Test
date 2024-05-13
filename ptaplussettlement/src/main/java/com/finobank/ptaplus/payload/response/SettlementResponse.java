package com.finobank.ptaplus.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class SettlementResponse {
    private String responseCode;
    private String responseMessage;    
}
