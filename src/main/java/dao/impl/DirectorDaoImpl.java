package dao.impl;

import dao.interfaces.DirectorDao;
import databaseconnaction.DataSourceHikariPostgreSQL;
import exceptions.MySqlRuntimeException;
import models.Director;
import models.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dao.impl.DirectorDaoImpl.SQLTask.*;

public class DirectorDaoImpl implements DirectorDao {

    @Override
    public Director addDirector(Director director) {
        try (Connection connection = DataSourceHikariPostgreSQL.getConnection();
             PreparedStatement pst = connection.prepareStatement(INSERT_DIRECTOR.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, director.getName());
            pst.setInt(2, director.getAge());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    rs.next();
                    director.setDirectorId(rs.getLong(1));
                }
                return director;
            }
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при добавлении режиссера");
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при добавлени режиссера", e);
        }
    }

    @Override
    public Optional<Director> findById(Long id) {
        Director director = null;
        try (Connection connection = DataSourceHikariPostgreSQL.getConnection();
             PreparedStatement pst = connection.prepareStatement(GET_DIRECTOR_BY_ID.QUERY)) {
            pst.setLong(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    director = parseDirectorFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении режиссера по id", e);
        }
        return Optional.ofNullable(director);
    }


    @Override
    public List<Director> findAll() {
        List<Director> directors = new ArrayList<>();

        try (Connection connection = DataSourceHikariPostgreSQL.getConnection();
             PreparedStatement pst = connection.prepareStatement(GET_ALL_DIRECTORS.QUERY);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                directors.add(parseDirectorFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении списка режиссеров", e);
        }
        return directors;
    }

    @Override
    public boolean deleteDirectorById(Long id) {
        int rowsUpdated;
        try (Connection connection = DataSourceHikariPostgreSQL.getConnection();
             PreparedStatement pst = connection.prepareStatement(DELETE_DIRECTOR_BY_ID.QUERY)) {
            pst.setLong(1, id);
            rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при удалении режиссера", e);
        }
    }

    @Override
    public Director updateDirectorById(Director director) {
        int rowsUpdated;
        try (Connection connection = DataSourceHikariPostgreSQL.getConnection();
             PreparedStatement pst = connection.prepareStatement(UPDATE_DIRECTOR_BY_ID.QUERY)) {
            pst.setString(1, director.getName());
            pst.setInt(2, director.getAge());
            pst.setLong(3, director.getDirectorId());
            rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                return director;
            } else {
                throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при обновлении данных о режиссере");
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при обновлении данных о режиссере", e);
        }
    }

    private Director parseDirectorFromResultSet(ResultSet rs) {
        Director director = new Director();

        try {
            director.setDirectorId(rs.getLong("director_id"));
            director.setName(rs.getString("name"));
            director.setAge(rs.getInt("age"));
            List<Movie> movieList = getDirectorMovies(director.getDirectorId());
            director.setMovieList(movieList);
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных", e);
        }
        return director;
    }

    private List<Movie> getDirectorMovies(Long directorId) {
        List<Movie> movieList = new ArrayList<>();

        try (Connection connection = DataSourceHikariPostgreSQL.getConnection();
             PreparedStatement pst = connection.prepareStatement(FIND_ALL_MOVIES.QUERY)) {
            pst.setLong(1, directorId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie();
                    movie.setMovieId(rs.getLong("movie_id"));
                    movie.setName(rs.getString("name"));
                    movie.setYearOfProduction(rs.getInt("year_of_production"));
                    movie.setDirectorId(rs.getLong("director_id"));
                    movieList.add(movie);
                }
            }
        } catch (SQLException e) {
            throw new MySqlRuntimeException("Возникла непредвиденная ошибка во время работы с базой данных при получении списка фильмов у режиссера", e);
        }
        return movieList;
    }

    enum SQLTask {
        INSERT_DIRECTOR("INSERT INTO director (name, age) VALUES (?, ?)"),
        GET_DIRECTOR_BY_ID("SELECT * from director where director_id = ?"),
        DELETE_DIRECTOR_BY_ID("DELETE from director where director_id=?"),
        UPDATE_DIRECTOR_BY_ID("UPDATE director set name=?, age=? where director_id=?"),
        GET_ALL_DIRECTORS("SELECT * from director"),
        FIND_ALL_MOVIES("SELECT m.movie_id, m.name, m.year_of_production, m.director_id FROM movie m JOIN director d ON m.director_id = d.director_id WHERE d.director_id = ?");

        String QUERY;

        SQLTask(String QUERY) {
            this.QUERY = QUERY;
        }
    }


}
