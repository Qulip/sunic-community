# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Multi-Module Spring Boot Community Service** built using **Hexagonal Architecture** with clean separation of concerns. The project implements Domain-Driven Design (DDD) principles and CQRS patterns across five distinct modules.

## Architecture

### Module Structure
```
community-service/
├── community-spec/         # 📋 Domain contracts & models (pure domain)
├── community-aggregate/    # 🏗️  Business logic & data access
├── community-rest/         # 🌐 REST APIs & event handling
├── community-client/       # 🔌 External integration client
└── community-boot/         # 🚀 Application bootstrap
```

### Dependency Flow Rules
- Inner modules can be imported by outer modules
- Inner modules must NOT depend on outer modules
- All modules can depend on the spec module
- Client module is independent and only depends on spec

### Domain Architecture
- **community-spec**: Pure domain entities, facade interfaces, DTOs without implementation
- **community-aggregate**: Business logic (Logic/QueryLogic), data access (Store), JPA entities (Jpo)
- **community-rest**: REST controllers (Resource), request/response DTOs, exception handling
- **community-client**: External API clients using WebClient (reactive)
- **community-boot**: Application main class, security config, swagger setup

## Build & Development Commands

### Build Commands
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :community-boot:build

# Run the application
./gradlew :community-boot:bootRun

# Create bootable JAR
./gradlew :community-boot:bootJar
```

### Test Commands
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :community-aggregate:test

# Run tests with coverage
./gradlew test jacocoTestReport
```

## Key Naming Conventions

### Data Transfer Objects (DTOs)
- `*Jpo` - JPA Persistence Objects (aggregate/store layer)
- `*Sdo` - Service Data Objects for input (spec/facade layer)
- `*Rdo` - Response Data Objects for output (spec/facade layer)
- `*Qdo` - Query Data Objects for search operations
- `*Cdo` - Command Data Objects for specific actions

### Service Classes
- `*Logic` - Business logic implementation (aggregate layer)
- `*QueryLogic` - Read-only query operations (CQRS)
- `*Store` - Data access orchestration (aggregate layer)
- `*Flow` - Complex business workflows
- `*Proxy` - External service integration
- `*Resource` - REST API controllers (rest layer)
- `*Client` - External API clients (client layer)

## Technology Stack

- **Framework**: Spring Boot 3.5.4-SNAPSHOT
- **Java**: 17
- **Build Tool**: Gradle with multi-module setup
- **Database**: MySQL with JPA/Hibernate
- **Testing**: JUnit 5 + AssertJ
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Security**: Spring Security with JWT

## Development Patterns

### CQRS Implementation
- Commands (Create, Update, Delete) → `*Logic` classes
- Queries (Read, Search) → `*QueryLogic` classes
- Data persistence → `*Store` classes with JPO conversion

### Module Development Order
1. Start with **spec** module - define entities, DTOs, facade interfaces
2. Implement **aggregate** module - business logic, data access
3. Add **rest** module - controllers implementing facade interfaces  
4. Create **client** module - for external integration
5. Bootstrap with **boot** module - wire everything together

## Configuration

### Database Configuration
Located in `community-boot/src/main/resources/application.yml`:
- Local profile: H2/MySQL local database
- Production profile: Environment variables for DB connection

### API Documentation
- Swagger UI available at `/swagger-ui.html` when running locally
- API endpoints follow `/api/v1/{domain}` pattern

## Testing Strategy

- **Unit Tests**: Logic classes with mocked dependencies
- **Repository Tests**: `@DataJpaTest` for data access layer  
- **Integration Tests**: `@SpringBootTest` for REST endpoints
- **Client Tests**: WebClient integration testing

## Key Business Domains

### Community Domain
- **Entities**: Community, Member, CommunityType
- **Operations**: Register, modify, delete, join/leave members
- **Key Classes**: CommunityLogic, CommunityStore, CommunityResource

### Post Domain  
- **Entities**: Post, Comment, PostType
- **Operations**: Create, update, delete posts and comments
- **Key Classes**: PostLogic, PostStore, PostResource

## Important Implementation Details

- All domain entities in spec module are pure POJOs without JPA annotations
- JPA entities (JPOs) in aggregate module handle persistence concerns
- Controllers delegate all business logic to facade implementations
- Store classes handle conversion between domain models and JPOs
- Client module uses reactive WebClient for external API calls
- Global exception handling configured in rest module