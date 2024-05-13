package com.finobank.ptaplus.payload.request;

import java.util.Date;
import java.util.List;

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
public class SettlementWrapperRequest {
    private List<String> batchId;
    private String appId;
    private String userClass;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date executeAt;
    private Long previousExecutionTime;
    private boolean apiExecutionMode;
}

