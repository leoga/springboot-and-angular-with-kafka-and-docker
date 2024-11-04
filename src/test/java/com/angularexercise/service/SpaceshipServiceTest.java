package com.angularexercise.service;

import com.angularexercise.model.Spaceship;
import com.angularexercise.repository.SpaceshipRepository;
import com.angularexercise.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaceshipServiceTest {

    @Mock
    private SpaceshipRepository spaceshipRepository;

    @Mock
    private KafkaTemplate<String, Spaceship> kafkaTemplate;

    @InjectMocks
    private SpaceshipService spaceshipService;

    

    @Test
    void testGetAllSpaceships() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        List<Spaceship> spaceships = Arrays.asList(
            new Spaceship(1L, "Enterprise", "NCC-1701"),
            new Spaceship(2L, "Voyager", "NCC-74656")
        );
        Page<Spaceship> page = new PageImpl<>(spaceships, pageable, spaceships.size());

        when(spaceshipRepository.findAll(pageable)).thenReturn(page);

        Page<Spaceship> result = spaceshipService.getAllSpaceships(null, "id", "asc", PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(spaceshipRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllSpaceshipsWithSorting() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
        List<Spaceship> spaceships = Arrays.asList(
            new Spaceship(2L, "Voyager", "NCC-74656"),
            new Spaceship(1L, "Enterprise", "NCC-1701")
        );
        Page<Spaceship> page = new PageImpl<>(spaceships, pageable, spaceships.size());

        when(spaceshipRepository.findAll(pageable)).thenReturn(page);

        Page<Spaceship> result = spaceshipService.getAllSpaceships(null, "name", "desc", PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Voyager", result.getContent().get(0).getName());
        assertEquals("Enterprise", result.getContent().get(1).getName());
        verify(spaceshipRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllSpaceshipsWithNameFilter() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        List<Spaceship> spaceships = Arrays.asList(
            new Spaceship(1L, "Enterprise", "NCC-1701")
        );
        Page<Spaceship> page = new PageImpl<>(spaceships, pageable, spaceships.size());

        when(spaceshipRepository.findByNameContainingIgnoreCase(eq("Enterprise"), any(Pageable.class))).thenReturn(page);

        Page<Spaceship> result = spaceshipService.getAllSpaceships("Enterprise", "id", "asc", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Enterprise", result.getContent().get(0).getName());
        verify(spaceshipRepository, times(1)).findByNameContainingIgnoreCase(eq("Enterprise"), any(Pageable.class));
    }

    @Test
    void testGetSpaceshipById() {
        Long id = 1L;
        Spaceship spaceship = new Spaceship(id, "Enterprise", "NCC-1701");

        when(spaceshipRepository.findById(id)).thenReturn(Optional.of(spaceship));

        Spaceship result = spaceshipService.getSpaceshipById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Enterprise", result.getName());
        assertEquals("NCC-1701", result.getModel());
    }

    @Test
    void testGetSpaceshipByIdNotFound() {
        Long id = 1L;

        when(spaceshipRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> spaceshipService.getSpaceshipById(id));
    }

    @Test
    void testCreateSpaceship() {
        Spaceship spaceship = new Spaceship(null, "Enterprise", "NCC-1701");
        Spaceship savedSpaceship = new Spaceship(1L, "Enterprise", "NCC-1701");

        when(spaceshipRepository.save(spaceship)).thenReturn(savedSpaceship);

        Spaceship result = spaceshipService.createSpaceship(spaceship);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Enterprise", result.getName());
        assertEquals("NCC-1701", result.getModel());
        verify(kafkaTemplate, times(1)).send(eq("spaceship-topic"), eq(savedSpaceship));
    }

    @Test
    void testUpdateSpaceship() {
        Long id = 1L;
        Spaceship existingSpaceship = new Spaceship(id, "Enterprise", "NCC-1701");
        Spaceship updatedSpaceship = new Spaceship(id, "Enterprise-A", "NCC-1701-A");

        when(spaceshipRepository.findById(id)).thenReturn(Optional.of(existingSpaceship));
        when(spaceshipRepository.save(any(Spaceship.class))).thenReturn(updatedSpaceship);

        Spaceship result = spaceshipService.updateSpaceship(id, updatedSpaceship);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Enterprise-A", result.getName());
        assertEquals("NCC-1701-A", result.getModel());
        verify(kafkaTemplate, times(1)).send(eq("spaceship-topic"), eq(updatedSpaceship));
    }

    @Test
    void testUpdateSpaceshipNotFound() {
        Long id = 1L;
        Spaceship updatedSpaceship = new Spaceship(id, "Enterprise-A", "NCC-1701-A");

        when(spaceshipRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> spaceshipService.updateSpaceship(id, updatedSpaceship));
    }

    @Test
    void testDeleteSpaceship() {
        Long id = 1L;
        Spaceship spaceship = new Spaceship(id, "Enterprise", "NCC-1701");

        when(spaceshipRepository.findById(id)).thenReturn(Optional.of(spaceship));

        spaceshipService.deleteSpaceship(id);

        verify(spaceshipRepository, times(1)).deleteById(id);
        verify(kafkaTemplate, times(1)).send(eq("spaceship-topic"), eq(spaceship));
    }

    @Test
    void testDeleteSpaceshipNotFound() {
        Long id = 1L;

        when(spaceshipRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> spaceshipService.deleteSpaceship(id));
    }
}