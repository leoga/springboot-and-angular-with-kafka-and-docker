package com.angularexercise.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpaceshipTest {

    @Test
    void testSpaceshipConstructorAndGetters() {
        Long id = 1L;
        String name = "Enterprise";
        String model = "NCC-1701";

        Spaceship spaceship = new Spaceship(id, name, model);

        assertEquals(id, spaceship.getId());
        assertEquals(name, spaceship.getName());
        assertEquals(model, spaceship.getModel());
    }

    @Test
    void testSpaceshipSetters() {
        Spaceship spaceship = new Spaceship();

        Long id = 2L;
        String name = "Voyager";
        String model = "NCC-74656";

        spaceship.setId(id);
        spaceship.setName(name);
        spaceship.setModel(model);

        assertEquals(id, spaceship.getId());
        assertEquals(name, spaceship.getName());
        assertEquals(model, spaceship.getModel());
    }

    @Test
    void testSpaceshipEquality() {
        Spaceship spaceship1 = new Spaceship(1L, "Enterprise", "NCC-1701");
        Spaceship spaceship2 = new Spaceship(1L, "Enterprise", "NCC-1701");
        Spaceship spaceship3 = new Spaceship(2L, "Voyager", "NCC-74656");

        assertEquals(spaceship1, spaceship2);
        assertNotEquals(spaceship1, spaceship3);
    }

    @Test
    void testSpaceshipHashCode() {
        Spaceship spaceship1 = new Spaceship(1L, "Enterprise", "NCC-1701");
        Spaceship spaceship2 = new Spaceship(1L, "Enterprise", "NCC-1701");

        assertEquals(spaceship1.hashCode(), spaceship2.hashCode());
    }

    @Test
    void testSpaceshipToString() {
        Spaceship spaceship = new Spaceship(1L, "Enterprise", "NCC-1701");
        String expected = "Spaceship{id=1, name='Enterprise', model='NCC-1701'}";

        assertEquals(expected, spaceship.toString());
    }
}