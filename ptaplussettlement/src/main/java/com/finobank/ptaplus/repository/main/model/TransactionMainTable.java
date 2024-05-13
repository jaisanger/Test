package com.finobank.ptaplus.repository.main.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@ToString
@Entity
@Table(name = "transaction_leg_amt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionMainTable extends PanacheEntityBase {

    @Id
    @SequenceGenerator(name = "fund_transfer_leg_seq_generator",
            sequenceName = "fund_transfer_leg_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_transfer_leg_seq_generator")
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
    public boolean isCreditAccount;

    @Column(name = "is_debit_account")
    public boolean isDebitAccount;

    // new columns to set available balance
    @Column(name = "dr_gl_available_balance_amount")
    public BigDecimal debitGlAccountAvailableBalanceAmount;

    @Column(name = "cr_gl_available_balance_amount")
    public BigDecimal creditGlAccountAvailableBalanceAmount;

    @Column(name= "updated_at")
    public Date updatedAt;

}
