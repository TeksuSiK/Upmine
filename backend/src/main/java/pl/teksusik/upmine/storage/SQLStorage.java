package pl.teksusik.upmine.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class SQLStorage implements Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLStorage.class);

    private final HikariDataSource dataSource;

    public SQLStorage(String jdbcUrl) {
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(jdbcUrl);

        try {
            this.dataSource.getConnection();
        } catch (SQLException exception) {
            LOGGER.error("An error occurred while connecting to the database", exception);
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
