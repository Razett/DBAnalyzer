package co.kr.sothis.dbanalyzer.util;

import co.kr.sothis.dbanalyzer.dto.Column;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ObjectToCsv {

    public boolean convertToCsv(Object object) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");

        if (object == null) {
            return false;
        } else {
            if (object instanceof List) {
                if (((List<?>) object).get(0) instanceof Column) {
                    saveSQLToFile(columnToCsv(object), "Object_" + LocalDateTime.now().format(FORMATTER) + ".csv");
                    return true;
                }
            }
        }
        return false;
    }

    private void saveSQLToFile(String data, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(data);
            System.out.println("데이터가 " + fileName + " 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String columnToCsv(Object object) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("table_name,column_name,comment,type,length,nullable,default,pk,refer_table,refer_col\n");

        if (object instanceof List) {
            for (Object o : (List) object) {
                if (o instanceof Column) {
                    csvBuilder.append(((Column) o).getTableName()).append(",");
                    csvBuilder.append(((Column) o).getColumnName()).append(",");
                    csvBuilder.append("'").append(((Column) o).getComment()).append("',");
                    csvBuilder.append(((Column) o).getType()).append(",");
                    csvBuilder.append(((Column) o).getLength()).append(",");
                    csvBuilder.append(((Column) o).getNullable()).append(",");
                    csvBuilder.append(((Column) o).getDefaultValue()).append(",");
                    csvBuilder.append(((Column) o).getPk()).append(",");
                    csvBuilder.append(((Column) o).getReferTable()).append(",");
                    csvBuilder.append(((Column) o).getReferColumn()).append("\n");
                }
            }
        }
        return csvBuilder.toString();
    }
}
