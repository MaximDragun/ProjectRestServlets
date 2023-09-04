package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dao.impl.ActorDaoImpl;
import dao.impl.DirectorDaoImpl;
import dao.impl.MovieDaoImpl;
import dao.interfaces.ActorDao;
import dao.interfaces.DirectorDao;
import dao.interfaces.MovieDao;
import databaseconnaction.DataSourceHikariPostgreSQL;
import models.Actor;
import models.Director;
import models.Movie;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

class MovieDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("project2")
            .withUsername("postgres")
            .withPassword("maxim")
            .withInitScript("db/migration/V1__Init_DB.sql");

    DirectorDao directorDao;
    MovieDao movieDao;
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
        directorDao = new DirectorDaoImpl();
        movieDao = new MovieDaoImpl();
        actorDao = new ActorDaoImpl();
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM director");
            statement.executeUpdate("DELETE FROM actor");
            statement.executeUpdate("DELETE FROM movie");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createMovieTest() {
        Director director = directorDao.addDirector(new Director("Woody Allen", 76));
        movieDao.addMovie(new Movie(director.getDirectorId(), "The Shawshank Redemption", 1994));
        movieDao.addMovie(new Movie(director.getDirectorId(), "The Godfather", 1972));

        List<Movie> movies = movieDao.findAll();

        Assertions.assertEquals(2, movies.size());
    }


    @Test
    void findOptionalByIdMovieTest() {
        Director director = directorDao.addDirector(new Director("Woody Allen", 76));
        Movie movie = movieDao.addMovie(new Movie(director.getDirectorId(), "The Shawshank Redemption", 1994));

        Optional<Movie> optionalMovie = movieDao.findById(movie.getMovieId());

        Assertions.assertNotNull(optionalMovie);
        Assertions.assertTrue(optionalMovie.isPresent());
    }

    @Test
    void updateMovieTest() {
        Director director = directorDao.addDirector(new Director("Woody Allen", 76));
        Movie movie = movieDao.addMovie(new Movie(director.getDirectorId(), "The Shawshank Redemption", 1994));

        movie = movieDao.updateMovieById(new Movie(movie.getMovieId(), director.getDirectorId(), "The Godfather", 1972));

        Assertions.assertEquals(1972, movie.getYearOfProduction());
    }

    @Test
    void findAllMoviesTest() {
        Director director = directorDao.addDirector(new Director("Woody Allen", 76));
        movieDao.addMovie(new Movie(director.getDirectorId(), "The Shawshank Redemption", 1994));
        movieDao.addMovie(new Movie(director.getDirectorId(), "The Godfather", 1972));
        movieDao.addMovie(new Movie(director.getDirectorId(), "The Dark Knight", 2008));

        List<Movie> movies = movieDao.findAll();

        Assertions.assertEquals(3, movies.size());
    }

    @Test
    void deleteMovieTest() {
        Director director = directorDao.addDirector(new Director("Woody Allen", 76));
        movieDao.addMovie(new Movie(director.getDirectorId(), "The Shawshank Redemption", 1994));
        Movie movie = movieDao.addMovie(new Movie(director.getDirectorId(), "The Godfather", 1972));
        movieDao.addMovie(new Movie(director.getDirectorId(), "The Dark Knight", 2008));

        boolean deleted = movieDao.deleteMovieById(movie.getMovieId());
        List<Movie> movieList = movieDao.findAll();

        Assertions.assertEquals(2, movieList.size());
        Assertions.assertTrue(deleted);
    }

    @Test
    void setMovieActorTest() {
        Director director = directorDao.addDirector(new Director("Francis Ford Coppola", 76));
        Actor actor = actorDao.addActor(new Actor("Marlon Brando",67));
        Movie movie = movieDao.addMovie(new Movie(director.getDirectorId(), "The Godfather", 1972));

        boolean result = movieDao.insertMovieActor(movie.getMovieId(), actor.getActorId());

        Assertions.assertTrue(result);
    }
}
