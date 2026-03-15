# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.3 application using H2 in-memory database and JPA for data persistence. The app demonstrates basic CRUD operations via REST endpoints with a Person entity.

## Architecture

```
src/main/java/com/example/demo/
├── DemoApplication.java        # Main Spring Boot app with CommandLineRunner for seed data
├── HelloController.java         # REST controller with / and /users endpoints
├── model/
│   └── Person.java              # JPA entity (id + name fields)
└── repository/
    └── PersonRepository.java    # JpaRepository interface for Person
```

**Key Points:**
- Uses H2 Console (`spring-boot-h2console`) accessible at `http://localhost:8080/h2-console`
- CommandLineRunner on startup creates 3 sample persons (Alice, Bob, Charlie)
- Tests are disabled in build.gradle (`enabled = false`)
- Build uses Java 25 toolchain

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

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Welcome message |
| `/users` | GET | Returns all persons as comma-separated string |

## Important Notes

- H2 database stores data in memory; data is lost on restart
- To persist data across runs, configure `spring.h2.console.enabled=true` and use a file-based datasource
- The Person entity's toString() outputs "User{" prefix (inconsistent naming)
- Tests are disabled by default in build.gradle
