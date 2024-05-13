package com.finobank.ptaplus.SettlementUtils;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Alternative
@ApplicationScoped
@Priority(1)
public class CorelationId1 extends CorelationId{
    
    public String getCorlationId(){
        return null;
    }

    public synchronized String getUniqueTransactionId(){
        return "P-0-11111111111";
    }

    public synchronized String getReferenceNo() {
        return "11111111111";
    }
    
}
