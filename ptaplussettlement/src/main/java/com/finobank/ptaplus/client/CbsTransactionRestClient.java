package com.finobank.ptaplus.client;

// import com.finobank.pta.core.client.EsbHeadersFactory;
// import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.finobank.ptaplus.payload.request.CbsPostTransactionRequest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("")
@RegisterRestClient
public interface CbsTransactionRestClient {

    @POST
    Response postTransaction(CbsPostTransactionRequest request);

}