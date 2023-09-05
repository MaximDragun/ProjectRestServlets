package servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import dao.impl.ActorDaoImpl;
import dao.impl.MovieDaoImpl;
import models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServletTest {
    @Mock
    private MovieDaoImpl movieDao;
    @Mock
    private ActorDaoImpl actorDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    private MovieServlet movieServlet;

    @BeforeEach
    public void setUp() {
        movieServlet = new MovieServlet();
        movieServlet.setObjectMapper(new ObjectMapper());
        movieServlet.setMovieDao(movieDao);
        movieServlet.setActorDao(actorDao);
        StringWriter stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testDoGetAllMovies() throws IOException {
        when(request.getQueryString()).thenReturn(null);

        List<Movie> movies = Arrays.asList(
                new Movie(1L, "The Shawshank Redemption", 1994),
                new Movie(2L, "The Godfather", 1972)
        );

        when(movieDao.findAll()).thenReturn(movies);
        when(response.getWriter()).thenReturn(writer);

        movieServlet.doGet(request, response);

        verify(response).setContentType("application/json; charset=UTF-8");

        List<Movie> movieList = movieDao.findAll();
        assertEquals(movies.size(), movieList.size());
    }

    @Test
    void testDoGetMovieWithId() throws IOException {
        when(request.getQueryString()).thenReturn("1");
        when(request.getParameter("id")).thenReturn("1");
        Movie movie = new Movie(1L, "The Shawshank Redemption", 1994);

        when(movieDao.findById(1L)).thenReturn(Optional.ofNullable(movie));
        when(response.getWriter()).thenReturn(writer);

        movieServlet.doGet(request, response);

        verify(response).setContentType("application/json; charset=UTF-8");

        Movie optionalMovie = movieDao.findById(1L).orElse(null);
        assertEquals(movie.getName(), optionalMovie.getName());
    }

    @Test
    void testDoDeleteMovie() throws IOException {
        when(request.getParameter("id")).thenReturn(Long.valueOf(1L).toString());
        when(response.getWriter()).thenReturn(writer);

        Movie movie = new Movie(1L, "The Shawshank Redemption", 1994);
        when(movieDao.findById(1L)).thenReturn(Optional.ofNullable(movie));
        when(movieDao.deleteMovieById(1L)).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        movieServlet.doDelete(request, response);

        verify(response).setStatus(SC_OK);
        assertEquals("movie успешно удален!\r\n", stringWriter.toString());
    }
}
