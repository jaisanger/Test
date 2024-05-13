package com.finobank.ptaplus.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.Link;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@CsvRecord(separator = "\\|", autospanLine = true)
public class SettlementIFTResultFile {

    @Link
    private final List<CbsGlIftResult> cbsGlIftResultList = new ArrayList<>();
}
