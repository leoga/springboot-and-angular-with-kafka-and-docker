package com.angularexercise.service;

import com.angularexercise.model.Spaceship;
import com.angularexercise.repository.SpaceshipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import com.angularexercise.exception.ResourceNotFoundException;

@Service
public class SpaceshipService {

    private static final Logger logger = LoggerFactory.getLogger(SpaceshipService.class);
    private final SpaceshipRepository spaceshipRepository;
    private final KafkaTemplate<String, Spaceship> kafkaTemplate;
    private static final String TOPIC = "spaceship-topic";

    @Autowired
    public SpaceshipService(SpaceshipRepository spaceshipRepository, KafkaTemplate<String, Spaceship> kafkaTemplate) {
        this.spaceshipRepository = spaceshipRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Cacheable(value = "spaceships", key = "#name + '-' + #sort + '-' + #direction + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Spaceship> getAllSpaceships(String name, String sort, String direction, Pageable pageable) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Sort sorting = Sort.by(sortDirection, sort);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);
        
        Page<Spaceship> result;
        if (name != null && !name.isEmpty()) {
            result = spaceshipRepository.findByNameContainingIgnoreCase(name, pageableWithSort);
        } else {
            result = spaceshipRepository.findAll(pageableWithSort);
        }
        logger.info("Fetched {} spaceships for page {} with size {}, name filter: {}, and sort: {}",
            result.getNumberOfElements(), pageable.getPageNumber(), pageable.getPageSize(), name, pageable.getSort());
        return result;
    }

    @Cacheable(value = "spaceship", key = "#id")
    public Spaceship getSpaceshipById(Long id) {
        return spaceshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spaceship not found with id: " + id));
    }

    @CacheEvict(value = "spaceships", allEntries = true)
    public Spaceship createSpaceship(Spaceship spaceship) {
        Spaceship savedSpaceship = spaceshipRepository.save(spaceship);
        kafkaTemplate.send(TOPIC, savedSpaceship);
        logger.info("Sent spaceship creation message to Kafka for spaceship: {}", savedSpaceship.getId());
        return savedSpaceship;
    }

    @CacheEvict(value = {"spaceship", "spaceships"}, allEntries = true)
    public Spaceship updateSpaceship(Long id, Spaceship spaceshipDetails) {
        Spaceship existingSpaceship = spaceshipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Spaceship not found with id: " + id));
        existingSpaceship.setName(spaceshipDetails.getName());
        existingSpaceship.setModel(spaceshipDetails.getModel());
        Spaceship updatedSpaceship = spaceshipRepository.save(existingSpaceship);
        kafkaTemplate.send(TOPIC, updatedSpaceship);
        logger.info("Sent spaceship update message to Kafka for spaceship: {}", updatedSpaceship.getId());
        return updatedSpaceship;
    }

    @CacheEvict(value = {"spaceship", "spaceships"}, allEntries = true)
    public void deleteSpaceship(Long id) {
        Spaceship spaceship = spaceshipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Spaceship not found with id: " + id));
        spaceshipRepository.deleteById(id);
        kafkaTemplate.send(TOPIC, spaceship);
        logger.info("Sent spaceship deletion message to Kafka for spaceship: {}", spaceship.getId());
    }
}