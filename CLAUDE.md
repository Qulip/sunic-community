# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Multi-Module Spring Boot Community Service** built using **Hexagonal Architecture** with clean separation of concerns. The project implements Domain-Driven Design (DDD) principles and CQRS patterns across four distinct modules.

## Architecture

### Module Structure
```
community-service/
├── community-spec/         # 📋 Domain contracts & REST interface definitions
├── community-aggregate/    # 🏗️  Business logic & data access
├── community-rest/         # 🌐 REST API implementations  
└── community-boot/         # 🚀 Application bootstrap
```

### Dependency Flow Rules
- **Unidirectional Dependencies**: `community-boot` → `community-rest` → `community-aggregate` → `community-spec`
- Inner modules cannot depend on outer modules
- All modules can depend on the spec module
- Only the boot module creates the executable JAR

### Module Responsibilities
- **community-spec**: Pure domain entities, facade interfaces (REST contracts), DTOs, exceptions
- **community-aggregate**: Business logic (Logic classes), data access (Store classes), JPA entities (JPO classes)
- **community-rest**: REST controllers (Resource classes) that implement facade interfaces
- **community-boot**: Application main class, security config, swagger setup, global configuration

## Build & Development Commands

### Build Commands
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :community-boot:build

# Run the application (local profile)
./gradlew :community-boot:bootRun

# Run with specific profile
./gradlew :community-boot:bootRun --args="--spring.profiles.active=prod"

# Create bootable JAR (community-service.jar)
./gradlew :community-boot:bootJar

# Compile only (faster feedback)
./gradlew compileJava
```

### Test Commands
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :community-aggregate:test

# Run single test class
./gradlew :community-aggregate:test --tests="CommunityLogicTest"

# Run with coverage
./gradlew test jacocoTestReport
```

## Key Naming Conventions

### Data Transfer Objects (DTOs) - CQRS Pattern
- `*Cdo` - Command Data Objects for create operations
- `*Udo` - Update Data Objects for update operations
- `*Qdo` - Query Data Objects for search/filter operations
- `*Rdo` - Response Data Objects for output
- `*Jpo` - JPA Persistence Objects (aggregate/store layer)

### Service Classes
- `*Logic` - Business logic implementation (aggregate layer)
- `*Store` - Data access orchestration (aggregate layer)
- `*Resource` - REST API controllers implementing facade interfaces (rest layer)
- `*Facade` - Interface definitions for REST contracts (spec layer)

## Critical Architecture Patterns

### Facade Pattern Implementation
**Facades define REST Resource contracts**, not business logic contracts:
```java
// In spec module - defines REST API contract
public interface CommunityFacade {
    ResponseEntity<ApiResponse<CommunityRdo>> getCommunity(Integer id);
    ResponseEntity<ApiResponse<CommunityRdo>> registerCommunity(@Valid CommunityCdo cdo);
    // ... other REST endpoints
}

// In rest module - implements the REST contract
@RestController
public class CommunityResource implements CommunityFacade {
    private final CommunityLogic communityLogic; // Business logic injection
    
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommunityRdo>> getCommunity(@PathVariable Integer id) {
        CommunityRdo result = communityLogic.getCommunity(id); // Delegate to business logic
        return ResponseEntity.ok(ApiResponse.success("Success", result));
    }
}
```

### JPO Factory Method Pattern
**JPO classes handle their own conversion logic**:
```java
// JPO classes contain conversion methods
public class CommunityJpo {
    // Domain → JPO
    public static CommunityJpo fromDomain(Community community) { /* conversion */ }
    
    // JPO → Domain  
    public Community toDomain() { /* conversion */ }
    
    // Update existing JPO from domain
    public void update(Community community) { /* update fields */ }
}

// Store classes use JPO factory methods
public class CommunityStore {
    public Community save(Community community) {
        CommunityJpo jpo = CommunityJpo.fromDomain(community);
        CommunityJpo saved = repository.save(jpo);
        return saved.toDomain();
    }
}
```

### Data Flow Architecture
```
REST Request → Resource (implements Facade) → Logic (business logic) → Store (data access) → JPO (persistence) → Database
                    ↓                           ↓                     ↓
               HTTP concerns              Domain operations      JPO conversion
```

## Technology Stack

- **Framework**: Spring Boot 3.5.4-SNAPSHOT
- **Java**: 17 (required)
- **Build Tool**: Gradle with multi-module setup
- **Database**: MySQL with JPA/Hibernate
- **Testing**: JUnit 5 + AssertJ
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Security**: Spring Security with JWT

## Configuration

### Database Configuration
Located in `community-boot/src/main/resources/application.yml`:
- **Local profile**: Direct MySQL connection (`localhost:3306/sunic`)
- **Production profile**: Environment variables (`${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`)

### Application Profiles
```bash
# Local development (shows SQL, debug logging)
--spring.profiles.active=local

# Production (minimal logging, env vars)
--spring.profiles.active=prod
```

### API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`
- Endpoints follow `/api/v1/{domain}` pattern

## Key Business Domains

### Community Domain
- **Entities**: Community, Member, CommunityType
- **Operations**: Register, modify, delete, join/leave members
- **Key Classes**: 
  - `CommunityFacade` (REST contract)
  - `CommunityResource` (REST implementation)  
  - `CommunityLogic` (business logic)
  - `CommunityStore` (data access)
  - `CommunityJpo` (persistence)

### Post Domain  
- **Entities**: Post, Comment, PostType
- **Operations**: Create, update, delete posts and comments
- **Key Classes**:
  - `PostFacade` (REST contract)
  - `PostResource` (REST implementation)
  - `PostLogic` (business logic)
  - `PostStore` (data access)
  - `PostJpo`, `CommentJpo` (persistence)

## Development Best Practices

### Module Development Order
1. **Spec Module**: Define domain entities, DTOs, and facade interfaces (REST contracts)
2. **Aggregate Module**: Implement business logic (Logic), data access (Store), persistence (JPO)
3. **Rest Module**: Implement facade interfaces in Resource controllers
4. **Boot Module**: Wire everything together with configuration

### JPO Conversion Guidelines
- **Always use JPO factory methods**: `fromDomain()`, `toDomain()`, `update()`
- **Handle relationships properly**: Set foreign key JPO references after creation
- **Null safety**: Check for null collections and relationships in `toDomain()`

### Facade Interface Guidelines  
- **Facades define REST contracts**: Include `ResponseEntity<ApiResponse<T>>` return types
- **Include validation annotations**: Use `@Valid` for request DTOs
- **Resource classes implement facades**: Handle HTTP concerns, delegate to business logic

## Important Implementation Details

- **Pure Domain Entities**: Spec module entities have no JPA annotations
- **JPO Self-Contained**: Each JPO handles its own domain conversion logic
- **Resource Delegation**: Controllers delegate all business logic to Logic classes
- **Store Orchestration**: Store classes orchestrate data access using JPO factory methods  
- **Global Exception Handling**: Centralized in rest module using `@RestControllerAdvice`
- **CQRS Naming**: Consistent use of Cdo/Udo/Qdo/Rdo suffixes for DTOs