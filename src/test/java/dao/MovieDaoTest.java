package dao;

import dao.impl.ActorDaoImpl;
import dao.impl.DirectorDaoImpl;
import dao.impl.MovieDaoImpl;
import dao.interfaces.ActorDao;
import dao.interfaces.DirectorDao;
import dao.interfaces.MovieDao;
import databaseconnaction.DataSourceConnection;
import models.Actor;
import models.Director;
import models.Movie;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

class MovieDaoTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("project2")
            .withUsername("postgres")
            .withPassword("maxim")
            .withInitScript("db/NewTables.sql");

    DirectorDao directorDao;
    MovieDao movieDao;
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
        directorDao = new DirectorDaoImpl(dataSourceConnection);
        movieDao = new MovieDaoImpl(dataSourceConnection);
        actorDao = new ActorDaoImpl(dataSourceConnection);
    }

    @AfterEach
    void clearDatabase() {
        try (Connection connection = dataSourceConnection.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM director");
            statement.executeUpdate("DELETE FROM actor");
            statement.executeUpdate("DELETE FROM movie");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addMovieTest() {
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
    void insertMovieActorTest() {
        Director director = directorDao.addDirector(new Director("Francis Ford Coppola", 76));
        Actor actor = actorDao.addActor(new Actor("Marlon Brando", 67));
        Movie movie = movieDao.addMovie(new Movie(director.getDirectorId(), "The Godfather", 1972));

        boolean result = movieDao.insertMovieActor(movie.getMovieId(), actor.getActorId());

        Assertions.assertTrue(result);
    }
}
