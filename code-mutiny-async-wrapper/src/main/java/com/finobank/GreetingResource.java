package com.finobank;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import com.finobank.pojo.SettlementWrapperPayload;
import com.finobank.pojo.SettlementWrapperRequest;
import com.finobank.service.SettlementResource;

import io.quarkus.logging.Log;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/wrapper")
public class GreetingResource {

    @RestClient
    SettlementResource service;

    @Inject
    EventBus bus;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("batch")
    public Response requestBatch(SettlementWrapperRequest payload) throws InterruptedException {
        Log.info("Input Request batch : "+ payload.toString());
        bus.<SettlementWrapperPayload>requestAndForget("settlebatches", payload);
        return Response.ok().build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("eod")
    public Response requestEOD(SettlementWrapperRequest payload) throws InterruptedException {
        Log.info("Input Request EOD: "+ payload.toString());
        bus.<SettlementWrapperPayload>requestAndForget("settleEOD", payload);
        return Response.ok().build();
    }

}
