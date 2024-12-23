package co.kr.sothis.dbanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryResult {
    private List<String> columnList;
    private List<List<String>> dataList;
    private String error;
}
