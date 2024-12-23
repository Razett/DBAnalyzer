package co.kr.sothis.dbanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ColumnInfo {
    private String tableName;
    private String columnName;
    private String comment;
    private String type;
    private Integer length;
    private Boolean nullable;
    private String defaultValue;
    private Boolean pk;
    private String referTable;
    private String referColumn;
}
