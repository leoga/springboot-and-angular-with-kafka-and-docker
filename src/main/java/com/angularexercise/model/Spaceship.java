package com.angularexercise.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Objects;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "spaceships")
public class Spaceship implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("model")
    private String model;

    // Constructors
    public Spaceship() {}

    public Spaceship(String name, String model) {
        this.name = name;
        this.model = model;
    }

    public Spaceship(Long id, String name, String model) {
        this.id = id;
        this.name = name;
        this.model = model;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spaceship spaceship = (Spaceship) o;
        return Objects.equals(id, spaceship.id) &&
               Objects.equals(name, spaceship.name) &&
               Objects.equals(model, spaceship.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, model);
    }

    @Override
    public String toString() {
        return "Spaceship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}