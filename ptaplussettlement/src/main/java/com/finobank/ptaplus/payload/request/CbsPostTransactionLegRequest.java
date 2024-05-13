package com.finobank.ptaplus.payload.request;


import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CbsPostTransactionLegRequest {

    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private String creditDebitFlag;
    private String transactionType;
    private String transactionComment;
    private String costCenter;
    private String supportData;
    private String beneficiaryRefOrMmid;
    private String beneficiaryMobile;
    private String remitterMobile;
    private String remitterMmid;
    private String beneficiaryAccountNo;
    private String beneficiaryIfsc;
    private String remarks;

}
