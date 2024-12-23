package co.kr.sothis.dbanalyzer.dto;

import lombok.Data;

import java.util.List;

@Data
public class TableInfo {
    private List<ColumnInfo> columns;
    private String schema;
    private String tableName;
}
