package co.kr.sothis.dbanalyzer.service;

import co.kr.sothis.dbanalyzer.dto.MariaDbConnInfo;
import co.kr.sothis.dbanalyzer.vo.MariaDbQuery;
import co.kr.sothis.dbanalyzer.dto.Column;
import co.kr.sothis.dbanalyzer.dto.ColumnLog;
import co.kr.sothis.dbanalyzer.util.conn.MariaDbConnector;
import co.kr.sothis.dbanalyzer.util.ObjectToCsv;
import co.kr.sothis.dbanalyzer.util.SqlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MariaDbService {

    private final SqlUtil sqlUtil;
    private final ObjectToCsv objectToCsv;
    private final MariaDbQuery query;
    private final MariaDbConnector connector;

    private static Connection mariaDbConn;

    public Integer createTable(MariaDbConnInfo info) throws SQLException {
        open(info);

        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String tableName = info.getDatabase().toUpperCase() + "_" + LocalDateTime.now().format(FORMATTER);

        Integer result = sqlUtil.executeUpdate(mariaDbConn, query.createColumnTableQuery(tableName));
        sqlUtil.executeUpdate(mariaDbConn, query.insertLogQuery(tableName));

        close();

        return result;
    }

    public ColumnLog getColumnLog(MariaDbConnInfo info) throws SQLException {
        open(info);

        ColumnLog columnLog = new ColumnLog();

        ResultSet resultSet = sqlUtil.executeQuery(mariaDbConn, query.selectLogQuery());

        while (resultSet.next()) {
            columnLog.setTableName(resultSet.getString(1));
            columnLog.setInserted(resultSet.getInt(2));
        }

        close();

        return columnLog;
    }

    public Integer insertColumnInfo(MariaDbConnInfo info, List<Column> columnList) throws SQLException {
        ColumnLog columnLog = getColumnLog(info);

        open(info);
        Integer result = 0;
        StringBuilder totalSql = new StringBuilder();

        for (Column column : columnList) {
            String sql = query.insertColumnQuery(columnLog, column);
            totalSql.append(sql);

            Integer temp = sqlUtil.executeUpdate(mariaDbConn, sql);
            result += temp;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(columnLog.getTableName() + ".sql"))) {
            writer.write(totalSql.toString());
            System.out.println("SQL 쿼리가 " + columnLog.getTableName() + ".sql" + " 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        sqlUtil.executeUpdate(mariaDbConn, query.updateLogQuery(columnLog));

        close();

        return result;
    }

    private void open(MariaDbConnInfo info) throws SQLException {
        if (mariaDbConn == null) {
            mariaDbConn = connector.getConnection(info);
        }
    }

    public boolean close() {
        if (mariaDbConn != null) {
            try {
                mariaDbConn.close();
                mariaDbConn = null;
            } catch (SQLException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean getColumnList(MariaDbConnInfo info) throws SQLException {
        ColumnLog columnLog = getColumnLog(info);

        open(info);

        List<Column> list = new ArrayList<>();
        ResultSet resultSet = sqlUtil.executeQuery(mariaDbConn, query.selectColumnQuery(columnLog.getTableName()));

        while (resultSet.next()) {
            Column column = new Column();
            column.setTableName(resultSet.getString(1));
            column.setColumnName(resultSet.getString(2));
            column.setComment(resultSet.getString(3));
            column.setType(resultSet.getString(4));
            column.setLength(resultSet.getString(5));
            column.setNullable(resultSet.getString(6));
            column.setDefaultValue(resultSet.getString(7));
            column.setPk(resultSet.getString(8));
            column.setReferTable(resultSet.getString(9));
            column.setReferColumn(resultSet.getString(10));
            list.add(column);
        }

        return objectToCsv.convertToCsv(list);

    }
}
