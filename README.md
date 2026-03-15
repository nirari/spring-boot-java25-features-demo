# testClaw

Spring Boot 4.0.3 demo application showcasing Java 25 Sequenced Collections and basic REST APIs with H2 in-memory database.

## Features

- Spring Boot 4.0.3 with Java 25
- H2 in-memory database with JPA
- RESTful endpoints for CRUD operations
- Sequenced Collections demonstration (Java 25 feature)
- H2 Console for database exploration

## Project Structure

```
src/main/java/com/example/demo/
├── DemoApplication.java              # Main Spring Boot application
├── HelloController.java              # REST controller
├── SequencedCollectionsController.java  # Java 25 Sequenced Collections demo
├── model/
│   └── Person.java                   # JPA entity
├── repository/
│   └── PersonRepository.java         # JpaRepository
└── service/
    └── SequencedCollectionService.java  # Sequenced Collections service
```

## Getting Started

### Prerequisites

- Java 25 or higher
- Gradle (wrapper included)

### Build & Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Access the application
# API: http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
```

### Running Tests

Tests are disabled by default in `build.gradle`. To enable:

```bash
./gradlew test
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Welcome message |
| GET | `/users` | Returns all Person entities as comma-separated string |

## Configuration

- **Database**: H2 in-memory (data lost on restart)
- **JPA**: Auto-configuration with Hibernate
- **Port**: 8080 (default)

### Using H2 Console

1. Start the application
2. Navigate to http://localhost:8080/h2-console
3. Use JDBC URL: `jdbc:h2:mem:testdb`
4. No credentials required

## Notes

- The Person entity's `toString()` outputs "User{" prefix (naming inconsistency)
- Application pre-loads 3 sample persons (Alice, Bob, Charlie) via CommandLineRunner
- For persistent storage across restarts, switch to a file-based datasource

## Tech Stack

- Spring Boot 4.0.3
- Java 25
- Gradle 8.x
- H2 Database
- Spring Data JPA
