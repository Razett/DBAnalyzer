package co.kr.sothis.dbanalyzer.dto;

import lombok.Data;

@Data
public class TableList {
    private String schema;
    private String[] tables;
}
