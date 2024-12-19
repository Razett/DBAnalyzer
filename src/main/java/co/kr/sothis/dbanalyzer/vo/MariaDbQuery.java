package co.kr.sothis.dbanalyzer.vo;

import co.kr.sothis.dbanalyzer.dto.Column;
import co.kr.sothis.dbanalyzer.dto.ColumnLog;
import org.springframework.stereotype.Component;

@Component
public final class MariaDbQuery {

    public String createColumnTableQuery(String tableName) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "`table_name` VARCHAR(100) NULL COMMENT '테이블명', " +
                "`column_name` VARCHAR(100) NULL COMMENT '컬럼영문명', " +
                "`comment` VARCHAR(100) NULL COMMENT '컬럼한글명', " +
                "`type` VARCHAR(100) NULL COMMENT '데이터_타입', " +
                "`length` VARCHAR(8) NULL COMMENT '데이터_길이', " +
                "`nullable` VARCHAR(5) NULL COMMENT 'NULL여부', " +
                "`default` VARCHAR(100) NULL COMMENT '기본값', " +
                "`pk` VARCHAR(5) NULL COMMENT '주키', " +
                "`refer_table` VARCHAR(100) NULL COMMENT '참조테이블', " +
                "`refer_col` VARCHAR(100) NULL COMMENT '참조컬럼' " +
                ")";
    }

    public String insertLogQuery(String tableName) {
        return "INSERT INTO log(tablename) values('" + tableName + "')";
    }


    public String insertColumnQuery(ColumnLog columnLog, Column column) {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO `");
        sb.append(columnLog.getTableName()).append("` ");
        sb.append("(`table_name`,`column_name`,`comment`,`type`,`length`,`nullable`,`default`,`pk`,`refer_table`,`refer_col`) VALUES (");
        sb.append("'").append(column.getTableName()).append("'").append(",");
        sb.append("'").append(column.getColumnName()).append("'").append(",");
        sb.append("'").append(column.getComment()).append("'").append(",");
        sb.append("'").append(column.getType()).append("'").append(",");
        sb.append("'").append(column.getLength()).append("'").append(",");
        sb.append("'").append(column.getNullable()).append("'").append(",");

        String defaultValue = column.getDefaultValue();
        if (defaultValue != null) {
            defaultValue = defaultValue.replaceAll("'", "");
        }

        sb.append("'").append(defaultValue).append("'").append(",");
        sb.append("'").append(column.getPk()).append("'").append(",");
        sb.append("'").append(column.getReferTable()).append("'").append(",");
        sb.append("'").append(column.getReferColumn()).append("'").append(")");
        sb.append(";\n");

        return sb.toString();
    }

    public String selectLogQuery() {
        return "SELECT * FROM log order by tablename desc limit 1";
    }

    public String updateLogQuery(ColumnLog columnLog) {
        return "UPDATE log SET inserted = 1 where tablename = '" + columnLog.getTableName() + "'";
    }

    public String selectColumnQuery(String tableName) {
        return "SELECT * FROM `" + tableName + "`" + "order by `table_name` asc";
    }
}
