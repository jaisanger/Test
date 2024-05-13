package com.finobank.ptaplus.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private @NotNull(message = "Transaction Id must not be null") @NotBlank(message = "Transaction Id must not be blank") 
    String uniqueTransactionId;
    private String userClass;
    private @NotNull(message = "Transaction category must not be null") @NotBlank(message = "Transaction category must not be blank") 
    String transCategory;
    private String appId;
    private Long iftFileId;
    private String referenceNo;
    private String reversalFlag;
    private String chargeOverride;
    private String analysisFlag;
    private String isClubbed;
    private String isInclusive;
    private String valueDate;
    private Long initiatingBranchCode;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("partner_id")
    private String partnerId;
    @JsonProperty("merchants_id")
    public String merchantsId;
    private @Valid List<FundTransferLegRequest> acctFundTransferLegs = new ArrayList();
    // private @Valid List<ChargeLegRequest> chargeLegs = new ArrayList();
    // private @Valid List<TaxLegRequest> taxLegs = new ArrayList();

}
