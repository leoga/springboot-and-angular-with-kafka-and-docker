package com.angularexercise.controller;

import com.angularexercise.model.Spaceship;
import com.angularexercise.service.SpaceshipService;
import com.angularexercise.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpaceshipControllerTest {

    @Mock
    private SpaceshipService spaceshipService;

    @InjectMocks
    private SpaceshipController spaceshipController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSpaceships() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Spaceship> spaceships = Arrays.asList(
            new Spaceship(1L, "Enterprise", "NCC-1701"),
            new Spaceship(2L, "Voyager", "NCC-74656")
        );
        Page<Spaceship> page = new PageImpl<>(spaceships, pageable, spaceships.size());

        when(spaceshipService.getAllSpaceships(null, "id", "asc", pageable)).thenReturn(page);

        Page<Spaceship> result = spaceshipController.getAllSpaceships(null, "id", "asc", pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(spaceshipService, times(1)).getAllSpaceships(null, "id", "asc", pageable);
    }

    @Test
    void testGetAllSpaceshipsWithSorting() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
        List<Spaceship> spaceships = Arrays.asList(
            new Spaceship(2L, "Voyager", "NCC-74656"),
            new Spaceship(1L, "Enterprise", "NCC-1701")
        );
        Page<Spaceship> page = new PageImpl<>(spaceships, pageable, spaceships.size());

        when(spaceshipService.getAllSpaceships(null, "name", "desc", pageable)).thenReturn(page);

        Page<Spaceship> result = spaceshipController.getAllSpaceships(null, "name", "desc", pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Voyager", result.getContent().get(0).getName());
        assertEquals("Enterprise", result.getContent().get(1).getName());
        verify(spaceshipService, times(1)).getAllSpaceships(null, "name", "desc", pageable);
    }

    @Test
    void testGetSpaceshipById() {
        Long id = 1L;
        Spaceship spaceship = new Spaceship(id, "Enterprise", "NCC-1701");

        when(spaceshipService.getSpaceshipById(id)).thenReturn(spaceship);

        ResponseEntity<Spaceship> response = spaceshipController.getSpaceshipById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Enterprise", response.getBody().getName());
        assertEquals("NCC-1701", response.getBody().getModel());
    }

    @Test
    void testGetSpaceshipByIdNotFound() {
        Long id = 1L;

        when(spaceshipService.getSpaceshipById(id)).thenThrow(new ResourceNotFoundException("Spaceship not found"));

        assertThrows(ResourceNotFoundException.class, () -> spaceshipController.getSpaceshipById(id));
    }

    @Test
    void testCreateSpaceship() {
        Spaceship spaceship = new Spaceship(null, "Enterprise", "NCC-1701");
        Spaceship savedSpaceship = new Spaceship(1L, "Enterprise", "NCC-1701");

        when(spaceshipService.createSpaceship(spaceship)).thenReturn(savedSpaceship);

        ResponseEntity<Spaceship> response = spaceshipController.createSpaceship(spaceship);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Enterprise", response.getBody().getName());
        assertEquals("NCC-1701", response.getBody().getModel());
    }

    @Test
    void testUpdateSpaceship() {
        Long id = 1L;
        Spaceship spaceship = new Spaceship(id, "Enterprise-A", "NCC-1701-A");
        Spaceship updatedSpaceship = new Spaceship(id, "Enterprise-A", "NCC-1701-A");

        when(spaceshipService.updateSpaceship(id, spaceship)).thenReturn(updatedSpaceship);

        ResponseEntity<Spaceship> response = spaceshipController.updateSpaceship(id, spaceship);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Enterprise-A", response.getBody().getName());
        assertEquals("NCC-1701-A", response.getBody().getModel());
    }

    @Test
    void testUpdateSpaceshipNotFound() {
        Long id = 1L;
        Spaceship spaceship = new Spaceship(id, "Enterprise-A", "NCC-1701-A");

        when(spaceshipService.updateSpaceship(id, spaceship)).thenThrow(new ResourceNotFoundException("Spaceship not found"));

        assertThrows(ResourceNotFoundException.class, () -> spaceshipController.updateSpaceship(id, spaceship));
    }

    @Test
    void testDeleteSpaceship() {
        Long id = 1L;

        doNothing().when(spaceshipService).deleteSpaceship(id);

        ResponseEntity<Void> response = spaceshipController.deleteSpaceship(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(spaceshipService, times(1)).deleteSpaceship(id);
    }

    @Test
    void testDeleteSpaceshipNotFound() {
        Long id = 1L;

        doThrow(new ResourceNotFoundException("Spaceship not found")).when(spaceshipService).deleteSpaceship(id);

        assertThrows(ResourceNotFoundException.class, () -> spaceshipController.deleteSpaceship(id));
    }
}