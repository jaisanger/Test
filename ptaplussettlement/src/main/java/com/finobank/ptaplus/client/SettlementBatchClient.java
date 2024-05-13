package com.finobank.ptaplus.client;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.finobank.ptaplus.payload.request.BatchConfigRule;
import com.finobank.ptaplus.payload.response.SettlementRuleResponse;

@Path("")
@RegisterRestClient
public interface SettlementBatchClient {
 
    @POST
    List<SettlementRuleResponse> getBatch(BatchConfigRule settlementRuleRequests);

}
