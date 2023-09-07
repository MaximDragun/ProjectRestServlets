package services;


import models.Actor;

import java.util.List;
import java.util.Optional;

public interface ActorService {
    Actor addActor(Actor actor);

    Optional<Actor> findById(Long id);

    List<Actor> findAll();

    boolean deleteActorById(Long id);

    Actor updateActorById(Actor actor);
}
