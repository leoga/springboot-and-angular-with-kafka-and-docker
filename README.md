# Spaceship Management System

This is a full-stack application for managing spaceships, built with Spring Boot and Angular.

## Running the Application

### Running with Docker (Could not be tested due to licensing issues)

This method deploys the entire application stack (frontend, backend, and Kafka) using Docker.

1. Ensure Docker and Docker Compose are installed on your system.
2. Open a terminal and navigate to the project's root directory (where the `docker-compose.yml` file is located).
3. Build the Docker images:

   ```
   docker-compose build
   ```

4. Start the containers:

   ```
   docker-compose up
   ```

5. Wait for all containers to start. You should see output from the backend, frontend, and Kafka services.

6. Once everything is running, access the application:
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080

To stop the application, press Ctrl+C in the terminal where docker-compose is running, or run `docker-compose down` in another terminal.

Note: If you make changes to the application code, rebuild the Docker images using `docker-compose build` before running `docker-compose up` again.

### Manual Deployment

#### Backend Deployment

1. Ensure you have Java 21 or later and Maven installed.
2. Navigate to the project root directory.
3. Build the project:

   ```
   mvn clean install
   ```

4. Run the backend:

   ```
   mvn spring-boot:run
   ```

The backend will start on http://localhost:8080.

#### Frontend Deployment

1. Ensure Node.js and npm are installed.
2. Navigate to the `angularclient` directory.
3. Install dependencies:

   ```
   npm install --legacy-peer-deps
   npm install popper.js --save --force
   ```

4. Start the Angular development server:

   ```
   ng serve --open
   ```

The frontend will be available at http://localhost:4200.

#### Kafka Installation and Setup

1. You can follow this guide:
   
   https://learn.conduktor.io/kafka/how-to-install-apache-kafka-on-windows/

   Make sure that every time you run Kafka from WSL2 you run the following commands in order to disable IPv6:

   ```
   sudo sysctl -w net.ipv6.conf.all.disable_ipv6=1
   sudo sysctl -w net.ipv6.conf.default.disable_ipv6=1
   ```
