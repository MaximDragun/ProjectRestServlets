package services;


import dao.interfaces.ActorDao;
import models.Actor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.impl.ActorServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {
    @Mock
    ActorDao actorDao;
    ActorService actorService;

    @BeforeEach
    public void setUp() {
        actorService = new ActorServiceImpl(actorDao);
    }


    @Test
    void testFindOptionalActorById() {
        Actor actor = new Actor();
        actor.setActorId(1L);
        actor.setName("Max");
        actor.setAge(25);

        when(actorDao.findById(1L)).thenReturn(Optional.of(actor));

        assertEquals(Optional.of(actor), actorService.findById(1L));
    }

    @Test
    void testFindAllActors() {
        List<Actor> actorList = new ArrayList<>();
        Actor actor1 = new Actor();
        actor1.setActorId(1L);
        actor1.setName("Max");
        actor1.setAge(25);

        Actor actor2 = new Actor();
        actor2.setActorId(2L);
        actor2.setName("Egor");
        actor2.setAge(26);
        actorList.add(actor1);
        actorList.add(actor2);

        when(actorDao.findAll()).thenReturn(actorList);

        assertEquals(actorList, actorService.findAll());
    }

    @Test
    void testAddActor() {
        Actor actor = new Actor();
        actor.setActorId(1L);
        actor.setName("Max");
        actor.setAge(25);

        when(actorDao.addActor(actor)).thenReturn(actor);

        assertEquals(actor, actorService.addActor(actor));
    }

    @Test
    void testUpdateActor() {
        Actor actor = new Actor();
        actor.setActorId(1L);
        actor.setName("Max");
        actor.setAge(25);

        when(actorDao.updateActorById(actor)).thenReturn(actor);

        assertEquals(actor, actorService.updateActorById(actor));
    }

    @Test
    void testDeleteActor() {
        when(actorDao.deleteActorById(1L)).thenReturn(true);

        assertTrue(actorService.deleteActorById(1L));
    }
}
