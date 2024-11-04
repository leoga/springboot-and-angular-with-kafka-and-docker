package com.angularexercise.controller;

import com.angularexercise.model.Spaceship;
import com.angularexercise.repository.SpaceshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/test-data.sql")
public class SpaceshipControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpaceshipRepository spaceshipRepository;

    private String getRootUrl() {
        return "http://localhost:" + port + "/api/spaceships";
    }

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        spaceshipRepository.deleteAll();
    }

    @Test
    public void testGetAllSpaceships() {
        ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("content");
    }

    @Test
    public void testGetSpaceshipById() {
        Spaceship spaceship = new Spaceship();
        spaceship.setName("Test Spaceship");
        spaceship.setModel("Test Model");
        spaceship = spaceshipRepository.save(spaceship);

        ResponseEntity<Spaceship> response = restTemplate.getForEntity(getRootUrl() + "/" + spaceship.getId(), Spaceship.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Test Spaceship");
    }

    @Test
    public void testCreateSpaceship() {
        Spaceship spaceship = new Spaceship();
        spaceship.setName("New Spaceship");
        spaceship.setModel("New Model");

        ResponseEntity<Spaceship> response = restTemplate.postForEntity(getRootUrl() + "/create", spaceship, Spaceship.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("New Spaceship");
    }

    @Test
    public void testUpdateSpaceship() {
        Spaceship spaceship = new Spaceship();
        spaceship.setName("Old Spaceship");
        spaceship.setModel("Old Model");
        spaceship = spaceshipRepository.save(spaceship);

        spaceship.setName("Updated Spaceship");
        restTemplate.put(getRootUrl() + "/" + spaceship.getId(), spaceship);

        Spaceship updatedSpaceship = spaceshipRepository.findById(spaceship.getId()).orElse(null);
        assertThat(updatedSpaceship.getName()).isEqualTo("Updated Spaceship");
    }

    @Test
    public void testDeleteSpaceship() {
        Spaceship spaceship = new Spaceship();
        spaceship.setName("To Be Deleted");
        spaceship.setModel("Delete Model");
        spaceship = spaceshipRepository.save(spaceship);

        restTemplate.delete(getRootUrl() + "/" + spaceship.getId());

        Spaceship deletedSpaceship = spaceshipRepository.findById(spaceship.getId()).orElse(null);
        assertThat(deletedSpaceship).isNull();
    }

    @Test
    public void testGetSpaceshipByIdNotFound() {
        ResponseEntity<Spaceship> response = restTemplate.getForEntity(getRootUrl() + "/999", Spaceship.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testUpdateSpaceshipNotFound() {
        Spaceship spaceship = new Spaceship();
        spaceship.setName("Non-existent Spaceship");
        spaceship.setModel("Non-existent Model");

        ResponseEntity<Spaceship> response = restTemplate.exchange(getRootUrl() + "/999", HttpMethod.PUT, new HttpEntity<>(spaceship), Spaceship.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteSpaceshipNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(getRootUrl() + "/999", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetAllSpaceshipsPagination() {
        Spaceship spaceship1 = new Spaceship();
        spaceship1.setName("Spaceship 1");
        spaceship1.setModel("Model 1");
        spaceshipRepository.save(spaceship1);

        Spaceship spaceship2 = new Spaceship();
        spaceship2.setName("Spaceship 2");
        spaceship2.setModel("Model 2");
        spaceshipRepository.save(spaceship2);

        ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + "?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalElements\" : 2");
        assertThat(response.getBody()).contains("\"totalPages\" : 2");
        assertThat(response.getBody()).contains("\"size\" : 1");
        assertThat(response.getBody()).contains("\"number\" : 0");
    }

    @Test
    public void testGetAllSpaceshipsSorting() {
        Spaceship spaceship1 = new Spaceship();
        spaceship1.setName("B Spaceship");
        spaceship1.setModel("Model B");
        spaceshipRepository.save(spaceship1);

        Spaceship spaceship2 = new Spaceship();
        spaceship2.setName("A Spaceship");
        spaceship2.setModel("Model A");
        spaceshipRepository.save(spaceship2);

        ResponseEntity<String> response = restTemplate.getForEntity(getRootUrl() + "?sort=name&direction=asc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("A Spaceship").contains("B Spaceship");
        assertThat(response.getBody().indexOf("A Spaceship")).isLessThan(response.getBody().indexOf("B Spaceship"));
    }

}