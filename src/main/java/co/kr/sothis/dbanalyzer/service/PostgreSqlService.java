package co.kr.sothis.dbanalyzer.service;

import co.kr.sothis.dbanalyzer.dto.PostgreSqlConnInfo;
import co.kr.sothis.dbanalyzer.vo.PostgreSqlQuery;
import co.kr.sothis.dbanalyzer.dto.Column;
import co.kr.sothis.dbanalyzer.util.conn.PostgreSqlConnector;
import co.kr.sothis.dbanalyzer.util.SqlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostgreSqlService  {

    private final SqlUtil sqlUtil;
    private final PostgreSqlQuery query;
    private final PostgreSqlConnector connector;

    private Connection postgreConn;

    public List<Column> getColumns(PostgreSqlConnInfo info) throws SQLException {
        open(info);

//        ResultSet resultSet = SQL_UTIL.executeQuery(postgreConn, QUERY.columnInfoQuery());
        String[] psValue = {info.getSchema(), info.getSchema(), info.getSchema(), info.getSchema(), info.getSchema(), info.getSchema()};
        System.out.println(psValue[5]);
        ResultSet resultSet = sqlUtil.executeQuery(postgreConn, query.columnInfoQuery(), psValue);

        List<Column> columnList = new ArrayList<>();

        while (resultSet.next()) {
            Column column = Column.builder()
                    .tableName(resultSet.getString(1))
                    .columnName(resultSet.getString(2))
                    .comment(resultSet.getString(3))
                    .type(resultSet.getString(4))
                    .length(resultSet.getString(5))
                    .nullable(resultSet.getString(6))
                    .defaultValue(resultSet.getString(7))
                    .pk(resultSet.getString(8))
                    .referTable(resultSet.getString(9))
                    .referColumn(resultSet.getString(10)).build();
            columnList.add(column);
        }

        close();

        return columnList;
    }

    private void open(PostgreSqlConnInfo info) throws SQLException {
        if (postgreConn == null) {
            postgreConn = connector.getConnection(info);
        }
    }

    public boolean close() {
        if (postgreConn != null) {
            try {
                postgreConn.close();
                postgreConn = null;
            } catch (SQLException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
