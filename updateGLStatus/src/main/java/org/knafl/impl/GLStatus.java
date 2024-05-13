
package org.knafl.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.acme.greeting.extension.runtime.ContextBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.logging.Log;


@Path("/glstatus")
public class GLStatus {
    
    @Inject
    ContextBuilder contextBuilder;
    @ConfigProperty(name = "lastSettlementDateParam")
    String lastSettlementDate;
    @ConfigProperty(name = "glbaseDn")
    String glbaseDn;
    @ConfigProperty(name = "daysForDormancy")
    int x;
    @ConfigProperty(name = "daysForInactivity")
    int y;
     @ConfigProperty(name = "todayDate")
    int z;
    @ConfigProperty(name = "inactiveGLsFilterdn")
    String inactiveGLfilterdn;
    @ConfigProperty(name = "dormantGLsFilterdn")
    String dormantGLfilterdn;
 


     public String getDurationDate(int days){
     LocalDateTime currentTime = LocalDateTime.now();
     LocalDateTime duration = currentTime.minus(days, ChronoUnit.DAYS);
     DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
     Log.debug("Generated duration date: "+ dtf.format(duration));
     return dtf.format(duration)+"000000Z";
    }

       private String timestampToGeneralizedTime(Timestamp timestamp){
      Date date = new Date(timestamp.getTime());
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'");
      Log.debug(dateFormat.format(date));
      return dateFormat.format(date);
    }
    
  

     @GET
    @Path("/update-active-gls")
    public List<MyEntity> updateActiveLdapGLsList() throws NamingException{
      List<MyEntity> listofEntities=MyEntity.findRecordsUpdatedToday();
      Log.info("OUTPUT GLs: "+listofEntities);
      InitialLdapContext context=contextBuilder.createContext();
      for (MyEntity myEntity : listofEntities) {
      ModificationItem[] mods=new ModificationItem[1];
      Log.info("Last transaction date of gl: "+myEntity.getGlNumber()+" - is :" + myEntity.lastTransactionDate);
      mods[0]= new ModificationItem(InitialLdapContext.REPLACE_ATTRIBUTE,new BasicAttribute("lastTransactionDate", timestampToGeneralizedTime(myEntity.lastTransactionDate)));
      context.modifyAttributes("glNumber="+myEntity.getGlNumber()+","+glbaseDn,mods);
      Log.info("updated last transaction date");
      }
       updateinactivefilter(context);
       updatedormantfilter(context);
      context.close();
      return listofEntities;
    }


    public String updateinactivefilter(InitialLdapContext context) throws NamingException{
        ModificationItem[] mods=new ModificationItem[1];
        String filter = "(&("+lastSettlementDate+"<=" + getDurationDate(y) + ")("+lastSettlementDate+">=" + getDurationDate(x-1) + "))";
        Log.info(filter);
        mods[0]= new ModificationItem(InitialLdapContext.REPLACE_ATTRIBUTE,new BasicAttribute("nsRoleFilter",filter ));
        context.modifyAttributes(inactiveGLfilterdn,mods);
        Log.info("updated inactivefilter");
      return "updated";
    }

    public String updatedormantfilter(InitialLdapContext context) throws NamingException{
        ModificationItem[] mods=new ModificationItem[1];
        String filter = "("+lastSettlementDate+"<=" + getDurationDate(x) + ")";
        Log.info(filter);
        mods[0]= new ModificationItem(InitialLdapContext.REPLACE_ATTRIBUTE,new BasicAttribute("nsRoleFilter",filter ));
        context.modifyAttributes(dormantGLfilterdn,mods);
        Log.info("updated dormantfilter");
      return "updated";
    }



    public  List<MyEntity> getList(String filter) throws NamingException {
      InitialLdapContext context=contextBuilder.createContext();  
      List<MyEntity> entityList=new ArrayList<>();
      NamingEnumeration<SearchResult> nm=context.search(glbaseDn,filter, new SearchControls());
      while (nm.hasMore()) {
        SearchResult sr=nm.next();
        MyEntity entity=new MyEntity();
        if (sr != null) { 
            Attributes attr=sr.getAttributes();
              entity=new MyEntity(attr);
        }
        entityList.add(entity);  
      }
      context.close();
      return entityList;
    }


    @GET
    @Path("/searchtoday")
    public   List<MyEntity> getActiveListWithTodayTransaction() throws NamingException {
      String filter = "("+lastSettlementDate+">=" + getDurationDate(z) + ")";
      Log.info("Generated Filter is :"+filter);
      return getList(filter);
    }

    @GET
    @Path("/searchInactive")
    public  List<MyEntity> getInactiveList() throws NamingException {
      String filter = "(&("+lastSettlementDate+"<=" + getDurationDate(y) + ")("+lastSettlementDate+">=" + getDurationDate(x-1) + "))";
      Log.info("Generated Filter is :"+filter);
      return (getList(filter));
    }

    @GET
    @Path("/searchDormant")
    public  List<MyEntity> getDormantList() throws NamingException {
      String filter = "("+lastSettlementDate+"<=" + getDurationDate(x) + ")";
      Log.info("Generated Filter is :"+filter);
      return getList(filter);
    }    

    @GET
    @Path("/searchActiveNoTransaction")
    public  List<MyEntity> getActiveListNoTransaction() throws NamingException {
      String filter = "(&("+lastSettlementDate+">=" + getDurationDate(y+1) + ")("+lastSettlementDate+"<=" + getDurationDate(z+1) + "))"; 
      Log.info("Generated Filter is :"+filter);
      return (getList(filter));
    }  


}
