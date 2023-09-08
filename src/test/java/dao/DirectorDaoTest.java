package dao;

import dao.impl.DirectorDaoImpl;
import dao.interfaces.DirectorDao;
import databaseconnaction.DataSourceConnection;
import models.Director;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

class DirectorDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("project2")
            .withUsername("postgres")
            .withPassword("maxim")
            .withInitScript("db/NewTables.sql");

    DirectorDao directorDao;
    DataSourceConnection dataSourceConnection;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        dataSourceConnection = new DataSourceConnection(postgres.getJdbcUrl(),
                postgres.getUsername(), postgres.getPassword());
        directorDao = new DirectorDaoImpl(dataSourceConnection);
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = dataSourceConnection.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM director");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addDirectorTest() {
        directorDao.addDirector(new Director("Stanley Kubrick", 56));
        directorDao.addDirector(new Director("Alfred Hitchcock", 78));

        List<Director> directors = directorDao.findAll();

        Assertions.assertEquals(2, directors.size());
    }


    @Test
    void findOptionalByIdDirectorTest() {
        Director director = directorDao.addDirector(new Director("Akira Kurosawa", 76));

        Optional<Director> optionalDirector = directorDao.findById(director.getDirectorId());

        Assertions.assertNotNull(optionalDirector);
        Assertions.assertTrue(optionalDirector.isPresent());
    }

    @Test
    void updateDirectorTest() {
        Director director = directorDao.addDirector(new Director("Martin Scorsese", 77));

        director = directorDao.updateDirectorById(new Director(director.getDirectorId(), "Charles Chaplin", 58));

        Assertions.assertEquals(58, director.getAge());
    }

    @Test
    void findAllDirectorTest() {
        directorDao.addDirector(new Director("Steven Spielberg", 79));
        directorDao.addDirector(new Director("Andrei Tarkovsky", 81));

        List<Director> directors = directorDao.findAll();

        Assertions.assertEquals(2, directors.size());
    }

    @Test
    void deleteDirectorTest() {
        Director director1 = directorDao.addDirector(new Director("David Lynch", 68));
        directorDao.addDirector(new Director("Francis Ford Coppola", 57));
        directorDao.addDirector(new Director("Woody Allen", 63));

        boolean deleted = directorDao.deleteDirectorById(director1.getDirectorId());
        List<Director> directors = directorDao.findAll();

        Assertions.assertEquals(2, directors.size());
        Assertions.assertTrue(deleted);
    }
}
