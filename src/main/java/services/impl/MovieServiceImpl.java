package services.impl;

import dao.interfaces.MovieDao;
import models.Movie;
import services.MovieService;

import java.util.List;
import java.util.Optional;

public class MovieServiceImpl implements MovieService {
    private final MovieDao movieDao;

    public MovieServiceImpl(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    @Override
    public Movie addMovie(Movie movie) {
        return movieDao.addMovie(movie);
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieDao.findById(id);
    }

    @Override
    public List<Movie> findAll() {
        return movieDao.findAll();
    }

    @Override
    public boolean deleteMovieById(Long id) {
        return movieDao.deleteMovieById(id);
    }

    @Override
    public Movie updateMovieById(Movie movie) {
        return movieDao.updateMovieById(movie);
    }

    @Override
    public boolean insertMovieActor(Long movieId, Long actorId) {
        return movieDao.insertMovieActor(movieId,actorId);
    }
}
