package dao.impl;

import dao.interfaces.MovieDao;
import databaseconnaction.DataSourceConnection;
import exceptions.MySqlRuntimeException;
import models.Actor;
import models.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dao.impl.MovieDaoImpl.SQLTask.*;

public class MovieDaoImpl implements MovieDao {

    private DataSourceConnection dataSourceConnection;

    public MovieDaoImpl(DataSourceConnection dataSourceConnection) {
        this.dataSourceConnection = dataSourceConnection;
    }

    @Override
    public Movie addMovie(Movie movie) {
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(INSERT_MOVIE.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, movie.getName());
            pst.setInt(2, movie.getYearOfProduction());
            pst.setLong(3, movie.getDirectorId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    rs.next();
                    movie.setMovieId(rs.getLong(1));
                }
                return movie;
            }
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при добавлении фильма");
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при добавлени фильма", e);
        }
    }

    @Override
    public Optional<Movie> findById(Long id) {
        Movie movie = null;
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(GET_MOVIE_BY_ID.QUERY)) {
            pst.setLong(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    movie = parseMovieFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении фильма по id", e);
        }
        return Optional.ofNullable(movie);
    }

    @Override
    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();

        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(GET_ALL_MOVIES.QUERY);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                movies.add(parseMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении списка фильмов", e);
        }
        return movies;
    }

    @Override
    public boolean deleteMovieById(Long id) {
        int rowsUpdated;
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(DELETE_MOVIE_BY_ID.QUERY)) {
            pst.setLong(1, id);
            rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при удалении фильма", e);
        }
    }

    @Override
    public Movie updateMovieById(Movie movie) {
        int rowsUpdated;
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(UPDATE_MOVIE_BY_ID.QUERY)) {
            pst.setString(1, movie.getName());
            pst.setInt(2, movie.getYearOfProduction());
            pst.setLong(3, movie.getDirectorId());
            pst.setLong(4, movie.getMovieId());
            rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                return movie;
            } else {
                throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при обновлении данных о фильме");
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при обновлении данных о фильме", e);
        }
    }


    private Movie parseMovieFromResultSet(ResultSet rs) {

        Movie movie = new Movie();

        try {
            movie.setMovieId(rs.getLong("movie_id"));
            movie.setName(rs.getString("name"));
            movie.setYearOfProduction(rs.getInt("year_of_production"));
            movie.setDirectorId(rs.getLong("director_id"));
            List<Actor> actorList = getMovieActors(movie.getMovieId());
            movie.setActorList(actorList);
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при парсинге фильма из ResultSet", e);
        }
        return movie;
    }

    private List<Actor> getMovieActors(Long movieId) {
        List<Actor> actorList = new ArrayList<>();

        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(FIND_ALL_ACTORS.QUERY)) {
            pst.setLong(1, movieId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Actor actor = new Actor();
                    actor.setActorId(rs.getLong("actor_id"));
                    actor.setName(rs.getString("name"));
                    actor.setAge(rs.getInt("age"));
                    actorList.add(actor);
                }
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении списка актеров у фильма", e);
        }
        return actorList;
    }

    @Override
    public boolean insertMovieActor(Long movieId, Long actorId) {
        boolean result = false;
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(INSERT_MOVIE_ACTOR.QUERY)) {

            if (!isActorAndMovieExist(movieId, actorId)) {
                pst.setLong(1, actorId);
                pst.setLong(2, movieId);
                pst.executeUpdate();
                result = true;
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при добавлении записи в линковочную таблицу", e);
        }
        return result;
    }

    private boolean isActorAndMovieExist(Long movieId, Long actorId) {
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(FIND_MOVIE_ACTOR.QUERY)) {
            pst.setLong(1, movieId);
            pst.setLong(2, actorId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при проверке существования записи с линковочной таблице", e);
        }
    }

    enum SQLTask {
        INSERT_MOVIE("INSERT INTO movie (name, year_of_production, director_id) VALUES (?, ?, ?)"),
        GET_MOVIE_BY_ID("SELECT * from movie where movie_id = ?"),
        DELETE_MOVIE_BY_ID("DELETE from movie where movie_id=?"),
        UPDATE_MOVIE_BY_ID("UPDATE movie set name=?, year_of_production=?, director_id=? where movie_id=?"),
        GET_ALL_MOVIES("SELECT * from movie"),
        FIND_ALL_ACTORS("SELECT a.actor_id, a.name, a.age FROM actor a JOIN movie_actor ma ON ma.actor_id = a.actor_id JOIN movie m ON ma.movie_id = m.movie_id WHERE m.movie_id = ?"),
        INSERT_MOVIE_ACTOR("INSERT INTO movie_actor (actor_id, movie_id) VALUES (?, ?)"),
        FIND_MOVIE_ACTOR("SELECT * FROM movie_actor WHERE movie_id = ? AND actor_id = ?");

        String QUERY;

        SQLTask(String QUERY) {
            this.QUERY = QUERY;
        }
    }

    public MovieDaoImpl() {
    }
}
