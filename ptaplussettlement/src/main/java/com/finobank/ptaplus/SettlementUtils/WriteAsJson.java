package com.finobank.ptaplus.SettlementUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteAsJson{
    public static synchronized String log(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer();
            return writer.writeValueAsString(object);
            // log.info("JSON for {} was | {}", prefix, writer.writeValueAsString(object));
        } catch (Exception exception) {
            log.error("Util=AppUtil|Method=printJson|Reason=Unable to print request JSON|Exception {} \n|Object=",exception,object);
        }
        return object.toString();
    }
}