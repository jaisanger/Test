package com.finobank.ptaplus.routes;

import io.agroal.api.AgroalDataSource;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;

import com.finobank.ptaplus.payload.CbsGlIftResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class CbsIFTPResultAnalyzerRoutes extends RouteBuilder {

    @Inject
    CbsIFTResultPojoProcessor cbsIFTResultPojoProcessor;

    @Inject
    AgroalDataSource defaultDataSource;

    @Override
    public void configure() {

        BindyCsvDataFormat bindy = new BindyCsvDataFormat(CbsGlIftResult.class);
        bindy.setLocale("default");

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"Route=UploadSettlementIFTRoute|Message=Error -- ${exception.message}")
                ;
                // .process(processErrorHandler);

        // String fromRoute = "sftp://{{ftpUsername}}@{{ftpHost}}:{{ftpPort}}/{{settlementIFTResultDownloadDir}}" +
        // "?preferredAuthentications=password&password={{ftpPassword}}&delay={{settlementIFTResultDownloadDirPollingDelay}}" +
        // "&useUserKnownHostsFile=false&include={{settlementIftFilePrefix}}\\d{8}_\\d{3}\\.csv\\.out&fastExistsCheck={{fastExistsEnable}}&noop={{noopEnable}}";

        String fromRoute = "sftp://{{ftpUsername}}@{{ftpHost}}:{{ftpPort}}/{{settlementIFTResultDownloadDir}}" +
        "?preferredAuthentications=password&password={{ftpPassword}}&delay={{settlementIFTResultDownloadDirPollingDelay}}" +
        "&useUserKnownHostsFile=false&include={{settlementIftFilePrefix}}\\d{8}_\\S*_\\d{3}\\.csv\\.out&fastExistsCheck={{fastExistsEnable}}&noop={{noopEnable}}";

        from(fromRoute)
                .log("fromRoute CbsIFTPResultAnalyzerRoutes")
                .choice().when(simple("${header.CamelFileLength} <= 0"))
                .log("Empty File Recieved ** ${header.CamelFileName}")
                .endChoice()
                .otherwise()
                .setHeader("UploadedIFTType", constant("SETTLEMENT_GL_IFT"))
                .idempotentConsumer(header("CamelFileName"),
                        new JdbcMessageIdRepository(defaultDataSource,
                                "GL_SETTLEMENT_RESULT_IFT_FILE_DOWNLOAD_TRACK_PROCESSOR"))
                .split(body())
                .unmarshal(bindy)
                .process(cbsIFTResultPojoProcessor)
                .end();
        }
}

