package org.acme;
// package com.finobank;

// import io.smallrye.mutiny.Multi;
// import io.smallrye.mutiny.Uni;
// import io.vertx.core.json.JsonObject;
// import io.vertx.ext.web.client.WebClientOptions;
// import io.vertx.mutiny.core.Vertx;
// import io.vertx.mutiny.core.buffer.Buffer;
// import io.vertx.mutiny.ext.web.client.HttpResponse;
// import io.vertx.mutiny.ext.web.client.WebClient;
// import io.vertx.mutiny.ext.web.codec.BodyCodec;

// import java.util.Collections;
// import java.util.List;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.stream.Collectors;

// public class Quotes {

//     public static final String PROGRAMMING_QUOTE = "https://random-data-api.com/api/v2/users";
//     public static final String CHUCK_NORRIS_QUOTE = "https://api.chucknorris.io/jokes/random";

//     public static void main(String[] args) throws InterruptedException {
//         CountDownLatch latch = new CountDownLatch(1);
//         // Create the Vert.x instance. In a Quarkus app, just inject it.
//         Vertx vertx = Vertx.vertx();
//         // Create a Web Client
//         WebClient client = WebClient.create(vertx);

//         // Combine the result of our 2 Unis in a tuple and subscribe to it
//         Uni.combine().all()
//                 .unis(getProgrammingQuote(client), getChuckNorrisQuote(client));
//         //         .asTuple()
//         //         .subscribe().with(tuple -> {
//         //     System.out.println("Programming Quote: " + tuple.getItem1());
//         //     System.out.println("Chuck Norris Quote: " + tuple.getItem2());

//         //     latch.countDown();
//         // });


//         latch.await(10, TimeUnit.SECONDS);
//         System.out.println("outside request:---");
//         vertx.closeAndAwait();
//     }

//     private static Uni<String> getProgrammingQuote(WebClient client) {
//         return client.getAbs(PROGRAMMING_QUOTE)
//                 .as(BodyCodec.jsonObject())
//                 .send()
//                 .onItem().transform(r -> r.body().getString("avatar"));
//     }

//     private static Uni<String> getChuckNorrisQuote(WebClient client) {
//         return client.getAbs(CHUCK_NORRIS_QUOTE)
//                 .as(BodyCodec.jsonObject())
//                 .send()
//                 .onItem().transform(r -> r.body().getString("value"));
//     }



// }