package services;


import dao.interfaces.MovieDao;
import models.Actor;
import models.Director;
import models.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import services.impl.MovieServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
    @Mock
    MovieDao movieDao;
    MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieServiceImpl(movieDao);
    }

    @Test
    void testFindOptionalMovieById() {
        Movie movie = new Movie();
        movie.setMovieId(1L);
        movie.setName("Max");
        movie.setYearOfProduction(1999);
        movie.setDirectorId(1L);

        when(movieDao.findById(1L)).thenReturn(Optional.of(movie));

        assertEquals(Optional.of(movie), movieService.findById(1L));
    }

    @Test
    void testFindAllMovies() {
        List<Movie> movieList = new ArrayList<>();
        Movie movie1 = new Movie();
        movie1.setMovieId(1L);
        movie1.setName("Max");
        movie1.setYearOfProduction(1999);
        movie1.setDirectorId(1L);

        Movie movie2 = new Movie();
        movie2.setMovieId(2L);
        movie2.setName("Egor");
        movie2.setYearOfProduction(1998);
        movie2.setDirectorId(2L);
        movieList.add(movie1);
        movieList.add(movie2);

        when(movieDao.findAll()).thenReturn(movieList);

        assertEquals(movieList, movieService.findAll());
    }

    @Test
    void testAddMovie() {
        Movie movie = new Movie();
        movie.setMovieId(1L);
        movie.setName("Max");
        movie.setYearOfProduction(1999);
        movie.setDirectorId(1L);

        when(movieDao.addMovie(movie)).thenReturn(movie);

        assertEquals(movie, movieService.addMovie(movie));
    }

    @Test
    void testUpdateMovie() {
        Movie movie = new Movie();
        movie.setMovieId(1L);
        movie.setName("Max");
        movie.setYearOfProduction(1999);
        movie.setDirectorId(1L);

        when(movieDao.updateMovieById(movie)).thenReturn(movie);

        assertEquals(movie, movieService.updateMovieById(movie));
    }

    @Test
    void testDeleteMovie() {
        when(movieDao.deleteMovieById(1L)).thenReturn(true);

        assertTrue(movieService.deleteMovieById(1L));
    }

    @Test
    void insertMovieActorTest() {
        when(movieDao.insertMovieActor(1L,1L)).thenReturn(true);

        assertTrue(movieService.insertMovieActor(1L,1L));
    }
}
