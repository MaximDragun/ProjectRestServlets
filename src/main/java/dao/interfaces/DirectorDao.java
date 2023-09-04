package dao.interfaces;

import models.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    Director addDirector(Director director);

    Optional<Director> findById(Long id);

    List<Director> findAll();

    boolean deleteDirectorById(Long id);

    Director updateDirectorById(Director director);
}
