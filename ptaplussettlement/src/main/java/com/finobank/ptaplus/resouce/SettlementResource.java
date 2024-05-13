package com.finobank.ptaplus.resouce;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.finobank.ptaplus.payload.request.SettlementRequest;
// import com.finobank.ptaplus.payload.request.SettlementWrapperRequest;

@Path("/api/v1/settlement")
public interface SettlementResource {

    @POST
    @Path("/p2mpay")
    @Produces(MediaType.APPLICATION_JSON)
    public Response SettlementP2mPay(SettlementRequest settlementRequest);

    @POST
    @Path("/p2mpay/eod")
    @Produces(MediaType.APPLICATION_JSON)
    public Response EodSettlementP2mPay(SettlementRequest settlementRequest);


    @GET
    @Path("/cbs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response SettlementCbs(SettlementRequest settlementRequest);

    @GET
    @Path("/cbs/eod")
    @Produces(MediaType.APPLICATION_JSON)
    public Response EodSettlementCbs(SettlementRequest settlementRequest);
}
