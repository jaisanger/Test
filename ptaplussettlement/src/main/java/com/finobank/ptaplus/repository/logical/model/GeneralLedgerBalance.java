package com.finobank.ptaplus.repository.logical.model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "gl_balance",
        indexes = {@Index(name = "index_gl_number", columnList = "gl_number", unique = true)})
@Getter
@Setter
@NoArgsConstructor
public class GeneralLedgerBalance extends PanacheEntityBase {

    @Id
    @SequenceGenerator(name = "gl_balance_seq_generator",
            sequenceName = "gl_balance_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gl_balance_seq_generator")
    public Long id;

    @Column(name = "gl_number")
    public Long generalLedgerNumber;

    @Column(name = "balance_amount")
    public BigDecimal balanceAmount;

    @Column(name = "total_hold")
    public BigDecimal totalHold;

    @Column(name = "total_lien")
    public BigDecimal totalLien;

    @Column(name = "created_at", nullable = false)
    public Date createdAt;

    @Column(name = "updated_at")
    public Date updatedAt;

    public BigDecimal availableBalance() {
        BigDecimal amountToDeduct;
        BigDecimal availableBalance = this.balanceAmount;

        if (this.totalHold == null && this.totalLien != null) {
            amountToDeduct = this.totalLien;
        } else if (this.totalHold != null && this.totalLien == null) {
            amountToDeduct = this.totalHold;
        } else {
            amountToDeduct = this.totalHold.add(this.totalLien);
        }

        if (this.balanceAmount != null) {
            availableBalance = this.balanceAmount.subtract(amountToDeduct);
        }

        return availableBalance;
    }

}

