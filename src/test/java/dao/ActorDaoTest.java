package dao;

import dao.impl.ActorDaoImpl;
import dao.interfaces.ActorDao;
import databaseconnaction.DataSourceConnection;
import models.Actor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActorDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("project2")
            .withUsername("postgres")
            .withPassword("maxim")
            .withInitScript("db/NewTables.sql");

    ActorDao actorDao;
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
        actorDao = new ActorDaoImpl(dataSourceConnection);
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = dataSourceConnection.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM actor");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addActorTest() {
        actorDao.addActor(new Actor("Brad Pitt", 59));
        actorDao.addActor(new Actor("Jennifer Aniston", 51));

        List<Actor> actors = actorDao.findAll();

        assertEquals(2, actors.size());
    }

    @Test
    void findOptionalByIdActorTest() {
        Actor newActor = actorDao.addActor(new Actor("Max Dragun", 25));

        Optional<Actor> actor = actorDao.findById(newActor.getActorId());

        assertNotNull(actor);
        assertTrue(actor.isPresent());
    }

    @Test
    void updateActorTest() {
        Actor newActor = actorDao.addActor(new Actor("Max Dragun", 25));

        newActor = actorDao.updateActorById(new Actor(newActor.getActorId(), "Maximus", 27));

        assertEquals("Maximus", newActor.getName());
    }

    @Test
    void findAllActorTest() {
        actorDao.addActor(new Actor("Max Dragun", 25));
        actorDao.addActor(new Actor("Jennifer Aniston", 27));
        actorDao.addActor(new Actor("Brad Pitt", 21));

        List<Actor> actors = actorDao.findAll();

        assertEquals(3, actors.size());
    }

    @Test
    void deleteActorTest() {
        Actor newActor1 = actorDao.addActor(new Actor("Max Dragun", 25));
        actorDao.addActor(new Actor("Jennifer Aniston", 27));
        actorDao.addActor(new Actor("Brad Pitt", 21));

        boolean deleted = actorDao.deleteActorById(newActor1.getActorId());
        List<Actor> actors = actorDao.findAll();

        assertEquals(2, actors.size());
        assertTrue(deleted);
    }
}
