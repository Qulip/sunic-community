# Modularization Strategy

This document outlines the modularization strategy and package structure used in the Content Service project. This architecture can be applied to other microservices following similar domain-driven design principles.

## Overview

The project follows a **4-layer modular architecture** pattern that separates concerns across distinct modules with clear dependencies and responsibilities. This approach promotes maintainability, testability, and scalability while enforcing architectural boundaries.

## Module Architecture

### Dependency Flow
```
content-boot → content-rest → content-aggregate → content-spec
```

Each module can only depend on modules to its right, ensuring unidirectional dependencies and preventing circular references.

## Module Breakdown

### 1. content-spec (Domain Contracts)
**Purpose**: Defines domain contracts, interfaces, and specifications that other modules implement.

**Package Structure**:
```
com.sunic.content.spec/
├── common/              # Shared response wrappers
│   └── ApiResponse.java
├── {domain}/            # Per domain aggregate
│   ├── entity/          # Pure domain entities (no JPA)
│   ├── exception/       # Domain-specific exceptions
│   └── facade/          # Interface contracts
│       └── sdo/         # Service Data Objects (DTOs)
```

**Key Patterns**:
- **Pure Domain Entities**: No framework dependencies (JPA, Spring)
- **CQRS Naming**: Cdo (Create), Udo (Update), Qdo (Query), Rdo (Response)
- **Facade Pattern**: Interface definitions for external contracts
- **Domain Exceptions**: Business rule violations

**Dependencies**: 
- Jakarta Validation API
- Jackson annotations
- SpringDoc OpenAPI (for documentation)

### 2. content-aggregate (Business Logic & Data Access)
**Purpose**: Implements business logic and data persistence operations.

**Package Structure**:
```
com.sunic.content.aggregate/
├── {domain}/
│   ├── logic/           # Business logic implementation
│   └── store/           # Data access facade
│       ├── jpo/         # JPA Persistence Objects
│       └── repository/  # Spring Data repositories
```

**Key Patterns**:
- **Logic Classes**: Implement business rules and orchestrate operations
- **Store Classes**: Data access facade hiding persistence complexity
- **JPO (JPA Persistence Objects)**: Database-specific entities
- **Repository Pattern**: Spring Data JPA repositories

**Dependencies**:
- content-spec module
- Spring Boot Data JPA
- Spring Context & Transactions
- MySQL Connector

### 3. content-rest (Web Layer)
**Purpose**: Exposes REST APIs and handles HTTP concerns.

**Package Structure**:
```
com.sunic.content.rest/
├── config/              # Web-specific configurations
└── rest/                # REST controllers
    └── {domain}/        # Domain-specific controllers
```

**Key Patterns**:
- **Resource Controllers**: Implement facade interfaces from spec module
- **Global Exception Handler**: Centralized error handling
- **Validation**: Bean validation integration

**Dependencies**:
- content-aggregate module
- content-spec module
- Spring Boot Web & Validation
- SpringDoc OpenAPI

### 4. content-boot (Application Bootstrap)
**Purpose**: Application entry point and global configuration.

**Package Structure**:
```
com.sunic.content/
├── ContentServiceApplication.java  # Main application class
└── config/                         # Global configurations
    ├── SecurityConfig.java
    ├── SwaggerConfig.java
    └── WebConfig.java
```

**Key Patterns**:
- **Single Application Class**: Minimal main class with component scanning
- **Global Configuration**: Cross-cutting concerns (security, documentation)
- **Executable JAR**: Single deployable artifact

**Dependencies**:
- content-rest module
- Spring Boot Starter
- Spring Security
- Actuator
- JWT libraries

## Domain Package Structure

Each domain follows a consistent package structure across modules:

### Domain Entity Lifecycle
1. **Spec Module**: Pure domain entity with business methods
2. **Aggregate Module**: JPO for persistence + Logic for business operations + Store for data access
3. **Rest Module**: Resource controller implementing facade interface
4. **Boot Module**: Global configuration affecting the domain

### Example: Category Domain
```
content-spec/com.sunic.content.spec.category/
├── entity/Category.java              # Pure domain model
├── exception/CategoryNotFoundException.java
├── facade/CategoryFacade.java        # Interface contract
└── facade/sdo/
    ├── CategoryCdo.java              # Create command
    ├── CategoryUdo.java              # Update command
    └── CategoryRdo.java              # Response data

content-aggregate/com.sunic.content.aggregate.category/
├── logic/CategoryLogic.java          # Business logic
└── store/
    ├── CategoryStore.java            # Data access facade
    ├── jpo/CategoryJpo.java          # JPA entity
    └── repository/CategoryRepository.java

content-rest/com.sunic.content.rest.rest.category/
└── CategoryResource.java            # REST controller

content-boot/com.sunic.content.config/
└── [Global configurations affecting categories]
```

## Key Design Principles

### 1. Separation of Concerns
- **Spec**: What (contracts and models)
- **Aggregate**: How (business logic and data)
- **Rest**: Interface (HTTP/REST concerns)
- **Boot**: Where (deployment and global config)

### 2. CQRS Pattern
- **Commands**: Cdo (Create), Udo (Update)
- **Queries**: Qdo (Query/Search parameters)
- **Responses**: Rdo (Read/Response data)

### 3. Clean Architecture
- **Domain models** are pure (no framework dependencies)
- **Business logic** is isolated from infrastructure concerns
- **Web layer** translates between HTTP and domain
- **Configuration** is externalized and environment-specific

### 4. Dependency Management
- Each module has minimal, focused dependencies
- Framework dependencies are isolated to appropriate layers
- Domain logic remains testable without Spring context

## Benefits

1. **Maintainability**: Clear separation makes changes predictable
2. **Testability**: Each layer can be tested in isolation
3. **Scalability**: Modules can evolve independently
4. **Reusability**: Spec module contracts can be shared across services
5. **Team Productivity**: Different teams can work on different modules
6. **Deployment Flexibility**: Single executable JAR with clear internal structure

## Application to Other Projects

When applying this pattern to new microservices:

1. **Start with Domain Modeling**: Define entities and operations in spec module
2. **Implement Business Logic**: Create logic and store classes in aggregate module
3. **Add REST Interface**: Implement facade interfaces in rest module
4. **Configure Bootstrap**: Set up global concerns in boot module
5. **Follow Naming Conventions**: Use consistent CQRS naming and package structure
6. **Maintain Dependencies**: Ensure unidirectional module dependencies

This modularization strategy provides a solid foundation for building maintainable, scalable microservices while keeping architectural concerns properly separated.