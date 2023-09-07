package services.impl;

import dao.interfaces.DirectorDao;
import models.Director;
import services.DirectorService;

import java.util.List;
import java.util.Optional;

public class DirectorServiceImpl implements DirectorService {
    private final DirectorDao directorDao;

    public DirectorServiceImpl(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    @Override
    public Director addDirector(Director director) {
        return directorDao.addDirector(director);
    }

    @Override
    public Optional<Director> findById(Long id) {
        return directorDao.findById(id);
    }

    @Override
    public List<Director> findAll() {
        return directorDao.findAll();
    }

    @Override
    public boolean deleteDirectorById(Long id) {
        return directorDao.deleteDirectorById(id);
    }

    @Override
    public Director updateDirectorById(Director director) {
        return directorDao.updateDirectorById(director);
    }
}
