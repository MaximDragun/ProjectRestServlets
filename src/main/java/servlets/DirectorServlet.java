package servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.interfaces.DirectorDao;
import exceptions.MySqlRuntimeException;
import models.Director;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@WebServlet("/director")
public class DirectorServlet extends HttpServlet {
    private DirectorDao directorDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        this.directorDao = (DirectorDao) getServletContext().getAttribute("directorDao");
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getQueryString();
        resp.setContentType("application/json; charset=UTF-8");
        try {
            if (pathInfo != null) {
                long id = Long.parseLong(req.getParameter("id"));
                if (directorDao.findById(id).isPresent()) {
                    Director director = directorDao.findById(id).get();
                    resp.setStatus(SC_OK);
                    objectMapper.writeValue(resp.getWriter(), director);
                } else {
                    resp.sendError(SC_BAD_REQUEST, "director под данным id в базе нет!");
                }
            } else {
                List<Director> directorList = directorDao.findAll();
                resp.setStatus(SC_OK);
                objectMapper.writeValue(resp.getWriter(), directorList);
            }
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите id director!");

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Director director = objectMapper.readValue(req.getInputStream(), Director.class);
            if (director.getAge() < 18 || director.getAge() > 100) {
                resp.sendError(SC_BAD_REQUEST, "Введите возраст больше 18 и меньше 100!");
            } else {
                resp.setStatus(SC_OK);
                objectMapper.writeValue(resp.getWriter(), directorDao.addDirector(director));
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
            Director director = objectMapper.readValue(req.getInputStream(), Director.class);
            if (director == null) {
                resp.sendError(SC_BAD_REQUEST, "Поле запроса пустое");
                return;
            }
            if (directorDao.findById(director.getDirectorId()).isPresent()) {
                int age = director.getAge();
                if (age < 18 || age > 100) {
                    resp.sendError(SC_BAD_REQUEST, "Введите возраст больше 18 и меньше 100!");
                } else {
                    resp.setStatus(SC_OK);
                    objectMapper.writeValue(resp.getWriter(), directorDao.updateDirectorById(director));
                }
            } else {
                resp.sendError(SC_NOT_FOUND, "director с таким id в базе нет!");
            }
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
                Long directorId = Long.parseLong(id);
                if (directorDao.findById(directorId).isPresent()) {
                    if (directorDao.deleteDirectorById(directorId)) {
                        resp.setStatus(SC_OK);
                        resp.getWriter().println("director успешно удален!");
                    } else {
                        resp.sendError(SC_INTERNAL_SERVER_ERROR, "Удалить director не удалось!");
                    }
                } else {
                    resp.sendError(SC_NOT_FOUND, "director с таким id в базе нет!");
                }
            } else
                resp.sendError(SC_BAD_REQUEST, "Введите корректный запрос! Введите id director");
        } catch (JsonProcessingException | NullPointerException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите корректный запрос! Введите id director");
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public DirectorDao getDirectorDao() {
        return directorDao;
    }

    public void setDirectorDao(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

