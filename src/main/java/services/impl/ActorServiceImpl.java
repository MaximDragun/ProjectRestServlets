package services.impl;

import dao.interfaces.ActorDao;
import models.Actor;
import services.ActorService;

import java.util.List;
import java.util.Optional;

public class ActorServiceImpl implements ActorService {
    private final ActorDao actorDao;

    public ActorServiceImpl(ActorDao actorDao) {
        this.actorDao = actorDao;
    }

    @Override
    public Actor addActor(Actor actor) {
        return actorDao.addActor(actor);
    }

    @Override
    public Optional<Actor> findById(Long id) {
        return actorDao.findById(id);
    }

    @Override
    public List<Actor> findAll() {
        return actorDao.findAll();
    }

    @Override
    public boolean deleteActorById(Long id) {
        return actorDao.deleteActorById(id);
    }

    @Override
    public Actor updateActorById(Actor actor) {
        return actorDao.updateActorById(actor);
    }
}
