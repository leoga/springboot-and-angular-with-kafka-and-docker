package com.angularexercise.controller;

import com.angularexercise.model.Spaceship;
import com.angularexercise.service.SpaceshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for managing Spaceship entities.
 * This controller provides CRUD operations for Spaceships via RESTful endpoints.
 */
@RestController
@RequestMapping("/api/spaceships")
public class SpaceshipController {
    private static final Logger logger = LoggerFactory.getLogger(SpaceshipController.class);
    private final SpaceshipService spaceshipService;

    @Autowired
    public SpaceshipController(SpaceshipService spaceshipService) {
        this.spaceshipService = spaceshipService;
    }

    /**
     * Retrieves a page of spaceships.
     *
     * @param name Optional name parameter to filter spaceships by name.
     * @param pageable Pagination information.
     * @return A Page of Spaceship objects.
     */
    @GetMapping
    @ResponseBody
    public Page<Spaceship> getAllSpaceships(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Pageable pageable) {

        Page<Spaceship> spaceships = spaceshipService.getAllSpaceships(name, sort, direction, pageable);

        logger.debug("Spaceships on this page: {}", spaceships.getContent());
        logger.debug("Is first page: {}, Is last page: {}", spaceships.isFirst(), spaceships.isLast());
        logger.debug("Pageable: offset={}, pageNumber={}, pageSize={}", pageable.getOffset(), pageable.getPageNumber(), pageable.getPageSize());
        logger.debug("Page info: number={}, size={}, totalElements={}, totalPages={}", 
                spaceships.getNumber(), spaceships.getSize(), spaceships.getTotalElements(), spaceships.getTotalPages());
        
        return spaceships;
    }

    

    /**
     * Retrieves a specific spaceship by its ID.
     *
     * @param id The ID of the spaceship to retrieve.
     * @return ResponseEntity containing the Spaceship if found, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Spaceship> getSpaceshipById(@PathVariable Long id) {
        Spaceship spaceship = spaceshipService.getSpaceshipById(id);
        return ResponseEntity.ok(spaceship);
    }

    /**
     * Creates a new spaceship.
     *
     * @param spaceship The Spaceship object to be created.
     * @return ResponseEntity containing the created Spaceship.
     */
    @PostMapping("/create")
    public ResponseEntity<Spaceship> createSpaceship(@RequestBody Spaceship spaceship) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spaceshipService.createSpaceship(spaceship));
    }

    /**
     * Updates an existing spaceship.
     *
     * @param id The ID of the spaceship to update.
     * @param spaceship The updated Spaceship object.
     * @return ResponseEntity containing the updated Spaceship if found, or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Spaceship> updateSpaceship(@PathVariable Long id, @RequestBody Spaceship spaceship) {
        Spaceship updatedSpaceship = spaceshipService.updateSpaceship(id, spaceship);
        return ResponseEntity.ok(updatedSpaceship);
    }

    /**
     * Deletes a spaceship by its ID.
     *
     * @param id The ID of the spaceship to delete.
     * @return ResponseEntity with no content if successful, or 404 if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpaceship(@PathVariable Long id) {
        spaceshipService.deleteSpaceship(id);
        return ResponseEntity.noContent().build();
    }
}