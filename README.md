# spring-boot-java25-sequenced-demo

Spring Boot 4.0.3 demo application showcasing **Java 25** features: Virtual Threads (Project Loom) and Sequenced Collections, with basic REST APIs and H2 in-memory database.

## Features

- **Spring Boot 4.0.3** with **Java 25** toolchain
- **H2 in-memory database** with JPA for simple data persistence
- **RESTful endpoints** for basic CRUD operations
- **Java 25 Sequenced Collections** (JEP 451) - `getFirst()`, `getLast()`, `reversed()`
- **Java 25 Virtual Threads** (Project Loom) - massive concurrency with lightweight threads
- **Interactive demo page** (Thymeleaf) to explore concurrency features in browser
- **H2 Console** for database exploration

## Project Structure

```
src/main/java/com/example/demo/
├── DemoApplication.java                         # Main Spring Boot application
├── controller/
│   ├── ConcurrencyController.java              # REST endpoints for virtual threads demos
│   ├── ConcurrencyPageController.java          # Serves Thymeleaf frontend page
│   ├── HelloController.java                    # Basic REST endpoints
│   └── SequencedCollectionsController.java     # Java 25 Sequenced Collections demo
├── model/
│   └── Person.java                             # JPA entity
├── repository/
│   └── PersonRepository.java                   # JpaRepository
└── service/
    ├── ConcurrencyService.java                 # Virtual threads logic & metrics
    └── SequencedCollectionService.java         # Sequenced Collections service
```

## Features Deep Dive

### Virtual Threads (Project Loom)

The application demonstrates **Java 25 Virtual Threads** through:

- **Concurrent Execution**: Spawn 1,000 - 10,000+ concurrent tasks with virtual threads
- **Benchmarking**: Compare virtual vs platform threads side-by-side
- **Pipeline Pattern**: Multi-stage processing pipeline demo
- **Metrics**: Elapsed time, throughput, success rate, active thread counts
- **Task Types**: I/O-bound (sleep simulation), CPU-bound (calculations)

### Sequenced Collections (JEP 451)

The `/sequenced/*` endpoints demonstrate Java 25's `getFirst()`, `getLast()`, and `reversed()` methods on Lists, unifying the API for ordered collections.


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

### Basic Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Welcome message |
| GET | `/users` | Returns all Person entities as comma-separated string |

### Virtual Threads Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/threads/virtual` | Execute N concurrent tasks with virtual threads |
| POST | `/threads/platform` | Execute N concurrent tasks with platform threads (OS threads, capped at 1000) |
| GET | `/threads/info` | Returns educational information about virtual vs platform threads |
| GET | `/threads/benchmark` | Compare performance of both thread types |
| POST | `/threads/pipeline` | Execute pipeline demo with virtual threads |

### Sequenced Collections Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/sequenced/first` | Return first person in collection |
| GET | `/sequenced/last` | Return last person |
| GET | `/sequenced/first/{n}` | Return first N persons |
| GET | `/sequenced/last/{n}` | Return last N persons |
| GET | `/sequenced/reversed` | Return all persons in reverse order |
| GET | `/sequenced/info` | Information about Sequenced Collections API |

### Web UI

| Method | Path | Description |
|--------|------|-------------|
| GET | `/concurrency` | Interactive Thymeleaf page to test all concurrency features |

## Request/Response Examples

#### Execute Virtual Threads

```bash
curl -X POST http://localhost:8080/threads/virtual \
  -H "Content-Type: application/json" \
  -d '{"count": 5000, "durationMs": 200, "type": "IO_BOUND"}'
```

Response:
```json
{
  "metrics": {
    "elapsedTimeMs": 250,
    "throughput": 20000.0,
    "activeThreads": 150,
    "taskCount": 5000,
    "successCount": 5000,
    "errorCount": 0,
    "timestamp": "2025-03-15T05:50:00.000Z"
  },
  "results": [...],
  "threadType": "VirtualThreadFactory",
  "message": "Completed 5000 tasks in 250 ms (20000.00 tasks/sec). Success: 5000, Errors: 0"
}
```

#### Run Benchmark

```bash
curl "http://localhost:8080/threads/benchmark?count=5000&durationMs=100"
```

Response:
```json
{
  "virtual": { "metrics": { ... }, "threadType": "VirtualThreadFactory", ... },
  "platform": { "metrics": { ... }, "threadType": "PlatformThreadFactory", ... },
  "comparison": "Virtual threads: 25000.00 tasks/sec, Platform threads: 425.53 tasks/sec..."
}
```

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
