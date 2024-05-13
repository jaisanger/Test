package com.finobank.ptaplus.payload;

import lombok.*;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import static com.finobank.ptaplus.config.GLIFTFieldIndex.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@CsvRecord(separator = "\\|")
public class CbsGlIftResult {

    @DataField(pos = COST_CENTRE_INDEX)
    private String costCentre;

    @DataField(pos = DEBIT_ACCOUNT_NUMBER_INDEX)
    private String debitAccountNumber;

    @DataField(pos = CREDIT_ACCOUNT_NUMBER_INDEX)
    private String creditAccountNumber;

    @DataField(pos = POSTING_AMOUNT_INDEX)
    private String postingAmount;

    @DataField(pos = DEBIT_TRANSACTION_COMMENT_INDEX)
    private String debitTransactionComment;

    @DataField(pos = CREDIT_TRANSACTION_COMMENT_INDEX)
    private String creditTransactionComment;

    @DataField(pos = DEBIT_TRANSACTION_TYPE_INDEX)
    private String debitTransactionType;

    @DataField(pos = CREDIT_TRANSACTION_TYPE_INDEX)
    private String creditTransactionType;

    // TODO: need to confirm with Fino
    @DataField(pos = DEBIT_SUPPORT_DATA_INDEX)
    private String debitSupportData;

    @DataField(pos = CREDIT_SUPPORT_DATA_INDEX)
    private String creditSupportData;

    @DataField(pos = DEBIT_COST_CENTRE_INDEX)
    private String debitCostCentre;

    @DataField(pos = CREDIT_COST_CENTRE_INDEX)
    private String creditCostCentre;

    @DataField(pos = RESULT_INDEX)
    private String result;

    @DataField(pos = RETURN_CODE_FIELD_INDEX)
    private String returnCode;

    @DataField(pos = FAILURE_REASON_INDEX)
    private String failureReason;

    public String toCsvRecord() {
        StringBuilder builder = new StringBuilder();
        // For an example IFT record, please see https://github.com/vbg/vbg-fino-final/issues/89
        builder.append(emptyStringIfNull(costCentre))
                .append("|")
                .append(emptyStringIfNull(debitAccountNumber))
                .append("|")
                .append(emptyStringIfNull(creditAccountNumber))
                .append("|")
                .append(emptyStringIfNull(postingAmount))
                .append("|")
                .append(emptyStringIfNull(debitTransactionComment))
                .append("|")
                .append(emptyStringIfNull(creditTransactionComment))
                .append("|")
                .append(emptyStringIfNull(debitTransactionType))
                .append("|")
                .append(emptyStringIfNull(creditTransactionType))
                .append("|")
                .append(emptyStringIfNull(debitSupportData))
                .append("|")
                .append(emptyStringIfNull(creditSupportData))
                .append("|")
                .append(emptyStringIfNull(debitCostCentre))
                .append("|")
                .append(emptyStringIfNull(creditCostCentre))
                .append("|")
                .append(emptyStringIfNull(result))
                .append("|")
                .append(emptyStringIfNull(failureReason));

        return builder.toString();
    }

    private Object emptyStringIfNull(String debitTransactionType2) {
        return null;
    }
}
