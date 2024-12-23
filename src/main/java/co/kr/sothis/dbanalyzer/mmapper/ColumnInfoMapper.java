package co.kr.sothis.dbanalyzer.mmapper;

import co.kr.sothis.dbanalyzer.dto.Column;
import co.kr.sothis.dbanalyzer.dto.ColumnInfo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ColumnInfoMapper {

    private final ModelMapper modelMapper;

    public ColumnInfo columnToColumnInfo(Column column) {
        ColumnInfo columnInfo = new ColumnInfo();

        ModelMapper tempMapper = new ModelMapper();

        tempMapper.typeMap(Column.class, ColumnInfo.class).addMappings(mapper -> {
            mapper.map(src -> {
                String type = column.getType();

                switch (type) {
                    case "bigint":
                        return "int8";
                    case "character varying":
                        return "varchar";
                    case "smallint":
                        return "int2";
                    case "integer":
                        return "int4";
                    case "character":
                        return "char";
                    case "timestamp without time zone":
                        return "timestamp";
                    default:
                        return type;
                }
            }, ColumnInfo::setType);
            mapper.map(src -> {
                int length = 0;
                try {
                    length = Integer.parseInt(column.getLength());
                    return length;
                } catch (NumberFormatException e) {
                    return null;
                }
            }, ColumnInfo::setLength);
            mapper.map(src -> {
                String isPk = column.getPk();

                if (isPk != null) {
                    if (isPk.equals("YES")) {
                        return true;
                    } else if (isPk.equals("NO")) {
                        return false;
                    }
                    return null;
                } else {
                    return null;
                }
            }, ColumnInfo::setPk);
            mapper.map(src -> {
                String isNull = column.getNullable();

                if (isNull != null) {
                    if (isNull.equals("YES")) {
                        return true;
                    } else if (isNull.equals("NO")) {
                        return false;
                    }
                    return null;
                } else {
                    return null;
                }
            }, ColumnInfo::setNullable);
        });

        tempMapper.map(column, columnInfo);
        return columnInfo;
    }
}
