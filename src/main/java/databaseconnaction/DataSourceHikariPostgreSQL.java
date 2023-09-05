package databaseconnaction;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceHikariPostgreSQL {
    private static HikariConfig config = new HikariConfig("/hikari.properties");
    private static HikariDataSource dataSource = new HikariDataSource(config);

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void setConfig(HikariConfig config) {
        DataSourceHikariPostgreSQL.config = config;
    }

    public static void setDataSource(HikariDataSource dataSource) {
        DataSourceHikariPostgreSQL.dataSource = dataSource;
    }

}


