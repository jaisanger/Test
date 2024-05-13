package com.finobank.ptaplus.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import com.finobank.ptaplus.payload.request.TransactionRequest;

@Path("/api/v1/transaction")
@RegisterRestClient
public interface PostTransaction {
    
    @POST
    public Response postTransaction(TransactionRequest transactionRequest);
}
