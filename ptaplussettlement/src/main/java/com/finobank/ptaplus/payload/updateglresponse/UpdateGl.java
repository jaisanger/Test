package com.finobank.ptaplus.payload.updateglresponse;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

public class UpdateGl {

    public long id;

    public long glNumber;

    public Timestamp lastTransactionDate;

    public UpdateGl() {
    }

    public UpdateGl(long id, long glNumber, Timestamp lastTransactionDate) {
        this.id = id;
        this.glNumber = glNumber;
        this.lastTransactionDate = lastTransactionDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(long glNumber) {
        this.glNumber = glNumber;
    }

    public Timestamp getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Timestamp lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    
}
