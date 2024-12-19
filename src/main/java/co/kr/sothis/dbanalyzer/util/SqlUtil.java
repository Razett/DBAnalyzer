package co.kr.sothis.dbanalyzer.util;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SqlUtil {

    public Integer executeUpdate(Connection conn, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.executeUpdate();
    }

    public ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet executeQuery(Connection conn, String sql, String[] psValue) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        int count = countQuestionMarks(sql);
        for (int i = 1; i <= count; i++) {
            ps.setString(i, psValue[i - 1]);
        }
        return ps.executeQuery();
    }

    private int countQuestionMarks(String input) {
        int count = 0;
        boolean inString = false;
        char previousChar = '\0';

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            // 문자열 리터럴 내부 여부 판별
            if (currentChar == '\'' && previousChar != '\\') {
                inString = !inString;
            }

            // 문자열 리터럴 외부에서만 ? 카운트
            if (currentChar == '?' && !inString) {
                count++;
            }

            previousChar = currentChar;
        }

        return count;
    }
}
