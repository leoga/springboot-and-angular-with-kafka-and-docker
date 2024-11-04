package com.angularexercise.repository;

import com.angularexercise.model.Spaceship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceshipRepository extends JpaRepository<Spaceship, Long> {
    Page<Spaceship> findByNameContainingIgnoreCase(String name, Pageable pageable);
    // This interface is empty as it inherits CRUD methods from JpaRepository
}