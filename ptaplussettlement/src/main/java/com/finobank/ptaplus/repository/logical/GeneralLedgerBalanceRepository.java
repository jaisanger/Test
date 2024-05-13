package com.finobank.ptaplus.repository.logical;



import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.finobank.ptaplus.repository.logical.model.GeneralLedgerBalance;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class GeneralLedgerBalanceRepository implements PanacheRepository<GeneralLedgerBalance>{
    
    @ConfigProperty(name ="getGlBalancequery")
    String getGlBalanceQuery;

    public List<GeneralLedgerBalance> getGlBalance(String ledger){
        PanacheQuery<GeneralLedgerBalance> query = find(getGlBalanceQuery,Long.parseLong(ledger));
            return query.list();
    }
}
