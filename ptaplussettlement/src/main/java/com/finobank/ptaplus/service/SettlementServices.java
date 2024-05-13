package com.finobank.ptaplus.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.finobank.ptaplus.SettlementUtils.CorelationId;
import com.finobank.ptaplus.SettlementUtils.WriteAsJson;
import com.finobank.ptaplus.client.UpdateGlService;
import com.finobank.ptaplus.filter.SettlementRequestValidator;
import com.finobank.ptaplus.payload.request.SettlementRequest;
import com.finobank.ptaplus.payload.response.SettlementResponse;
import com.finobank.ptaplus.payload.updateglresponse.UpdateGl;
import com.finobank.ptaplus.resouce.SettlementResource;

import com.finobank.ptaplus.service.finoSettlement.FinoEodSettlement;
import com.finobank.ptaplus.service.finoSettlement.FinoSettlement;

import io.quarkus.logging.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettlementServices implements SettlementResource {

    @Inject
    FinoSettlement finoSettlement;

    @Inject
    CorelationId corelationId;

    @Inject
    FinoEodSettlement finoEodSettlement;
    @Inject
    CBSSettlementWrapper cbsSettlementWrapper;

    @Inject
    SettlementRequestValidator settlementRequestValidator;

    @RestClient
    UpdateGlService updateglservice;

    @Override
    public Response EodSettlementP2mPay(SettlementRequest settlementRequest) {
        log.info("Request recieved for p2m Eod {} at {} ", WriteAsJson.log(settlementRequest), LocalDateTime.now());
        Response response = null;
        try {
            validateRequest(settlementRequest);
            response = finoEodSettlement.process(settlementRequest);
            log.info("Response from EodSettlementP2mPay :{}", WriteAsJson.log(response.getEntity()));
            Response response2 = EodSettlementCbs(settlementRequest);

        //Calling update gls service to update last settlement date in ldap
        try{
        List<UpdateGl> updateresponse= updateglservice.updateGlsLastsettlementDateLdap();
        log.info("GL lastTransactiondate updated");
        log.debug("Updated gls response {}",updateresponse); 
        }catch(Exception e){
            Log.error("Settlement eod process Completed but error updating gls last transaction date "+e.toString());
        }


        } catch (Exception e) {
            log.error("Unhandled Exception Occurs {}", e);
            return Response.ok(new SettlementResponse("1", e.toString())).build();
        }
        return response;
    }

    @Override
    public Response SettlementP2mPay(SettlementRequest settlementRequest) {
        log.info("Request recieved  for p2m settlement {} at {} ", WriteAsJson.log(settlementRequest),
                LocalDateTime.now());
        Response response = null;
        try {
            validateRequest(settlementRequest);
            response = finoSettlement.process(settlementRequest);
            Response response2 = SettlementCbs(settlementRequest);
            log.info("Response from settlementP2mPay :{}", WriteAsJson.log(response.getEntity()));

        } catch (Exception e) {
            log.error("Unhandled Exception Occurs {}", e);
            return Response.ok(new SettlementResponse("1", e.toString())).build();
        }
        return response;
    }

    private void validateRequest(SettlementRequest settlementRequest) {
        try {
            settlementRequestValidator.requestValidator(settlementRequest);
        } catch (Exception e) {
            log.error("Invalid SettlementRequest", e.toString());
            throw new WebApplicationException(e.getMessage());
        }
    }

    @Override
    public Response SettlementCbs(SettlementRequest settlementRequest) {
        log.info("Request recieved  for cbs settlement {} at {} ", WriteAsJson.log(settlementRequest),
                LocalDateTime.now());
        Response response = cbsSettlementWrapper.performSettlement(settlementRequest);
        log.info("Response from SettlementCbs :{}", WriteAsJson.log(response.getEntity()));
        return response;
    }

    @Override
    public Response EodSettlementCbs(SettlementRequest settlementRequest) {
        log.info("Request recieved  for cbs settlement {} at {} ", WriteAsJson.log(settlementRequest),
                LocalDateTime.now());
        Response response = cbsSettlementWrapper.performEod(settlementRequest);
        log.info("Response from EodSettlementCbs :{}", WriteAsJson.log(response.getEntity()));
        return response;
    }

}