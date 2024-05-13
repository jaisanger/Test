package com.finobank.pta;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.inject.Inject;

import org.gaul.s3proxy.S3Proxy;
import org.gaul.shaded.org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import io.quarkus.test.junit.QuarkusTest;



@QuarkusTest
public class CamelTest {
    @Inject
    SettlementConsumerRoute route;

S3Proxy s3Proxy ;
BlobStoreContext context;

    @BeforeEach
    void setup() throws Exception{
    Properties properties = new Properties();
properties.setProperty("jclouds.filesystem.basedir", "/tmp/blobstore");

        context = ContextBuilder
        .newBuilder("filesystem")
        .credentials("identity", "credential")
        .overrides(properties)
        .build(BlobStoreContext.class);

        s3Proxy = S3Proxy.builder()
        .blobStore(context.getBlobStore())
        .endpoint(URI.create("http://127.0.0.1:9090"))
        .build();

s3Proxy.start();
while (!s3Proxy.getState().equals(AbstractLifeCycle.STARTED)) {
    Thread.sleep(1);
} 
    }
    

    @AfterEach
    void destroy() throws Exception{
        if(s3Proxy!=null){
            s3Proxy.stop();
            s3Proxy=null;
            context.close();
        }
    }


    //@Test()
    // @Order(2)
    // public void setConsumerbucket() throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException {
    //     String expectedHost = "http://127.0.0.1:9090";
    //     MinioClient client = MinioClient.builder()
    //     .endpoint(expectedHost)
    //     .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
    //     .build();
    //     client.setAppInfo("testApp", "2.0.4");
    //     LocalDate currentDate = LocalDate.now();
    //     // Define the desired date format
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    //     // Format the date using the formatter
    //     String formattedDate = currentDate.format(formatter);
    //     client.uploadObject(UploadObjectArgs.builder().bucket("data-consumer"+formattedDate).object("data-consumer"+formattedDate).filename("src/test/resources/file.txt").build());
    // }
    @Test()
    @Order(1)
    public void setProducerbucket() throws IOException, MinioException, InvalidKeyException, NoSuchAlgorithmException, IllegalArgumentException, InterruptedException {
        String expectedHost = "http://127.0.0.1:9090";
        MinioClient client = MinioClient.builder()
        .endpoint(expectedHost)
        .credentials("identity", "credential")
        .build();
        client.setAppInfo("testApp", "2.0.4");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = currentDate.format(formatter);
        if(client.bucketExists(BucketExistsArgs.builder().bucket("data-producer"+formattedDate).build())){
            client.removeObject(RemoveObjectArgs.builder().bucket("data-producer"+formattedDate).object("data-producer"+formattedDate).build());
            client.removeBucket(RemoveBucketArgs.builder().bucket("data-producer"+formattedDate).build());
        }
        Thread.sleep(6000);
    }
    
    @Test
    @Order(0)
    void contextTest() throws Exception {
        String expectedHost = "http://127.0.0.1:9090";
        MinioClient client = MinioClient.builder()
        .endpoint(expectedHost)
        .credentials("identity", "credential")
        .build();
        client.setAppInfo("testApp", "2.0.4");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = currentDate.format(formatter);
        if(!client.bucketExists(BucketExistsArgs.builder().bucket("data-producer"+formattedDate).build())){
            client.makeBucket(MakeBucketArgs.builder().bucket("data-producer"+formattedDate).build());
            client.uploadObject(UploadObjectArgs.builder().bucket("data-producer"+formattedDate).object("data-producer"+formattedDate).filename("src/test/resources/file.txt").build());
        }
        Thread.sleep(10000);
        assertTrue(client.bucketExists(BucketExistsArgs.builder().bucket("data-producer"+formattedDate).build()));
    }




}
