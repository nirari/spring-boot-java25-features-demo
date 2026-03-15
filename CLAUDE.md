# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.3 application demonstrating **Java 25 features** using H2 in-memory database and JPA for data persistence. The app showcases two major Java 25 features:
- **Virtual Threads (Project Loom)** - Lightweight concurrency with an interactive frontend
- **Sequenced Collections (JEP 451)** - Unified API for ordered collections

## Architecture

```
src/main/java/com/example/demo/
├── DemoApplication.java              # Main Spring Boot app with CommandLineRunner for seed data
├── HelloController.java              # Basic REST endpoints: `/` and `/users`
├── SequencedCollectionsController.java  # Sequenced Collections API: `/sequenced/*`
├── ConcurrencyController.java        # Virtual Threads API: `/threads/*`
├── ConcurrencyPageController.java    # Serves Thymeleaf frontend: `/concurrency`
├── model/
│   └── Person.java                   # JPA entity (id + name fields)
├── repository/
│   └── PersonRepository.java         # JpaRepository interface for Person
└── service/
    ├── SequencedCollectionService.java  # Sequenced Collections logic
    └── ConcurrencyService.java          # Virtual Threads execution logic
```

**Key Points:**
- Uses H2 Console (`spring-boot-h2console`) accessible at `http://localhost:8080/h2-console`
- CommandLineRunner on startup creates 3 sample persons (Alice, Bob, Charlie)
- Tests are disabled in build.gradle (`enabled = false`)
- Build uses Java 25 toolchain
- Interactive Virtual Threads demo UI available at `/concurrency`

## Build & Run Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# With tests enabled (temporarily override build.gradle)
./gradlew bootRun --tests com.example.demo.*
```

## API Endpoints

### Basic CRUD

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Welcome message |
| `/users` | GET | Returns all persons as comma-separated string |

### Sequenced Collections (Java 25)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/sequenced/first` | GET | Returns first person using `getFirst()` |
| `/sequenced/last` | GET | Returns last person using `getLast()` |
| `/sequenced/first/{n}` | GET | Returns first N persons |
| `/sequenced/last/{n}` | GET | Returns last N persons |
| `/sequenced/reversed` | GET | Returns all persons in reverse order using `reversed()` |
| `/sequenced/info` | GET | Returns detailed info about the Sequenced Collections API |

### Virtual Threads (Java 25)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/concurrency` | GET | Serves interactive Thymeleaf frontend demo |
| `/threads/virtual` | POST | Execute N concurrent tasks with virtual threads |
| `/threads/platform` | POST | Execute N concurrent tasks with platform threads |
| `/threads/benchmark` | GET | Compare virtual vs platform thread performance |
| `/threads/pipeline` | POST | Run multi-stage pipeline demo with virtual threads |
| `/threads/info` | GET | Educational info about virtual threads vs platform threads |

**Request/Response bodies:** See corresponding controller files for detailed request JSON structures and response formats.

## Important Notes

- H2 database stores data in memory; data is lost on restart
- To persist data across runs, configure `spring.h2.console.enabled=true` and use a file-based datasource
- The Person entity's toString() outputs "User{" prefix (inconsistent naming)
- Tests are disabled by default in build.gradle
- **Virtual Threads demo:** Platform threads are capped at 1000 to avoid system overload; try 5000-10000+ virtual threads to see the difference
- **Sequenced Collections:** Requires Java 21+ (these methods exist on List in Java 25); the demo uses `List` which implements `SequencedCollection`
