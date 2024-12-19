package co.kr.sothis.dbanalyzer.dto;

import lombok.Data;

@Data
public class MariaDbConnInfo {

    private String url;
    private String username;
    private String password;
    private String database;
    private String port = "3306";

}
