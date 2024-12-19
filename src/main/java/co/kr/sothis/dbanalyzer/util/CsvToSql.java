package co.kr.sothis.dbanalyzer.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class CsvToSql {

    public String readCsvToSql(String filename) throws IOException, CsvException {
        String tableName = filename.substring(0, filename.length() - 4);

        InputStream inputStream = CsvToSql.class.getClassLoader().getResourceAsStream(filename);

        if (inputStream == null) {
            return null;
        }

        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
        List<String[]> records = reader.readAll();

        String[] columns = records.get(0);

        for (int i = 0; i < columns.length; i++) {
            columns[i] = columns[i].replace("\n", "").replace(" ", "");
        }

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(generateCreateTableQuery(tableName, columns));

        for (int i = 1; i < records.size(); i++) {
            String[] row = records.get(i);
            queryBuilder.append("INSERT INTO ").append(tableName).append(" (");

            // 컬럼 이름 추가
            for (int j = 0; j < columns.length; j++) {
                queryBuilder.append(columns[j]);
                if (j < columns.length - 1) {
                    queryBuilder.append(", ");
                }
            }

            queryBuilder.append(") VALUES (");

            // 값 추가
            for (int j = 0; j < row.length; j++) {
                queryBuilder.append("'").append(row[j]).append("'");
                if (j < row.length - 1) {
                    queryBuilder.append(", ");
                }
            }

            queryBuilder.append(");\n");
        }

        reader.close();

        saveSQLToFile(queryBuilder.toString(), tableName + ".sql");

        return queryBuilder.toString();
    }

    private String generateCreateTableQuery(String tableName, String[] columns) {
        StringBuilder createTableQuery = new StringBuilder();
        createTableQuery.append("CREATE TABLE ").append(tableName).append(" (\n");

        for (int i = 0; i < columns.length; i++) {
            createTableQuery.append("  ").append("`" + columns[i] + "`").append(" VARCHAR(1000) NULL");
            if (i < columns.length - 1) {
                createTableQuery.append(",");
            }
            createTableQuery.append("\n");
        }

        createTableQuery.append(");\n");
        return createTableQuery.toString();
    }

    private void saveSQLToFile(String sql, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(sql);
            System.out.println("SQL 쿼리가 " + fileName + " 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
