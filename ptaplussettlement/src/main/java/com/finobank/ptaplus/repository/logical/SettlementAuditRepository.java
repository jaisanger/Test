package com.finobank.ptaplus.repository.logical;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.finobank.ptaplus.SettlementUtils.SettlementStages;
import com.finobank.ptaplus.repository.logical.model.SettlementAuditTable;

@Slf4j
@ApplicationScoped
public class SettlementAuditRepository implements PanacheRepository<SettlementAuditTable> {

   @ConfigProperty(name = "updateStatusQuery")
   String updateStatusQuery;
   @ConfigProperty(name = "stopBatchQuery")
   String stopBatchQuery;

   @ConfigProperty(name = "AddSettlementResponeTotheTable")
   String addSettlementResponeTotheTable;
   @ConfigProperty(name = "AddFileRequesttoTable")
   String addFileRequesttoTable;
   @ConfigProperty(name = "AddFileResponsetoTable")
   String addFileResponsetoTable;

   @Inject
   EntityManager entityManager;

   Calendar cal = Calendar.getInstance();

   public boolean isRunningBatch(String BatchId) {
      PanacheQuery<SettlementAuditTable> query = find(
            "select f from SettlementAuditTable f where f.batchId= ?1 and f.isRunning=true", BatchId);
      if (query.list().size() == 0) {
         log.info("No parallel running batch for batchId {}", BatchId);
         return false;
      } else {
         log.info("Already running batch for batchId {}. current status is {}", BatchId,
               query.list().get(0).getProcessingStatus());
         return true;
      }
   }

   @Transactional
   public void StartSettlement(String BatchId, String SettlementId, Boolean isApiMode,Boolean isEod) {
      log.info("Starting settlement for batchId {} at {}", BatchId, cal.getTime());
      SettlementAuditTable settlementAuditTable = SettlementAuditTable.builder()
            .batchId(BatchId)
            .dateStarted(cal.getTime())
            .isRunning(true)
            .settlementRunType(isApiMode)
            .settlementId(SettlementId)
            .isEod(isEod)
            .processingStatus(SettlementStages.INITIALIZATION.toString()).build();
      entityManager.persist(settlementAuditTable);
   }

   @Transactional
   public void updateStatus(String BatchId, String processingStatus, String settlementId) {
      log.info("updating status of {} to {}", BatchId, processingStatus);
      String updatequery = updateStatusQuery.replace("@{processingStatus}", processingStatus)
            .replace("@{lastUpdated}", cal.getTime().toString()).replace("@{batchId}", BatchId)
            .replace("@{settlementId}", settlementId);
      Query query = entityManager.createNativeQuery(updatequery);
      query.executeUpdate();
   }

   @Transactional
   public void addFileResponse(SettlementAuditTable settlementAuditTable) {
      String updateQuery = addFileResponsetoTable
            .replace("{@resultFileName}", settlementAuditTable.getResultFileName())
            .replace("{@successCount}", "" + settlementAuditTable.getSuccessCount())
            .replace("{@failedCount}", "" + settlementAuditTable.getFailedCount())
            .replace("{@dateResponded}", cal.getTime().toString())
            .replace("{@remarks}", settlementAuditTable.getRemarks())
            .replace("{@batchId}", settlementAuditTable.getBatchId())
            .replace("{@settlementId}", settlementAuditTable.getSettlementId());
      log.info("Added file for batchId {} with {}.", settlementAuditTable.getBatchId(), updateQuery);
      Query query = entityManager.createNativeQuery(updateQuery);
      query.executeUpdate();
   }

   @Transactional
   public void addFileRequest(SettlementAuditTable settlementAuditTable) {
      String updateQuery = addFileRequesttoTable
            .replace("{@FileName}", settlementAuditTable.getFileName())
            .replace("{@recordCount}", "" + settlementAuditTable.getRecordsCount())
            .replace("{@rejectCount}", "" + settlementAuditTable.getRejectCount())
            .replace("{@dateResponded}", cal.getTime().toString())
            .replace("{@remarks}", settlementAuditTable.getRemarks())
            .replace("{@batchId}", settlementAuditTable.getBatchId())
            .replace("{@settlementId}", settlementAuditTable.getSettlementId());

      log.info("Added file for batchId {} with {}.", settlementAuditTable.getBatchId(), updateQuery);
      Query query = entityManager.createNativeQuery(updateQuery);
      query.executeUpdate();
   }

   @Transactional
   public void addSettlementResult(SettlementAuditTable settlementAuditTable) {
      String updateQuery = addSettlementResponeTotheTable
            .replace("{@recordCount}", "" + settlementAuditTable.getRecordsCount())
            .replace("{@successCount}", "" + settlementAuditTable.getSuccessCount())
            .replace("{@failedCount}", "" + settlementAuditTable.getFailedCount())
            .replace("{@rejectCount}", "" + settlementAuditTable.getRejectCount())
            .replace("{@dateResponded}", cal.getTime().toString())
            .replace("{@remarks}", settlementAuditTable.getRemarks())
            .replace("{@batchId}", settlementAuditTable.getBatchId())
            .replace("{@settlementId}", settlementAuditTable.getSettlementId());

      log.info("Batch {} settled with ::{}.", settlementAuditTable.getBatchId(), updateQuery);
      Query query = entityManager.createNativeQuery(updateQuery);
      query.executeUpdate();
   }

   @Transactional
   public void StopSettlement(String BatchId, String remark, String settlementId) {
      log.info("Stopping Batch due to {}", remark);
      String stopQuery = stopBatchQuery.replace("@{lastUpdate}", cal.getTime().toString())
            .replace("@{batchId}", BatchId).replace("@{remark}", remark).replace("@{settlementId}", settlementId);
      Query query = entityManager.createNativeQuery(stopQuery);
      query.executeUpdate();
   }

   public Long getIFTUploadedFileCount(String date) {
      try {
         PanacheQuery<SettlementAuditTable> transactionQuery = find(
               "select f from  SettlementAuditTable f where TO_CHAR(f.dateStarted, 'YYYY-MM-DD') = ?1 and f.settlementRunType=false",
               new Object[] { date });
         return transactionQuery.count();
      } catch (Exception var3) {
         log.error("Repository=IFTUploadedFileRepository|Method=getIFTUploadedFileCount|Exception= {}", var3);
         return null;
      }
   }

   public SettlementAuditTable getBatchId(String fileName) {
      PanacheQuery<SettlementAuditTable> query = find("select f from SettlementAuditTable f where f.fileName= ?1",
            fileName);
      return query.singleResult();
   }

}
