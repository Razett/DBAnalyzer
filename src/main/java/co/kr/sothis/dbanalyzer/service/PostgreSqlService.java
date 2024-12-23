package co.kr.sothis.dbanalyzer.service;

import co.kr.sothis.dbanalyzer.dto.*;
import co.kr.sothis.dbanalyzer.mmapper.ColumnInfoMapper;
import co.kr.sothis.dbanalyzer.vo.PostgreSqlQuery;
import co.kr.sothis.dbanalyzer.util.conn.PostgreSqlConnector;
import co.kr.sothis.dbanalyzer.util.SqlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostgreSqlService  {

    private final SqlUtil sqlUtil;
    private final PostgreSqlQuery query;
    private final PostgreSqlConnector connector;
    private final ColumnInfoMapper columnInfoMapper;

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

    public QueryResult executeQuery(PostgreSqlConnInfo info, QueryInput queryInput) throws SQLException {
        open(info);

        ResultSet resultSet = sqlUtil.executeQuery(postgreConn, queryInput.getQuery());
        ResultSetMetaData metaData = resultSet.getMetaData();

        // 컬럼 수 가져오기
        int columnCount = metaData.getColumnCount();

        List<String> columnList = new ArrayList<>();

        for (int i = 1; i <= columnCount; i++) {
            columnList.add(metaData.getColumnName(i));
        }
        List<List<String>> dataList = new ArrayList<>();
        while (resultSet.next()) {
            List<String> data = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                data.add(resultSet.getString(i));
            }
            dataList.add(data);
        }

        QueryResult queryResult = new QueryResult();
        queryResult.setColumnList(columnList);
        queryResult.setDataList(dataList);

        close();

        return queryResult;
    }

    public List<TableList> getTableList(PostgreSqlConnInfo info) throws SQLException {
        open(info);

        ResultSet resultSet = sqlUtil.executeQuery(postgreConn, query.schemaTableListQuery());

        List<TableList> schemaList = new ArrayList<>();
        while (resultSet.next()) {
            TableList tableList = new TableList();
            tableList.setSchema(resultSet.getString("table_schema"));

            Array tablesArray = resultSet.getArray("tables");
            String[] tables;
            if (tablesArray != null) {
                Object[] objects = (Object[]) tablesArray.getArray();
                tables = Arrays.stream(objects).map(Object::toString).toArray(String[]::new);
            } else {
                tables = new String[]{};
            }
            tableList.setTables(tables);
            schemaList.add(tableList);
        }

        close();

        return schemaList;
    }

    public TableInfo getTableInfo(PostgreSqlConnInfo info, TableInfo tableInfo) throws SQLException {
        open(info);

        String[] psValue = {tableInfo.getSchema(), tableInfo.getTableName()};
        ResultSet resultSet = sqlUtil.executeQuery(postgreConn, query.tableInfoQuery(), psValue);

        List<ColumnInfo> columnList = new ArrayList<>();

        while (resultSet.next()) {
            Column column = Column.builder()
                    .columnName(resultSet.getString(1))
                    .type(resultSet.getString(2))
                    .length(resultSet.getString(3))
                    .defaultValue(resultSet.getString(4))
                    .nullable(resultSet.getString(5))
                    .pk(resultSet.getString(6)).build();

            columnList.add(columnInfoMapper.columnToColumnInfo(column));
        }

        TableInfo table = new TableInfo();
        table.setColumns(columnList);

        close();

        return table;
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
