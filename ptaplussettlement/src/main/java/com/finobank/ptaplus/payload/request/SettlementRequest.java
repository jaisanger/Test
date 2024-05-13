package com.finobank.ptaplus.payload.request;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class SettlementRequest {
    
    @NotBlank
    private String batchId;
    private String appId;
    private String userClass;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeAt;
    private Long previousExecutionTime;
    private boolean apiExecutionMode;
    @NotBlank
    private String settlementOfTheDay;
    
    public SettlementRequest(SettlementWrapperRequest settlementRequest) {
    this.batchId=null;
    this.appId=settlementRequest.getAppId();
    this.userClass=settlementRequest.getUserClass();
    this.executeAt=settlementRequest.getExecuteAt();
    this.previousExecutionTime=settlementRequest.getPreviousExecutionTime();
    this.apiExecutionMode=settlementRequest.isApiExecutionMode();
    }

}
