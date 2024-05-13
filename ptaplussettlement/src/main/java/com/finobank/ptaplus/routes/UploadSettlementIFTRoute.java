package com.finobank.ptaplus.routes;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class UploadSettlementIFTRoute extends RouteBuilder {

    @ConfigProperty(name = "minioConfig")
    String minioConfig;

    @Transactional
    @Override
    public void configure() throws Exception {

        onException(Exception.class, ErrorResponseException.class, MinioException.class, ServerException.class)
                .handled(false)
                .log(LoggingLevel.ERROR,"Route=UploadSettlementIFTRoute|Message=Error -- ${exception.message}")
                .choice().when(simple("${body} == null"))
                .log("Route=UploadSettlementIFTRoute|Message=Body is Empty in Error")

                .otherwise()
                .log("Route=UploadSettlementIFTRoute|Message=IFT processing result = ${body}")
                .log("Route=UploadSettlementIFTRoute|Message=Saving file to {{fileStorageLocationErrorSettlement}}/${header.SettlementBucketName}")
                .toD("file:{{fileStorageLocationErrorSettlement}}/${header.SettlementBucketName}");

        from("direct:settlement-ift-processing-route")
            .routeId("minio")
                .setHeader("SettlementBucketName")
                .simple("{{glSettlementProducerBucketName}}-${date-with-timezone:now:IST:dd-MM-yyyy}")
                // .process(iftRecordProcessor)
                // .choice()
                // .when(simple("${header.CamelFileName} == 0"))
                // .log("Route=UploadSettlementIFTRoute|Message=Generated File is Empty")
                // .otherwise()
                .log("Route=UploadSettlementIFTRoute|Message=IFT processing result = ${body}")
                .log("Route=UploadSettlementIFTRoute|Message=GL_MINIO_BUCKET  ${header.SettlementBucketName}")
                .setHeader("CamelMinioObjectName").simple("${header.CamelFileName}")
                .log("Route=UploadSettlementIFTRoute|Message= GL_IFT_FILE_NAME ${header.CamelFileName} ")
                .log("Route=UploadSettlementIFTRoute|Message= GL_IFT_FILE_MINIO_UPLOAD_RUNNING ")
                .toD("minio://${header.SettlementBucketName}?" + minioConfig)
                .end();
        // .to("sftp://{{ftpUsername}}@{{ftpHost}}:{{ftpPort}}/{{settlementIFTftpUploadDir}}?preferredAuthentications=password&password={{ftpPassword}}&useUserKnownHostsFile=false")
        // .log("Route=UploadSettlementIFTRoute|Message=IFT processing result " +
        // body())
        // .end();
    }
}
