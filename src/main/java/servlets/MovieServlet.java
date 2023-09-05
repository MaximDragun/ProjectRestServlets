package servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.interfaces.ActorDao;
import dao.interfaces.MovieDao;
import exceptions.MySqlRuntimeException;
import models.Actor;
import models.Movie;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@WebServlet("/movie")
public class MovieServlet extends HttpServlet {
    private ActorDao actorDao;
    private MovieDao movieDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        this.actorDao = (ActorDao) getServletContext().getAttribute("actorDao");
        this.movieDao = (MovieDao) getServletContext().getAttribute("movieDao");
        this.objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getQueryString();
        resp.setContentType("application/json; charset=UTF-8");
        try {
            if (pathInfo != null) {
                long id = Long.parseLong(req.getParameter("id"));
                if (movieDao.findById(id).isPresent()) {
                    Movie movie = movieDao.findById(id).get();
                    resp.setStatus(SC_OK);
                    objectMapper.writeValue(resp.getWriter(), movie);
                } else {
                    resp.sendError(SC_BAD_REQUEST, "movie под данным id в базе нет!");
                }
            } else {
                List<Movie> movieList = movieDao.findAll();
                resp.setStatus(SC_OK);
                objectMapper.writeValue(resp.getWriter(), movieList);
            }
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите id movie!!");

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String movieId = req.getParameter("movieId");
            String actorId = req.getParameter("actorId");

            if (movieId != null && actorId != null) {
                Long movieIdLong = Long.parseLong(movieId);
                Long actorIdLong = Long.parseLong(actorId);

                Movie movie = movieDao.findById(movieIdLong).orElse(null);
                Actor actor = actorDao.findById(actorIdLong).orElse(null);

                if (movie == null || actor == null) {
                    resp.sendError(SC_NOT_FOUND, "Таких фильмов и актеров в базе нет!");
                } else {
                    if (movieDao.insertMovieActor(movieIdLong, actorIdLong)) {
                        resp.setStatus(SC_OK);
                    } else
                        resp.sendError(SC_BAD_REQUEST, "Такая запись в базе уже есть!");
                }
            } else {
                Movie movie = objectMapper.readValue(req.getInputStream(), Movie.class);
                if (movie.getYearOfProduction() <= 1900) {
                    resp.sendError(SC_BAD_REQUEST, "Год должен быть больше 1900ого!");
                } else {
                    resp.setStatus(SC_OK);
                    objectMapper.writeValue(resp.getWriter(), movieDao.addMovie(movie));
                }
            }
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonProcessingException | NullPointerException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите как положено!\ndirectorId, name и year!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Movie movie = objectMapper.readValue(req.getInputStream(), Movie.class);
            if (movie == null) {
                resp.sendError(SC_BAD_REQUEST, "Поле запроса пустое");
                return;
            }
            if (movieDao.findById(movie.getMovieId()).isPresent()) {
                int age = movie.getYearOfProduction();
                if (age <= 1900) {
                    resp.sendError(SC_BAD_REQUEST, "Год должен быть больше 1900ого!");
                } else {
                    resp.setStatus(SC_OK);
                    objectMapper.writeValue(resp.getWriter(), movieDao.updateMovieById(movie));
                }
            } else
                resp.sendError(SC_NOT_FOUND, "movie с таким id в базе нет!");
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonProcessingException | NullPointerException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, "Введите корректный запрос!\nid directorId, name и year!");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String id = req.getParameter("id");
            if (id != null) {
                Long movieId = Long.parseLong(id);
                if (movieDao.findById(movieId).isPresent()) {
                    if (movieDao.deleteMovieById(movieId)) {
                        resp.setStatus(SC_OK);
                        resp.getWriter().println("movie успешно удален!");
                    } else {
                        resp.sendError(SC_INTERNAL_SERVER_ERROR, "Удалить movie не удалось!");
                    }
                } else {
                    resp.sendError(SC_NOT_FOUND, "movie с таким id в базе нет!");
                }
            } else
                resp.sendError(SC_BAD_REQUEST, "Введите корректный запрос! Введите id");
        } catch (JsonProcessingException | NullPointerException e) {
            resp.sendError(SC_BAD_REQUEST, "Введите корректный запрос! Введите id");
        } catch (MySqlRuntimeException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    public void setActorDao(ActorDao actorDao) {
        this.actorDao = actorDao;
    }


    public void setMovieDao(MovieDao movieDao) {
        this.movieDao = movieDao;
    }


    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

