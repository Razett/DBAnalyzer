package co.kr.sothis.dbanalyzer.dto;

import lombok.Data;

import java.util.List;

@Data
public class QueryResult {
    private List<String> columnList;
    private List<List<String>> dataList;
}
