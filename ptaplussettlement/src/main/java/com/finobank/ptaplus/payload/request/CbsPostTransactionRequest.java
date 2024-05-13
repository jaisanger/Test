package com.finobank.ptaplus.payload.request;

import lombok.*;
import lombok.Builder.Default;

import java.util.ArrayList;
import java.util.List;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CbsPostTransactionRequest {

    private String appId;
    private String valueDate;
    private int analysisFlag;
    private String reversalFlag;
    private int isInclusive;
    private int isClubbed;
    private String referenceNo;
    @Default
    private List<CbsPostTransactionLegRequest> acctFundTransferLegs = new ArrayList<>();

}

