package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.impl.ActorDaoImpl;
import models.Actor;
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
class ActorServletTest {
    @Mock
    private ActorDaoImpl actorDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    private ActorServlet actorServlet;

    @BeforeEach
    public void setUp() {
        actorServlet = new ActorServlet();
        actorServlet.setObjectMapper(new ObjectMapper());
        actorServlet.setActorDao(actorDao);
        StringWriter stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @Test
    void testDoGetAllActors() throws IOException {
        when(request.getQueryString()).thenReturn(null);

        List<Actor> actorList = Arrays.asList(
                new Actor(1L, "Brad Pitt", 59),
                new Actor(2L, "Jennifer Aniston", 51)
        );

        when(actorDao.findAll()).thenReturn(actorList);
        when(response.getWriter()).thenReturn(writer);

        actorServlet.doGet(request, response);

        verify(response).setContentType("application/json; charset=UTF-8");

        List<Actor> actors = actorDao.findAll();
        assertEquals(actorList.size(), actors.size());
    }

    @Test
    void testDoGetActorWithId() throws IOException {
        when(request.getQueryString()).thenReturn("1");
        when(request.getParameter("id")).thenReturn("1");
        Actor actor = new Actor(1L, "Brad Pitt", 59);

        when(actorDao.findById(1L)).thenReturn(Optional.ofNullable(actor));
        when(response.getWriter()).thenReturn(writer);

        actorServlet.doGet(request, response);

        verify(response).setContentType("application/json; charset=UTF-8");

        Actor optionalActor = actorDao.findById(1L).orElse(null);
        assertEquals(actor.getName(), optionalActor.getName());
    }

    @Test
    void testDoDeleteActor() throws IOException {
        when(request.getParameter("id")).thenReturn(Long.valueOf(1L).toString());
        when(response.getWriter()).thenReturn(writer);

        Actor actor = new Actor(1L, "Brad Pitt", 59);
        when(actorDao.findById(1L)).thenReturn(Optional.ofNullable(actor));
        when(actorDao.deleteActorById(1L)).thenReturn(true);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        actorServlet.doDelete(request, response);

        verify(response).setStatus(SC_OK);
        assertEquals("actor успешно удален!\r\n", stringWriter.toString());
    }
}
