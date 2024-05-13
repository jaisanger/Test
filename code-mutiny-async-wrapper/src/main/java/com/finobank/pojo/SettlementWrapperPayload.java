package com.finobank.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementWrapperPayload {
    
    private String batchId;
    private String appId;
    private String userClass;
    private String executeAt;
    private Long previousExecutionTime;
    private boolean apiExecutionMode;
    private String settlementOfTheDay;
    

    public SettlementWrapperPayload(String batchId,SettlementWrapperRequest request){
        this.batchId=batchId;
        this.appId=request.getAppId();
        this.userClass= request.getUserClass();
        this.executeAt= request.getExecuteAt();
        this.previousExecutionTime= request.getPreviousExecutionTime();
        this.apiExecutionMode= request.isApiExecutionMode();
    }
}