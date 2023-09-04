package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dao.impl.ActorDaoImpl;
import dao.interfaces.ActorDao;
import databaseconnaction.DataSourceHikariPostgreSQL;
import models.Actor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

class ActorDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("project2")
            .withUsername("postgres")
            .withPassword("maxim")
            .withInitScript("db/migration/V1__Init_DB.sql");

    ActorDao actorDao;

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
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        DataSourceHikariPostgreSQL.setConfig(config);
        DataSourceHikariPostgreSQL.setDataSource(new HikariDataSource(config));
        actorDao = new ActorDaoImpl();
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM actor");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createActorTest() {
        actorDao.addActor(new Actor("Brad Pitt", 59));
        actorDao.addActor(new Actor("Jennifer Aniston", 51));

        List<Actor> actors = actorDao.findAll();

        Assertions.assertEquals(2, actors.size());
    }


    @Test
    void findOptionalByIdActorTest() {
        Actor newActor = actorDao.addActor(new Actor("Max Dragun", 25));

        Optional<Actor> actor = actorDao.findById(newActor.getActorId());

        Assertions.assertNotNull(actor);
        Assertions.assertTrue(actor.isPresent());
    }

    @Test
    void updateActorTest() {
        Actor newActor = actorDao.addActor(new Actor("Max Dragun", 25));

        newActor = actorDao.updateActorById(new Actor(newActor.getActorId(), "Maximus", 27));

        Assertions.assertEquals("Maximus", newActor.getName());
    }

    @Test
    void findAllActorTest() {
        actorDao.addActor(new Actor("Max Dragun", 25));
        actorDao.addActor(new Actor("Jennifer Aniston", 27));
        actorDao.addActor(new Actor("Brad Pitt", 21));

        List<Actor> actors = actorDao.findAll();

        Assertions.assertEquals(3, actors.size());
    }

    @Test
    void deleteActorTest() {
        Actor newActor1 = actorDao.addActor(new Actor("Max Dragun", 25));
        actorDao.addActor(new Actor("Jennifer Aniston", 27));
        actorDao.addActor(new Actor("Brad Pitt", 21));

        boolean deleted = actorDao.deleteActorById(newActor1.getActorId());
        List<Actor> actors = actorDao.findAll();

        Assertions.assertEquals(2, actors.size());
        Assertions.assertTrue(deleted);
    }
}
