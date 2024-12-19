package co.kr.sothis.dbanalyzer.util.conn;

import co.kr.sothis.dbanalyzer.dto.PostgreSqlConnInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class PostgreSqlConnector {

    private DataSource dataSource;

    public Connection getConnection(PostgreSqlConnInfo info) throws SQLException {
        if (dataSource == null) {
            dataSource = getDataSource(getConfig(info));
        }
        return dataSource.getConnection();
    }

    private static HikariConfig getConfig(PostgreSqlConnInfo info) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://" + info.getUrl() + ":" + info.getPort() + "/" + info.getDatabase() + "?currentSchema=" + info.getSchema());
        config.setUsername(info.getUsername());
        config.setPassword(info.getPassword());

        return config;
    }

    private static DataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }
}
