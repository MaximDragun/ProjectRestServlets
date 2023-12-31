package services;


import models.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    Movie addMovie(Movie movie);

    Optional<Movie> findById(Long id);

    List<Movie> findAll();

    boolean deleteMovieById(Long id);

    Movie updateMovieById(Movie movie);

    boolean insertMovieActor(Long movieId, Long actorId);
}
