package com.finobank.ptaplus.repository.logical.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "settlement_audit")
public class SettlementAuditTable extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "settlement_audit_seq_generator", sequenceName = "settlement_audit_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settlement_audit_seq_generator")
    @Column(name = "id")
    public Long id;

    @Column(name = "settlement_id")
    public String settlementId;
    @Column(name ="batch_id")
    public String batchId;
    @Column (name="is_running")
    public Boolean isRunning;
    @Column (name="last_updated")
    public Date lastUpdated;
    @Column(name = "is_api_mode")
    public Boolean settlementRunType;
    @Column(name = "file_name", nullable = true)
    public String fileName;
    @Column(name = "records_count")
    public Integer recordsCount;
    @Column(name = "success_count")
    public Integer successCount;
    @Column(name = "failed_count")
    public Integer failedCount;
    @Column(name = "reject_count")
    public Integer rejectCount;
    @Column(name = "date_started", nullable = true)
    public Date dateStarted;
    @Column(name = "date_responded")
    public Date respondingDate;
    @Column(name = "processing_status")
    public String processingStatus;
    @Column(name = "result_file_name")
    public String resultFileName;
    @Column(name = "remarks")
    public String remarks;
    @Column(name="isEod")
    public Boolean isEod;
}
