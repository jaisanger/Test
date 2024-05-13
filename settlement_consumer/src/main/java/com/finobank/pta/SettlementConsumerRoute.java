package com.finobank.pta;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;

@ApplicationScoped
public class SettlementConsumerRoute extends RouteBuilder {

        @ConfigProperty(name = "minioConfig")
        String minioConfig;

        @Inject
        MinioClient minioClient;

        @ConfigProperty(name = "settlementIFTftpUploadDir")
        String settlementIFTftpUploadDir;

		@ConfigProperty(name = "updateMinioUploadStatus")
    	String updateMinioUploadStatus;

		// @ConfigProperty(name = "updateReversalSuccessStatusBatchAudit")
    	// String updateReversalSuccessStatusBatchAudit;

		// @ConfigProperty(name = "updateReversalFailureStatusBatchAudit")
    	// String updateReversalFailureStatusBatchAudit;

		// @ConfigProperty(name = "updateReversalSuccessStatusSettlementLeg")
    	// String updateReversalSuccessStatusSettlementLeg;

		// @ConfigProperty(name = "updateReversalFailureStatusSettlementLeg")
    	// String updateReversalFailureStatusSettlementLeg;

		@ConfigProperty(name = "getIFTFileDetails")
    	String getIFTFileDetails;

        @Override
        public void configure() throws Exception {
               
            onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Route=SettlementConsumerRoute|Message = Exception Error : ${exception.message}")
				.choice()
					.when(simple("${exception.message} contains 'Cannot connect to'"))
						.log(LoggingLevel.INFO, "In Exception, calling route for settlement reversal")
						.to("direct:reverseSettlementTransactions")
				.end();

			onException(MinioException.class)
				.handled(true)
				.log(LoggingLevel.ERROR, "Route=SettlementConsumerRoute|Message = MinioException Error : ${exception.message}");
			
			onException(ErrorResponseException.class)
				.handled(true)
				.log(LoggingLevel.ERROR, "Route=SettlementConsumerRoute|Message = ErrorResponseException Error : ${exception.message}");

			from("quartz:SettlementConsumerCron?cron={{settlementCron}}")
				.log("\n\nSettlementConsumer started at : ${date:now:dd-MM-yyyy hh:mm:ss}")
				.setHeader("producerBucket")
				.simple("{{glSettlementProducerBucketName}}-${date:now:dd-MM-yyyy}")
				.setHeader("consumerBucket")
				.simple("{{glSettlementConsumerBucketName}}-${date:now:dd-MM-yyyy}")
				.log(LoggingLevel.INFO, "Route=SettlementConsumerRoute|Message=Settlement Consumer Bucket = ${header.consumerBucket}")
				.log(LoggingLevel.INFO, "Route=SettlementConsumerRoute|Message=Settlement Producer Bucket = ${header.producerBucket}")
				.process(e -> {
					boolean checkGlSettlementProducerBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket(e.getIn().getHeader("producerBucket").toString()).build());
					if (!checkGlSettlementProducerBucket)
						minioClient.makeBucket(MakeBucketArgs.builder().bucket(e.getIn().getHeader("producerBucket").toString()).build());

					String destination = e.getIn().getHeader("consumerBucket").toString();
					boolean destinationBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket(destination).build());
					if (!destinationBucket)
						minioClient.makeBucket(MakeBucketArgs.builder().bucket(destination).build());
					})
				.pollEnrich()
					.simple("minio://${header.producerBucket}?" + minioConfig
									+ "&includeBody=true&autoCloseBody=true&moveAfterRead=true&maxMessagesPerPoll=1&destinationBucketName=${header.consumerBucket}&sendEmptyMessageWhenIdle=true&autoCreateBucket=false")
				.log(LoggingLevel.INFO, "Route=SettlementConsumerRoute|File body = ${body}")
				.log(LoggingLevel.INFO, "Route=SettlementConsumerRoute|File Name = ${header.CamelMinioObjectName}")
				.choice()
					.when(simple("${body} == null"))
						.log(LoggingLevel.INFO, "Route=SettlementConsumerRoute| No file found")
					.otherwise()
						.setHeader("CamelFileName").simple("${header.CamelMinioObjectName}")
						.log(LoggingLevel.INFO, "Route=SettlementConsumerRoute|Message= Moving IFT file into sftp, folder name :"+ settlementIFTftpUploadDir)
						.removeHeader("CamelToEndpoint")
						.to("sftp://{{ftpUsername}}@{{ftpHost}}:{{ftpPort}}/{{settlementIFTftpUploadDir}}?preferredAuthentications=password&password={{ftpPassword}}&useUserKnownHostsFile=false")
						.setHeader("date_sftp_upload")
							.simple("${date:now:yyyy-MM-dd HH:mm:ss.SSS}")
						.setHeader("sftp_url")
							.simple("{{settlementIFTftpUploadDir}}/${header.CamelMinioObjectName}")
						//.to("sql:UPDATE public.ift_uploaded_file SET date_sftp_upload=:#date_sftp_upload,sftp_url=:#sftp_url WHERE file_name =:#CamelMinioObjectName;")
						.setBody(simple(updateMinioUploadStatus
                                .replace("@{date_sftp_upload}", "${header.date_sftp_upload}")
                                .replace("@{sftp_url}", "${header.sftp_url}")
								.replace("@{file_name}", "${header.CamelMinioObjectName}")))
						// .to("jdbc:camel")
						.log(LoggingLevel.INFO, "Route=SettlementConsumerRoute|Message= minio upload details updated in ift_uploaded_file table")
				.end()
			.end();

	}
}