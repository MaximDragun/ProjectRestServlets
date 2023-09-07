package databaseconnaction;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataSourceConnaction {
    private final String dbUrl;
    private final String username;
    private final String password;
    private final String driver;

    public DataSourceConnaction() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("hikari.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла пропертей");
        }
        this.dbUrl = properties.getProperty("database.url");
        this.username = properties.getProperty("database.login");
        this.password = properties.getProperty("database.password");
        this.driver = properties.getProperty("driver.name");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Введите валидный драйвер!",e);
        }
    }

    public DataSourceConnaction(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
        this.driver = "org.postgresql.Driver";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(dbUrl, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при подключении к базе данных, проверьте настройки и подключения", e);
        }
    }
}
