package dao.impl;

import dao.interfaces.ActorDao;
import databaseconnaction.DataSourceConnection;
import exceptions.MySqlRuntimeException;
import models.Actor;
import models.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dao.impl.ActorDaoImpl.SQLTask.*;


public class ActorDaoImpl implements ActorDao {

    private DataSourceConnection dataSourceConnection;

    public ActorDaoImpl(DataSourceConnection dataSourceConnection) {
        this.dataSourceConnection = dataSourceConnection;
    }

    @Override
    public Actor addActor(Actor actor) {
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(INSERT_ACTOR.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, actor.getName());
            pst.setInt(2, actor.getAge());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    rs.next();
                    actor.setActorId(rs.getLong(1));
                }
                return actor;
            }
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при добавлении актера");
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при добавлени актера", e);
        }
    }

    @Override
    public Optional<Actor> findById(Long id) {
        Actor actor = null;
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(GET_ACTOR_BY_ID.QUERY)) {
            pst.setLong(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    actor = parseActorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении актера по id", e);
        }
        return Optional.ofNullable(actor);
    }

    @Override
    public List<Actor> findAll() {
        List<Actor> actors = new ArrayList<>();

        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(GET_ALL_ACTORS.QUERY);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                actors.add(parseActorFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении списка актеров", e);
        }
        return actors;
    }

    @Override
    public boolean deleteActorById(Long id) {
        int rowsUpdated;
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(DELETE_ACTOR_BY_ID.QUERY)) {
            pst.setLong(1, id);
            rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при удалении актера", e);
        }
    }

    @Override
    public Actor updateActorById(Actor actor) {
        int rowsUpdated;
        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(UPDATE_ACTOR_BY_ID.QUERY)) {
            pst.setString(1, actor.getName());
            pst.setInt(2, actor.getAge());
            pst.setLong(3, actor.getActorId());
            rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                return actor;
            } else {
                throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при обновлении данных об актере");
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при обновлении данных об актере", e);
        }
    }

    private Actor parseActorFromResultSet(ResultSet rs) {
        Actor actor = new Actor();
        try {
            actor.setActorId(rs.getLong("actor_id"));
            actor.setName(rs.getString("name"));
            actor.setAge(rs.getInt("age"));
            List<Movie> movieList = getActorMovies(actor.getActorId());
            actor.setMovieList(movieList);
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при парсинге Актера из ResultSet", e);
        }
        return actor;
    }

    private List<Movie> getActorMovies(Long actorId) {
        List<Movie> movieList = new ArrayList<>();

        try (Connection connection = dataSourceConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(FIND_AND_GET_ALL_MOVIES.QUERY)) {
            pst.setLong(1, actorId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie();
                    movie.setMovieId(rs.getLong("movie_id"));
                    movie.setDirectorId(rs.getLong("director_id"));
                    movie.setName(rs.getString("name"));
                    movie.setYearOfProduction(rs.getInt("year_of_production"));
                    movieList.add(movie);
                }
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении списка фильмов у актера", e);
        }
        return movieList;
    }

    enum SQLTask {
        INSERT_ACTOR("INSERT INTO actor (name, age) VALUES (?, ?)"),
        GET_ACTOR_BY_ID("SELECT * from actor where actor_id = ?"),
        DELETE_ACTOR_BY_ID("DELETE from actor where actor_id=?"),
        UPDATE_ACTOR_BY_ID("UPDATE actor set name=?, age=? where actor_id=?"),
        GET_ALL_ACTORS("SELECT * from actor"),
        FIND_AND_GET_ALL_MOVIES("SELECT m.movie_id, m.name, m.year_of_production, m.director_id FROM movie m JOIN movie_actor ma ON ma.movie_id = m.movie_id JOIN actor a ON ma.actor_id = a.actor_id WHERE a.actor_id = ?");

        String QUERY;

        SQLTask(String QUERY) {
            this.QUERY = QUERY;
        }
    }

    public ActorDaoImpl() {
    }
}
