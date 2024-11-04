-- Create initial schema for Spaceship table
CREATE TABLE spaceships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    model VARCHAR(255)
);