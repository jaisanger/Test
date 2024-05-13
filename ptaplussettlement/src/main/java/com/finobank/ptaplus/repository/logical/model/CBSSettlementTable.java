package com.finobank.ptaplus.repository.logical.model;

import java.math.BigDecimal;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@ToString
@Entity
@Table(name = "cbs_settlement_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CBSSettlementTable extends PanacheEntityBase{
    @Id
    @SequenceGenerator(name = "cbs_settlement_table_seq_generator",
            sequenceName = "cbs_settlement_table_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cbs_settlement_table_seq_generator")
    public Long id;

    @Column(name = "pta_trans_seq_no", nullable = false)
    public String ptaTransSeqNo;

    @Column(name = "txn_reference_no")
    public String referenceNo;

    @Column(name = "posting_sequence")
    public Integer postingSequence;

    @Column(name = "posting_pair")
    public Integer postingPair;

    @Column(name = "is_charge")
    public Boolean isCharge;

    @Column(name = "is_tax")
    public Boolean isTax;

    @Column(name = "posting_amount")
    public BigDecimal postingAmount;

    @Column(name = "dr_gl_account")
    public Long debitGlAccount;

    @Column(name = "dr_gl_account_balance_amount", nullable = false)
    public BigDecimal debitGlAccountBalanceAmount;

    @Column(name = "cr_gl_account")
    public Long creditGlAccount;

    @Column(name = "cr_gl_account_balance_amount", nullable = false)
    public BigDecimal creditGlAccountBalanceAmount;

    @Column(name = "created_at", nullable = false)
    public Date createdAt;

    @Column(name = "value_date")
    public Date valueDate;

    // new column
    public Boolean reversed;

    // new requirement on the 6th Sept, 2021
    @Column(name = "transaction_type", nullable = true)
    public String transactionType;

    // new flag to avoid account/gl discovery
    @Column(name = "is_credit_account")
    public Boolean isCreditAccount;

    @Column(name = "is_debit_account")
    public Boolean isDebitAccount;

    // new columns to set available balance
    @Column(name = "dr_gl_available_balance_amount")
    public BigDecimal debitGlAccountAvailableBalanceAmount;

    @Column(name = "cr_gl_available_balance_amount")
    public BigDecimal creditGlAccountAvailableBalanceAmount;

    @Column(name = "is_settled_at_cbs")
    public Boolean settledAtCbs;

    @Column(name="settled_at_cbs")
    public Date settledTime;

    @Column(name="settlement_id")
    public String settlementId;

    @Column(name="cbs_reference_no")
    public String cbs_reference_no;

    @Column(name ="cbs_response")
    public String cbsResponse;

    @Column(name="isEod")
    public Boolean isEod;


    public CBSSettlementTable(TransactionTable transactions ,Boolean isEod) {
        // this.id = transactions.getId();
        // this.id=1L;
        this.ptaTransSeqNo = transactions.getPtaTransSeqNo();
        this.referenceNo = transactions.getReferenceNo();
        this.postingSequence = transactions.getPostingSequence();
        this.postingPair = transactions.getPostingPair();
        this.isCharge = transactions.getIsCharge();
        this.isTax = transactions.getIsTax();
        this.postingAmount = transactions.getPostingAmount();
        this.debitGlAccount = transactions.getDebitGlAccount();
        this.debitGlAccountBalanceAmount = transactions.getDebitGlAccountBalanceAmount();
        this.creditGlAccount = transactions.getCreditGlAccount();
        this.creditGlAccountBalanceAmount = transactions.getCreditGlAccountBalanceAmount();
        this.createdAt = transactions.getCreatedAt();
        this.valueDate = transactions.getValueDate();
        this.reversed = transactions.getReversed();
        this.transactionType = transactions.getTransactionType();
        this.isCreditAccount = transactions.getIsCreditAccount();
        this.isDebitAccount = transactions.getIsCreditAccount();
        this.settledAtCbs=false;
        this.debitGlAccountAvailableBalanceAmount = transactions.getDebitGlAccountAvailableBalanceAmount();
        this.creditGlAccountAvailableBalanceAmount = transactions.getCreditGlAccountAvailableBalanceAmount();
        this.isEod=isEod;
    }

}
