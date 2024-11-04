# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim as build

# Set the working directory in the container
WORKDIR /workspace/app

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file
COPY pom.xml .

# Build all the dependencies in preparation to go offline. 
# This is a separate step so the dependencies will be cached unless 
# the pom.xml file has changed.
RUN ./mvnw dependency:go-offline -B

# Copy the project source
COPY src src

# Package the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Stage 2: Final
FROM openjdk:21-jdk-slim

ARG DEPENDENCY=/workspace/app/target/dependency

# Copy project dependencies from the build stage
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set the working directory in the container
WORKDIR /app

# Install Node.js and npm
RUN apt-get update && apt-get install -y nodejs npm

# Copy Angular project
COPY angularclient /app/angularclient

# Install Angular dependencies and build the project
WORKDIR /app/angularclient
RUN npm install
RUN npm run build

# Set the working directory back to /app
WORKDIR /app

# Run the application
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.angularexercise.AngularExerciseApplication"]

# Expose the port the app runs on
EXPOSE 8080