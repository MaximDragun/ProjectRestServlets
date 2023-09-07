package servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.MySqlRuntimeException;
import models.Actor;
import services.ActorService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@WebServlet("/actor")
public class ActorServlet extends HttpServlet {
    private ActorService actorService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        this.actorService = (ActorService) getServletContext().getAttribute("actorService");
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getQueryString();
        resp.setContentType("application/json; charset=UTF-8");
        try {
            if (pathInfo != null) {
                long id = Long.parseLong(req.getParameter("id"));
                if (actorService.findById(id).isPresent()) {
                    resp.setStatus(SC_OK);
                    objectMapper.writeValue(resp.getWriter(), actorService.findById(id).get());
                } else {
                    resp.sendError(SC_BAD_REQUEST, "actor под данным id в базе нет!");
                }
            } else {
                List<Actor> actorList = actorService.findAll();
                resp.setStatus(SC_OK);
                objectMapper.writeValue(resp.getWriter(), actorList);
            }
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите id actor!");

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Actor actor = objectMapper.readValue(req.getInputStream(), Actor.class);
            if (actor.getAge() < 18 || actor.getAge() > 100) {
                resp.sendError(SC_BAD_REQUEST, "Введите возраст больше 18 и меньше 100!");
            } else {
                resp.setStatus(SC_OK);
                objectMapper.writeValue(resp.getWriter(), actorService.addActor(actor));
            }
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonProcessingException | NullPointerException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите как положено!\nname и age!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Actor actor = objectMapper.readValue(req.getInputStream(), Actor.class);
            if (actor == null) {
                resp.sendError(SC_BAD_REQUEST, "Поле запроса пустое");
                return;
            }
            if (actorService.findById(actor.getActorId()).isPresent()) {
                int age = actor.getAge();
                if (age < 18 || age > 100) {
                    resp.sendError(SC_BAD_REQUEST, "Введите возраст больше 18 и меньше 100!");
                } else {
                    resp.setStatus(SC_OK);
                    objectMapper.writeValue(resp.getWriter(), actorService.updateActorById(actor));
                }
            } else
                resp.sendError(SC_NOT_FOUND, "actor с таким id в базе нет!");
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonProcessingException | NullPointerException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, "Введите корректный запрос!\nid name и age!");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String id = req.getParameter("id");
            if (id != null) {
                Long actorId = Long.parseLong(id);
                if (actorService.findById(actorId).isPresent()) {
                    if (actorService.deleteActorById(actorId)) {
                        resp.setStatus(SC_OK);
                        resp.getWriter().println("actor успешно удален!");
                    } else {
                        resp.sendError(SC_INTERNAL_SERVER_ERROR, "Удалить actor не удалось!");
                    }
                } else {
                    resp.sendError(SC_NOT_FOUND, "actor с таким id в базе нет!");
                }
            } else
                resp.sendError(SC_BAD_REQUEST, "Введите корректный запрос! Введите id actor");
        } catch (JsonProcessingException | NullPointerException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите корректный запрос! Введите id actor");
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void setActorService(ActorService actorService) {
        this.actorService = actorService;
    }


    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

