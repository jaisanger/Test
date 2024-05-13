package com.finobank.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import com.finobank.pojo.SettlementWrapperPayload;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

@Path("/api/v1/settlement")
@RegisterRestClient
public interface SettlementResource {

    @POST
    @Path("/p2mpay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SettlementWrapperPayload settlebatch(SettlementWrapperPayload payload);

    @POST
    @Path("/p2mpay/eod")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SettlementWrapperPayload settleeod(SettlementWrapperPayload payload);
}