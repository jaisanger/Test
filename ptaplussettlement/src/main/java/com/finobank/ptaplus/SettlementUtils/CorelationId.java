package com.finobank.ptaplus.SettlementUtils;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CorelationId {

    @ConfigProperty(name="uniqueprefix")
    String uniquePrefix;
    
    public String getCorlationId(){
        return null;
    }

    public synchronized String getUniqueTransactionId(){
        return uniquePrefix.charAt(0)+"-"+uniquePrefix.charAt(uniquePrefix.length()-1)+"-"+System.currentTimeMillis();
    }

    public synchronized String getReferenceNo() {
        return uniquePrefix.charAt(uniquePrefix.length()-1)+""+System.currentTimeMillis();
    }

}
