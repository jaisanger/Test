package com.finobank.ptaplus.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;


@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FundTransferLegRequest {
    
    private Integer postingPair;

    @NotNull
    @Length(min = 1, max = 1,
            message = "Credit/Debit flag must only contain one character (C or D)")
    private String creditDebitFlag;


    private String transactionType;

    private String trantype;
    

    @NotNull(message = "Leg's postingAmount must not be null")
    @Digits(fraction = 2,
            integer = 10,
            message = "Leg's postingAmount should be a number with max of 10 digits and up to 2 decimal places")
    private BigDecimal postingAmount;

    @NotNull
    private Long accountNumber;

    @NotNull
    private BigDecimal amount;

    private String currency;

    private String transactionComment;

    private String costCentre;

    private String supportData;

    private String transferLegsSupportData;

    private String beneficiaryRefOrMmid;

    private String beneficiaryMobile;

    private String remitterMobile;

    private String remitterMmid;

    private String beneficiaryAccountNo;

    private String beneficiaryIfsc;

    private String remarks;

    private Boolean restricted;

    private String restrictionId;

    private String restrictionString;

    private String productId;

    private Boolean transactionAllowed;

    private Integer legId;

    @JsonProperty("isAccount")
    private boolean isAccount;

}

