package co.kr.sothis.dbanalyzer.util.conn;

import co.kr.sothis.dbanalyzer.dto.MariaDbConnInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MariaDbConnector {

    private DataSource dataSource;

    public Connection getConnection(MariaDbConnInfo info) throws SQLException {
        if (dataSource == null) {
            dataSource = getDataSource(getConfig(info));
        }
        return dataSource.getConnection();
    }

    private HikariConfig getConfig(MariaDbConnInfo info) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl("jdbc:mariadb://" + info.getUrl() +  ":" + info.getPort() + "/" + info.getDatabase());
        config.setUsername(info.getUsername());
        config.setPassword(info.getPassword());

        return config;
    }

    private DataSource getDataSource(HikariConfig config) {
        return new HikariDataSource(config);
    }
}
