package servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import dao.impl.DirectorDaoImpl;
import models.Director;
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

import static javax.servlet.http.HttpServletResponse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectorServletTest {
    @Mock
    private DirectorDaoImpl directorDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    private DirectorServlet directorServlet;

    @BeforeEach
    public void setUp() {
        directorServlet = new DirectorServlet();
        directorServlet.setObjectMapper(new ObjectMapper());
        directorServlet.setDirectorDao(directorDao);
        StringWriter stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testDoGetAllDirectors() throws IOException {
        when(request.getQueryString()).thenReturn(null);

        List<Director> directors = Arrays.asList(
                new Director(1L,"Stanley Kubrick", 56),
                new Director(2L,"Alfred Hitchcock", 78)
        );

        when(directorDao.findAll()).thenReturn(directors);
        when(response.getWriter()).thenReturn(writer);

        directorServlet.doGet(request, response);

        verify(response).setContentType("application/json; charset=UTF-8");

        List<Director> directorList = directorDao.findAll();
        assertEquals(directors.size(), directorList.size());
    }

    @Test
    void testDoGetDirectorWithId() throws IOException {
        when(request.getQueryString()).thenReturn("1");
        when(request.getParameter("id")).thenReturn("1");
        Director director = new Director(1L,"Stanley Kubrick", 56);

        when(directorDao.findById(1L)).thenReturn(Optional.ofNullable(director));
        when(response.getWriter()).thenReturn(writer);

        directorServlet.doGet(request, response);

        verify(response).setContentType("application/json; charset=UTF-8");

        Director optionalDirector = directorDao.findById(1L).orElse(null);
        assertEquals(director.getName(), optionalDirector.getName());
    }

    @Test
    void testDeleteDirector() throws IOException {
        when(request.getParameter("id")).thenReturn(Long.valueOf(1L).toString());
        when(response.getWriter()).thenReturn(writer);

        Director director =  new Director(1L,"Stanley Kubrick", 56);
        when(directorDao.findById(1L)).thenReturn(Optional.ofNullable(director));
        when(directorDao.deleteDirectorById(1L)).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        directorServlet.doDelete(request, response);

        verify(response).setStatus(SC_OK);
        assertEquals("director успешно удален!\r\n", stringWriter.toString());
    }
}
