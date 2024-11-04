# Spaceship API Documentation

This document provides information about the Spaceship API endpoints, their purposes, request parameters, and response formats.

## Base URL

All API requests should be made to: `/api/spaceships`

## Endpoints

### 1. Get All Spaceships

Retrieves a paginated list of spaceships, with optional filtering by name.

- **URL:** `/`
- **Method:** GET
- **Query Parameters:**
  - `name` (optional): Filter spaceships by name (case-insensitive, partial match)
  - `page` (optional): Page number for pagination (default: 0)
  - `size` (optional): Number of items per page (default: 20)
  - `sort` (optional): Sort field and direction (e.g., `sort=name,asc`)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Enterprise",
      "model": "NCC-1701"
    },
    // ... more spaceships
  ],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```

### 2. Get Spaceship by ID

Retrieves a specific spaceship by its ID.

- **URL:** `/{id}`
- **Method:** GET
- **Path Parameters:**
  - `id`: The ID of the spaceship to retrieve

**Response:**
```json
{
  "id": 1,
  "name": "Enterprise",
  "model": "NCC-1701"
}
```

### 3. Create Spaceship

Creates a new spaceship.

- **URL:** `/create`
- **Method:** POST
- **Request Body:**
```json
{
  "name": "Millennium Falcon",
  "model": "YT-1300"
}
```

**Response:**
```json
{
  "id": 2,
  "name": "Millennium Falcon",
  "model": "YT-1300"
}
```

### 4. Update Spaceship

Updates an existing spaceship.

- **URL:** `/{id}`
- **Method:** PUT
- **Path Parameters:**
  - `id`: The ID of the spaceship to update
- **Request Body:**
```json
{
  "name": "Updated Enterprise",
  "model": "NCC-1701-A"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Updated Enterprise",
  "model": "NCC-1701-A"
}
```

### 5. Delete Spaceship

Deletes a spaceship by its ID.

- **URL:** `/{id}`
- **Method:** DELETE
- **Path Parameters:**
  - `id`: The ID of the spaceship to delete

**Response:** 204 No Content

## Error Handling

In case of errors, the API will return appropriate HTTP status codes along with error messages in the response body.

- 400 Bad Request: Invalid input or request parameters
- 404 Not Found: Requested resource not found
- 500 Internal Server Error: Unexpected server error

Error Response Format:
```json
{
  "timestamp": "2023-05-21T12:34:56.789Z",
  "status": 404,
  "error": "Not Found",
  "message": "Spaceship with id 999 not found",
  "path": "/api/spaceships/999"
}
```

This API documentation provides a comprehensive overview of the available endpoints and their usage for the Spaceship API.