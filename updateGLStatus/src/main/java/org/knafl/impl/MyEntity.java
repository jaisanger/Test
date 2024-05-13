package org.knafl.impl;


import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.logging.Log;



@Table(name="gl_balance")
@Entity
public class MyEntity extends PanacheEntityBase {

   
    @Column(name="id")
    @Id
    public long id;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name="gl_number")
    public long glNumber;

    @Column(name="updated_at")
    public Timestamp lastTransactionDate;

    public MyEntity() {
    }

    public MyEntity(Attributes attributes) throws NamingException {
        this.id = Long.parseLong(attributes.get("id").get().toString());
        this.glNumber = Long.parseLong(attributes.get("glNumber").get().toString());
        Log.info(attributes.get("lastTransactionDate").get().toString());
    }

    public long getGlNumber() {
        return glNumber;
    }

    public void setGlNumber(long glNumber) {
        this.glNumber = glNumber;
    }

    public static List<MyEntity> findRecordsUpdatedToday(){
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.valueOf(today);
        Date endOfDay = Date.valueOf(today.plusDays(1));
        Log.info("Start : "+startOfDay+" before : "+endOfDay );
        return list("updated_at >= ?1 and updated_at < ?2", startOfDay, endOfDay);
    }

    @Override
    public String toString() {
        return "MyEntity [id=" + id + ", glNumber=" + glNumber + ", lastTransactionDate=" + lastTransactionDate + "]";
    }
}
