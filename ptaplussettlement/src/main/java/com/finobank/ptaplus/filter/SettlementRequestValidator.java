package com.finobank.ptaplus.filter;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;

import com.finobank.ptaplus.payload.request.SettlementRequest;

@ApplicationScoped
public class SettlementRequestValidator {

    public void requestValidator(@Valid SettlementRequest settlementRequest){

    }
}
