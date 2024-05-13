package com.finobank.ptaplus.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.finobank.ptaplus.payload.request.BatchConfigRule;
import com.finobank.ptaplus.payload.response.SettlementRuleResponse;
import com.finobank.ptaplus.payload.updateglresponse.UpdateGl;

@Path("")
@RegisterRestClient
public interface UpdateGlService {
    
    @GET
    @Path("update-active-gls")
    List<UpdateGl> updateGlsLastsettlementDateLdap();
}
