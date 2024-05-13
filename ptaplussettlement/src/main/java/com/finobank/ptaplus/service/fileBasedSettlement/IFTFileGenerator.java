package com.finobank.ptaplus.service.fileBasedSettlement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.finobank.ptaplus.SettlementUtils.SettlementStages;
import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.response.PartnerSettlementRule;
import com.finobank.ptaplus.payload.response.SettlementResponse;
import com.finobank.ptaplus.payload.response.SettlementRuleResponse;
import com.finobank.ptaplus.repository.logical.SettlementAuditRepository;
import com.finobank.ptaplus.repository.logical.SettlementLeg;
import com.finobank.ptaplus.repository.logical.model.CBSSettlementTable;
import com.finobank.ptaplus.repository.logical.model.SettlementAuditTable;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class IFTFileGenerator {

    @ConfigProperty(name = "debitAdjustmentCommentPrefix")
    String debitAdjustmentCommentPrefix;

    @ConfigProperty(name = "creditAdjustmentCommentPrefix")
    String creditAdjustmentCommentPrefix;

    @ConfigProperty(name = "iftTransactionCommentFormat")
    String iftTransactionCommentFormat;

    @ConfigProperty(name = "settlementIftRfuFormat")
    String settlementIftRfuFormat;

    @ConfigProperty(name = "SETTLEMENT_IFT_PROCESSING_ROUTE")
    String SETTLEMENT_IFT_PROCESSING_ROUTE;
    @ConfigProperty(name = "costCentre")
    String costCentre;
    @ConfigProperty(name = "eolCharacters")
    String eolCharacter;
    @ConfigProperty(name = "referenceJoiner")
    String referenceJoiner;

    @ConfigProperty(name = "glSettlementProducerBucketName")
    String glSettlementProducerBucketName;

    @ConfigProperty(name = "glSettlementConsumerBucketName")
    String glSettlementConsumerBucketName;

    @ConfigProperty(name = "startRandomNumber")
    int startRandomNumber;
    @ConfigProperty(name = "maxRandomNumber")
    int maxRandomNumber;
    @ConfigProperty(name = "settlementIftFileNameFormat")
    String settlementIftFileNameFormat;
    // @ConfigProperty(name = "tranCategory")
    // String tranCategory;
    @ConfigProperty(name = "settlementTranCategoryInComment")
    String settlementTranCategoryInComment;
    @ConfigProperty(name = "eodTranCategoryInComment")
    String eodTranCategoryInComment;
    @ConfigProperty(name = "debitAccountGLTranType")
    String debitAccountGLTranType;
    @ConfigProperty(name = "creditAccountGLTranType")
    String creditAccountGLTranType;

    @Inject
    SettlementLeg settlementLeg;
    @Inject
    SettlementAuditRepository settlementAuditRepository;
    // @Inject
    MinioClient minioClient;
    @Inject
    ProducerTemplate processIFTProcessingTemplate;

    public Response runSettlement(SettlementRequest settlementRequest, SettlementRuleResponse settlementRuleResponses,
            boolean isEod) {
        Response response = null;
        try {
            settlementAuditRepository.updateStatus(settlementRequest.getBatchId(),
                    SettlementStages.IFT_FILE_GENERATION_RUNNING.toString(),
                    settlementRequest.getSettlementOfTheDay());
            response = perform(settlementRequest, settlementRuleResponses, isEod);
            settlementAuditRepository.updateStatus(settlementRequest.getBatchId(),
                    SettlementStages.SETTLEMENT_IFT_RESULT_PENDING.toString(),
                    settlementRequest.getSettlementOfTheDay());
        } catch (Exception e) {
            log.error("Exception ::{}", e.toString());
            response = Response.ok(new SettlementResponse("1", SettlementStages.IFT_FILE_GENERATION_FAILED.toString()))
                    .build();
        }
        return response;
    }

    public Response perform(SettlementRequest settlementRequest, SettlementRuleResponse settlementRuleResponses,
            boolean isEod) {
        List<String> Iftrecords = new ArrayList<>();
        for (PartnerSettlementRule partnerSettlementRule : settlementRuleResponses.getPartnerSettlementRules()) {
            List<CBSSettlementTable> unsettledList = getUnsettledTrasactions(partnerSettlementRule.getPtaPartnerGl());
            for (int i = 0; i < unsettledList.size(); i++) {
                String newLine = getRecord(unsettledList.get(i),
                        Integer.parseInt(settlementRequest.getSettlementOfTheDay()), isEod);
                Iftrecords.add(newLine);
            }
        }
        if (Iftrecords.size() == 0) {
            SettlementAuditTable settlementAuditTable = SettlementAuditTable.builder()
                    .recordsCount(settlementRuleResponses.getPartnerSettlementRules().size())
                    .successCount(0)
                    .failedCount(0)
                    .rejectCount(settlementRuleResponses.getPartnerSettlementRules().size())
                    .batchId(settlementRequest.getBatchId())
                    .settlementId(settlementRequest.getSettlementOfTheDay())
                    .remarks(SettlementStages.SETTLEMENT.toString())
                    .build();
            settlementAuditRepository.addSettlementResult(settlementAuditTable);
            throw new WebApplicationException("No Transaction to Settle.");
        }
        String iftSerial = getIftFileSerial();
        SimpleDateFormat iftFileCreationDateFormatter = new SimpleDateFormat("ddMMYYYY");
        String iftFileName = settlementIftFileNameFormat
                .replace("@{createDate}", iftFileCreationDateFormatter.format(new Date()))
                .replace("@{batchId}", settlementRequest.getBatchId())
                .replace("@{iftSerial}", iftSerial);

        SettlementAuditTable settlementAuditTable = SettlementAuditTable.builder()
                .fileName(iftFileName)
                .recordsCount(Iftrecords.size())
                .rejectCount(0)
                .batchId(settlementRequest.getBatchId())
                .remarks(SettlementStages.IFT_FILE_GENERATION_SUCCEEDED.toString())
                .settlementId(settlementRequest.getSettlementOfTheDay()).build();

        settlementAuditRepository.addFileRequest(settlementAuditTable);

        File iftFile = createFile(Iftrecords, iftFileName);
        Object responseObject = processIFTProcessingTemplate
                .sendBodyAndHeader(SETTLEMENT_IFT_PROCESSING_ROUTE,
                        ExchangePattern.InOut, iftFile, "CamelFileName", iftFileName);
        log.info("Response from file upload : {}", responseObject.toString());
        log.info("Settlement at cbs Completed.");
        return Response.ok(new SettlementResponse("0", "Settlement at cbs Completed.")).build();
    }

    private File createFile(List<String> iftrecords, String iftFileName) {
        String allRecords = Joiner.on(eolCharacter).skipNulls().join(iftrecords);
        File tempFile = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(allRecords.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            long fileSize = outputStream.toByteArray().length;
            tempFile = File.createTempFile(iftFileName, "" + System.nanoTime());
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.flush();
            outputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile;
    }

    public String getDateIFTFile() {
        SimpleDateFormat IFT_FILE_CREATION_DATE_FORMATTER = new SimpleDateFormat("ddMMYYYY");
        Date DateIFTFile = new Date();
        return IFT_FILE_CREATION_DATE_FORMATTER.format(DateIFTFile);
    }

    private String getRecord(CBSSettlementTable settlementAudit, Integer settlementOftheDay, boolean isEod) {

        String transactionComment = getTransactionComment(settlementAudit, settlementOftheDay, isEod);
        String supportData = getSupportData(settlementAudit, settlementOftheDay, isEod);
        String str = costCentre + "|"
                + settlementAudit.getCreditGlAccount() + "|"
                + settlementAudit.getDebitGlAccount() + "|"
                + settlementAudit.getPostingAmount() + "|"
                + transactionComment.replace("@{accountNumber}", settlementAudit.getDebitGlAccount().toString()) + "|"
                + transactionComment.replace("@{accountNumber}", settlementAudit.getCreditGlAccount().toString()) + "|"
                + debitAccountGLTranType + "|"
                + creditAccountGLTranType + "|"
                + supportData + "|"
                + supportData + "|"
                + costCentre + "|";
        // +creditCostCentre+"|"+result+"|"+failureReason
        log.info("Adding new row :: {}", str);
        return str;
    }

    private String getSupportData(CBSSettlementTable settlementAudit, Integer settlementOftheDay, boolean isEod) {
        String supportData = settlementIftRfuFormat;
        return supportData
                .replace("@{cbsReferenceNo}",
                        settlementAudit.getReferenceNo() + referenceJoiner + settlementAudit.getPostingPair())
                .replace("@{tranCategory}",
                        isEod ? (eodTranCategoryInComment + settlementOftheDay)
                                : (settlementTranCategoryInComment + settlementOftheDay))
                .replace("@{cbsTransactionType}", settlementAudit.getTransactionType())
                .replace("@{DATE}", getDateIFTFile());
    }

    private String getTransactionComment(CBSSettlementTable settlementAudit, Integer settlementOftheDay,
            boolean isEod) {
        String str = iftTransactionCommentFormat;
        return str
                .replace("@{tranCategory}",
                        isEod ? (eodTranCategoryInComment + settlementOftheDay)
                                : (settlementTranCategoryInComment + settlementOftheDay))
                .replace("@{cbsTransactionType}", settlementAudit.getTransactionType())
                .replace("@{DATE}", getDateIFTFile())
                .replace("@{cbsReferenceNo}",
                        settlementAudit.getReferenceNo() + referenceJoiner + settlementAudit.getPostingPair());
    }

    private String getIftFileSerial() {

        Date newDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String date = formatter.format(newDate);
        boolean found = false;
        boolean minioConnectivity = false;
        String lastIndex;
        try {
            log.info(
                    "CorrelationId={}|Processor=SettlementIFTRecordProcessor|Method=getIftFileSerial|Message=Checking GL Settlement Bucket Exist?");
            found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(glSettlementProducerBucketName + "-" + date).build());
            minioConnectivity = true;

        } catch (Exception exception) {
            log.error(
                    "Camel Processor=SettlementIFTRecordProcessor|Method=getIftFileSerial|Exception in checking bucket=",
                    exception.getLocalizedMessage());
            lastIndex = getFileCountfromDB();
            return lastIndex;
        }
        if (!found) {
            try {
                log.info(
                        "CorrelationId={}|Processor=SettlementIFTRecordProcessor|Method=getIftFileSerial|Message=Creating GL Settlement Bucket");
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(glSettlementProducerBucketName + "-" + date).build());
                minioConnectivity = true;

            } catch (Exception exception) {
                log.error(
                        "Camel Processor=SettlementIFTRecordProcessor|Method=getIftFileSerial|Exception in creating bucket=",
                        exception);
                lastIndex = getFileCountfromDB();
                return lastIndex;
            }

        }

        if (minioConnectivity == false) {
            log.info(
                    "Processor=SettlementIFTRecordProcessor|Method=getIftFileSerial|Message=minIo Down, Generating Random IFT file Sequence Number");
            lastIndex = String.valueOf(ThreadLocalRandom.current().nextInt(startRandomNumber, maxRandomNumber));
        } else {
            log.info(
                    "Processor=SettlementIFTRecordProcessor|Method=getIftFileSerial|Message=Generating IFT file Sequence Number");
            Iterable<Result<Item>> result = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(glSettlementProducerBucketName + "-" + date).build());
            int lastIn = Iterables.size(result);
            boolean foundCopy = false;
            try {
                foundCopy = minioClient.bucketExists(
                        BucketExistsArgs.builder().bucket(glSettlementConsumerBucketName + "-" + date)
                                .build());
            } catch (Exception exception) {
                log.error(
                        "Camel Processor=SettlementIFTRecordProcessor|Method=getIftFileSerial|Exception in checking existing bucket",
                        exception);
                lastIndex = getFileCountfromDB();
                return lastIndex;
            }

            int lastIndexCopy;
            if (!foundCopy) {
                lastIndexCopy = 0;
            } else {
                Iterable<Result<Item>> resultCopy = minioClient.listObjects(ListObjectsArgs
                        .builder()
                        .bucket(glSettlementConsumerBucketName + "-" + date)
                        .build());
                lastIndexCopy = Iterables.size(resultCopy);
            }
            int finalIndex = lastIn + lastIndexCopy + 1;

            if (finalIndex % 1000 < 10) {
                lastIndex = "00" + String.valueOf(finalIndex);
            } else if (finalIndex % 1000 < 100) {
                lastIndex = "0" + String.valueOf(finalIndex);

            } else {
                lastIndex = String.valueOf(finalIndex);
            }
        }
        return lastIndex;
    }

    private String getFileCountfromDB() {
        long fileCount;
        String lastIndex;
        Date newDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(newDate);

        fileCount = settlementAuditRepository.getIFTUploadedFileCount(date);
        long finalIndex = fileCount + 1;

        if (finalIndex % 1000 < 10) {
            lastIndex = "00" + String.valueOf(finalIndex);
        } else if (finalIndex % 1000 < 100) {
            lastIndex = "0" + String.valueOf(finalIndex);
        } else {
            lastIndex = String.valueOf(finalIndex);
        }
        return lastIndex;
    }

    private List<CBSSettlementTable> getUnsettledTrasactions(String partnerGl) {
        List<CBSSettlementTable> cbsSettlementTables = new ArrayList<>();
        cbsSettlementTables = settlementLeg.getUnsettledTransactions(partnerGl);
        if (cbsSettlementTables.size() == 0) {
            log.error("No Unsettled Transaction for {}", partnerGl);
        }
        return cbsSettlementTables;
    }

}
/**
 * (costCentre))
 * . (debitAccountNumber))
 * . (creditAccountNumber))
 * . (postingAmount))
 * . (debitTransactionComment))
 * . (creditTransactionComment))
 * . (debitTransactionType))
 * . (creditTransactionType))
 * . (debitSupportData))
 * . (creditSupportData))
 * . (debitCostCentre))
 * . (creditCostCentre))
 * . (result))
 * . (failureReason));
 * 
 * 9001|
 * 3233000104|
 * 3218000201|
 * 1229.35|
 * SETTLEMENT7/1694020846062/3218000201/06092023|
 * SETTLEMENT7/1694020846062/3233000104/06092023|
 * MDR|
 * MCR|
 * ZRFUT8#1694020846062~TCMT#SETTLEMENT7/PTNREOD/1694020846062/06092023|
 * ZRFUT8#1694020846062~TCMT#SETTLEMENT7/PTNREOD/1694020846062/06092023|
 * 9001
 */