package com.finobank.pojo;

import java.util.Date;
import java.util.List;


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
    private String executeAt;
    private Long previousExecutionTime;
    private boolean apiExecutionMode;

}
