package services;


import dao.interfaces.DirectorDao;
import models.Director;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import services.impl.DirectorServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectorServiceTest {
    @Mock
    DirectorDao directorDao;
    DirectorService directorService;

    @BeforeEach
    public void setUp() {
        directorService = new DirectorServiceImpl(directorDao);
    }

    @Test
    void testFindOptionalDirectorById() {
        Director director = new Director();
        director.setDirectorId(1L);
        director.setName("Max");
        director.setAge(25);

        when(directorDao.findById(1L)).thenReturn(Optional.of(director));

        assertEquals(Optional.of(director), directorService.findById(1L));
    }

    @Test
    void testFindAllDirectors() {
        List<Director> directors = new ArrayList<>();
        Director director1 = new Director();
        director1.setDirectorId(1L);
        director1.setName("Lena");
        director1.setAge(24);

        Director director2 = new Director();
        director2.setDirectorId(2L);
        director2.setName("Max");
        director2.setAge(25);
        directors.add(director1);
        directors.add(director2);

        when(directorDao.findAll()).thenReturn(directors);

        assertEquals(directors, directorService.findAll());
    }

    @Test
    void testAddDirector() {
        Director director = new Director();
        director.setDirectorId(2L);
        director.setName("Max");
        director.setAge(25);

        when(directorDao.addDirector(director)).thenReturn(director);

        assertEquals(director, directorService.addDirector(director));
    }

    @Test
    void testUpdateDirector() {
        Director director = new Director();
        director.setDirectorId(2L);
        director.setName("Max");
        director.setAge(25);

        when(directorDao.updateDirectorById(director)).thenReturn(director);

        assertEquals(director, directorService.updateDirectorById(director));
    }

    @Test
    void testDeleteDirector() {
        when(directorDao.deleteDirectorById(1L)).thenReturn(true);

        assertTrue(directorService.deleteDirectorById(1L));
    }
}
