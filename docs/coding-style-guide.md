# Multi-Module Spring Boot Coding Style Guide

This document provides comprehensive coding conventions and style guidelines for **Multi-Module Hexagonal Architecture** Spring Boot projects. It promotes consistency, maintainability, and best practices across development teams.

## Project Overview

This guide is designed for modern Spring Boot applications featuring:
- **Java 17+** (with compatibility notes for Java 11+)
- **Gradle** multi-module build system
- **Multi-Module Hexagonal Architecture** with clean separation of concerns
- **Domain-Driven Design** (DDD) principles
- **CQRS Pattern** for command/query separation
- **RESTful API** design patterns
- **Test-Driven Development** practices

## Multi-Module Architecture Structure

### Project Structure Overview
```
{project-name}/
├── {domain}-spec/         # 📋 Domain Contracts & Models
├── {domain}-aggregate/    # 🏗️  Business Logic & Data Access
├── {domain}-rest/         # 🌐 REST APIs & Event Handling
├── {domain}-client/       # 🔌 External Integration Client
└── {domain}-boot/         # 🚀 Application Bootstrap
```

### Module-Specific Package Structure

#### 1. {domain}-spec Module 📋
**Purpose**: Pure domain contracts and models without implementation details.

```
{domain}-spec/
└── src/main/java/com/{company}/{domain}/spec/
    ├── {subdomain}/
    │   ├── entity/          # Pure domain entities
    │   │   ├── {Entity}.java
    │   │   ├── {EntityType}.java (enums)
    │   │   └── {ValueObject}.java
    │   ├── facade/          # Service interfaces
    │   │   ├── {Domain}Facade.java
    │   │   └── sdo/         # Service Data Objects
    │   │       ├── {Entity}RegisterSdo.java
    │   │       ├── {Entity}ModifySdo.java
    │   │       ├── {Entity}Rdo.java
    │   │       ├── {Entity}Qdo.java
    │   │       └── {Entity}Cdo.java
    │   ├── exception/       # Domain-specific exceptions
    │   │   ├── {Domain}NotFoundException.java
    │   │   └── {Domain}BusinessException.java
    │   └── vo/              # Value objects
    └── shared/              # Cross-subdomain utilities
```

#### 2. {domain}-aggregate Module 🏗️
**Purpose**: Business logic implementation and data access orchestration.

```
{domain}-aggregate/
└── src/main/java/com/{company}/{domain}/aggregate/
    ├── {subdomain}/
    │   ├── logic/           # Business logic implementations
    │   │   ├── {Domain}Logic.java      # Command operations
    │   │   └── {Domain}QueryLogic.java # Query operations
    │   └── store/           # Data access layer
    │       ├── {Domain}Store.java      # Data access facade
    │       ├── jpo/                    # JPA Persistence Objects
    │       │   ├── {Entity}Jpo.java
    │       │   └── {EntityType}Jpo.java
    │       ├── repository/             # Spring Data repositories
    │       │   └── {Entity}Repository.java
    │       └── projection/             # Query projections
    │           └── {Entity}Projection.java
    ├── flow/                # Complex business workflows
    │   └── {Domain}Flow.java
    ├── proxy/               # External service integration
    │   └── {External}Proxy.java
    └── config/              # Persistence configuration
        └── JpaConfig.java
```

#### 3. {domain}-rest Module 🌐
**Purpose**: REST API endpoints and external event handling.

```
{domain}-rest/
└── src/main/java/com/{company}/{domain}/rest/
    ├── rest/
    │   ├── {subdomain}/
    │   │   ├── {Domain}Resource.java      # Public API endpoints
    │   │   ├── {Domain}AdminResource.java # Admin endpoints
    │   │   └── dto/                       # REST-specific DTOs
    │   │       ├── {Entity}CreateRequest.java
    │   │       ├── {Entity}UpdateRequest.java
    │   │       └── {Entity}Response.java
    │   └── common/
    │       └── ApiResponse.java           # Standardized response wrapper
    ├── event/
    │   ├── consumer/        # Event consumers
    │   └── producer/        # Event publishers
    └── config/              # Web configuration
        ├── WebConfig.java
        └── GlobalExceptionHandler.java
```

#### 4. {domain}-client Module 🔌
**Purpose**: External integration client for consuming APIs.

```
{domain}-client/
└── src/main/java/com/{company}/{domain}/client/
    ├── client/
    │   ├── {Domain}Client.java     # Client interface
    │   ├── {Domain}ClientImpl.java # WebClient implementation
    │   ├── dto/                    # Client-specific DTOs
    │   └── ApiResponse.java        # Response wrapper
    ├── config/
    │   └── {Domain}WebClientConfig.java # Client configuration
    └── exception/                  # Client-specific exceptions
        └── {Domain}ClientException.java
```

#### 5. {domain}-boot Module 🚀
**Purpose**: Application bootstrap, configuration, and deployment.

```
{domain}-boot/
└── src/main/java/com/{company}/{domain}/
    ├── {Domain}BootApplication.java    # Main application class
    └── config/                         # Global configuration
        ├── SecurityConfig.java         # Security setup
        ├── SwaggerConfig.java          # API documentation
        └── WebConfig.java              # Web layer config
```

## Core Principles

### 1. Hexagonal Architecture Layers

| Layer              | Module                         | Responsibility                        | Dependencies            |
| ------------------ | ------------------------------ | ------------------------------------- | ----------------------- |
| **Domain**         | {domain}-spec                  | Business rules, entities, contracts   | Minimal (no frameworks) |
| **Application**    | {domain}-aggregate             | Use cases, business logic             | Domain + Infrastructure |
| **Infrastructure** | {domain}-rest, {domain}-client | External interfaces, data persistence | Application + Domain    |
| **Configuration**  | {domain}-boot                  | Application assembly, runtime config  | All layers              |

### 2. Dependency Flow Rules
- ✅ Inner modules can be imported by outer modules
- ❌ Inner modules must NOT depend on outer modules
- ✅ All modules can depend on the spec module
- ✅ Client module is independent and only depends on spec

### 3. CQRS Pattern Implementation
- **Command operations**: Handled by `*Logic` classes (Create, Update, Delete)
- **Query operations**: Handled by `*QueryLogic` classes (Read, Search)
- **Data access**: Orchestrated through `*Store` classes

## Naming Conventions

### 1. Module-Specific Naming Patterns

#### Data Transfer Objects (DTOs)
| Pattern | Purpose                                    | Layer             | Example                    |
| ------- | ------------------------------------------ | ----------------- | -------------------------- |
| `*Jpo`  | **J**PA **P**ersistence **O**bjects        | Aggregate (Store) | `CommunityJpo.java`       |
| `*Sdo`  | **S**ervice **D**ata **O**bjects (Input)   | Spec (Facade)     | `CommunityRegisterSdo.java` |
| `*Rdo`  | **R**esponse **D**ata **O**bjects (Output) | Spec (Facade)     | `CommunityRdo.java`       |
| `*Qdo`  | **Q**uery **D**ata **O**bjects (Search)    | Spec (Facade)     | `CommunitySearchQdo.java` |
| `*Cdo`  | **C**ommand **D**ata **O**bjects (Actions) | Spec (Facade)     | `CommunityApproveCdo.java` |

#### Service & Component Classes
| Pattern       | Purpose                       | Layer     | Example                     |
| ------------- | ----------------------------- | --------- | --------------------------- |
| `*Logic`      | Business logic implementation | Aggregate | `CommunityLogic.java`      |
| `*QueryLogic` | Read-only query operations    | Aggregate | `CommunityQueryLogic.java` |
| `*Store`      | Data access orchestration     | Aggregate | `CommunityStore.java`      |
| `*Flow`       | Complex business workflows    | Aggregate | `CommunityApprovalFlow.java` |
| `*Proxy`      | External service integration  | Aggregate | `UserServiceProxy.java`    |
| `*Resource`   | REST API controllers          | Rest      | `CommunityResource.java`   |
| `*Client`     | External API clients          | Client    | `CommunityClient.java`     |

### 2. Class Structure Examples

#### Spec Module - Pure Domain Entity
```java
// Pure domain entity without JPA annotations
package com.sunic.community.spec.community.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Community {
    private final Integer id;
    private final CommunityType type;
    private final String thumbnail;
    private final String name;
    private final String description;
    private final String managerId;
    private final String managerName;
    private final String managerEmail;
    private final Long memberCount;
    private final Long registeredTime;
    private final Integer registrant;
    private final Long modifiedTime;
    private final Integer modifier;
    private final String secretNumber;
    private final boolean allowSelfJoin;
}
```

#### Spec Module - Facade Interface
```java
// Service interface defining business operations
package com.sunic.community.spec.community.facade;

import com.sunic.community.spec.community.facade.sdo.*;

import java.util.List;

public interface CommunityFacade {
    
    CommunityRdo registerCommunity(CommunityRegisterSdo registerSdo);
    
    CommunityRdo modifyCommunity(CommunityModifySdo modifySdo);
    
    void deleteCommunity(Integer communityId);
    
    CommunityRdo getCommunity(Integer communityId);
    
    List<CommunityRdo> getAllCommunities();
    
    void joinMember(MemberJoinSdo joinSdo);
    
    void leaveMember(MemberLeaveSdo leaveSdo);
    
    boolean checkMembership(Integer communityId, Integer userId);
}
```

#### Spec Module - Service Data Objects (SDOs)
```java
// Input DTO for service operations
package com.sunic.community.spec.community.facade.sdo;

import com.sunic.community.spec.community.entity.CommunityType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommunityRegisterSdo {
    private final CommunityType type;
    private final String thumbnail;
    private final String name;
    private final String description;
    private final String managerId;
    private final String managerName;
    private final String managerEmail;
    private final Integer registrant;
    private final boolean allowSelfJoin;
    private final String secretNumber;
}

// Output DTO for service operations
@Getter
@Builder
@ToString
public class CommunityRdo {
    private final Integer id;
    private final CommunityType type;
    private final String thumbnail;
    private final String name;
    private final String description;
    private final String managerId;
    private final String managerName;
    private final String managerEmail;
    private final Long memberCount;
    private final Long registeredTime;
    private final Integer registrant;
    private final Long modifiedTime;
    private final Integer modifier;
    private final boolean allowSelfJoin;
}
```

#### Aggregate Module - JPA Persistence Objects (JPOs)
```java
// JPA entity with database mappings
package com.sunic.community.aggregate.community.store.jpo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Table(name = "community")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CommunityJpo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private CommunityTypeJpo type;
    
    private String thumbnail;
    private String name;
    private String description;
    private String managerId;
    private String managerName;
    private String managerEmail;

    @ColumnDefault("0")
    private Long memberCount;

    private Long registeredTime;
    private Integer registrant;
    private Long modifiedTime;
    private Integer modifier;
    private String secretNumber;
    private boolean allowSelfJoin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private List<MemberJpo> members;

    // Business methods for domain logic
    public void updateDetails(CommunityTypeJpo type, String thumbnail, 
                            String name, String description, Integer modifier) {
        this.type = type;
        this.thumbnail = thumbnail;
        this.name = name;
        this.description = description;
        this.modifier = modifier;
        this.modifiedTime = System.currentTimeMillis();
    }

    public void incrementMemberCount() {
        this.memberCount = (this.memberCount == null ? 0 : this.memberCount) + 1;
    }

    public void decrementMemberCount() {
        this.memberCount = (this.memberCount == null || this.memberCount <= 0) ? 
                          0 : this.memberCount - 1;
    }
}
```

#### Aggregate Module - Store Pattern
```java
// Data access facade orchestrating repository operations
package com.sunic.community.aggregate.community.store;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import com.sunic.community.aggregate.community.store.repository.CommunityRepository;
import com.sunic.community.spec.community.entity.Community;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommunityStore {
    
    private final CommunityRepository communityRepository;
    
    public Community save(Community community) {
        CommunityJpo jpo = convertToJpo(community);
        CommunityJpo saved = communityRepository.save(jpo);
        return convertToCommunity(saved);
    }
    
    public Community findById(Integer id) {
        CommunityJpo jpo = communityRepository.findById(id)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + id));
        return convertToCommunity(jpo);
    }
    
    public List<Community> findAll() {
        return communityRepository.findAll().stream()
                .map(this::convertToCommunity)
                .collect(Collectors.toList());
    }
    
    // Conversion methods between domain and persistence models
    private Community convertToCommunity(CommunityJpo jpo) {
        return Community.builder()
                .id(jpo.getId())
                .type(convertToCommunityType(jpo.getType()))
                .thumbnail(jpo.getThumbnail())
                .name(jpo.getName())
                .description(jpo.getDescription())
                // ... other mappings
                .build();
    }
    
    private CommunityJpo convertToJpo(Community community) {
        return CommunityJpo.builder()
                .id(community.getId())
                .type(convertToCommunityTypeJpo(community.getType()))
                .thumbnail(community.getThumbnail())
                .name(community.getName())
                .description(community.getDescription())
                // ... other mappings
                .build();
    }
}
```

#### Aggregate Module - CQRS Logic Classes
```java
// Business logic implementing facade interface
package com.sunic.community.aggregate.community.logic;

import com.sunic.community.aggregate.community.store.CommunityStore;
import com.sunic.community.spec.community.facade.CommunityFacade;
import com.sunic.community.spec.community.facade.sdo.*;
import com.sunic.community.spec.community.entity.Community;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityLogic implements CommunityFacade {
    
    private final CommunityStore communityStore;
    
    @Override
    @Transactional
    public CommunityRdo registerCommunity(CommunityRegisterSdo registerSdo) {
        Community community = Community.builder()
                .type(registerSdo.getType())
                .thumbnail(registerSdo.getThumbnail())
                .name(registerSdo.getName())
                .description(registerSdo.getDescription())
                .managerId(registerSdo.getManagerId())
                .managerName(registerSdo.getManagerName())
                .managerEmail(registerSdo.getManagerEmail())
                .memberCount(0L)
                .registeredTime(System.currentTimeMillis())
                .registrant(registerSdo.getRegistrant())
                .secretNumber(registerSdo.getSecretNumber())
                .allowSelfJoin(registerSdo.isAllowSelfJoin())
                .build();
        
        Community saved = communityStore.save(community);
        return convertToCommunityRdo(saved);
    }
    
    @Override
    public CommunityRdo getCommunity(Integer communityId) {
        Community community = communityStore.findById(communityId);
        return convertToCommunityRdo(community);
    }
    
    @Override
    public List<CommunityRdo> getAllCommunities() {
        return communityStore.findAll().stream()
                .map(this::convertToCommunityRdo)
                .collect(Collectors.toList());
    }
    
    private CommunityRdo convertToCommunityRdo(Community community) {
        return CommunityRdo.builder()
                .id(community.getId())
                .type(community.getType())
                .thumbnail(community.getThumbnail())
                .name(community.getName())
                .description(community.getDescription())
                // ... other mappings
                .build();
    }
}
```

#### Rest Module - Resource Controllers
```java
// REST API controller implementing clean endpoints
package com.sunic.community.rest.rest.community;

import com.sunic.community.rest.rest.community.dto.CommunityRegisterRequest;
import com.sunic.community.spec.community.facade.CommunityFacade;
import com.sunic.community.spec.community.facade.sdo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityResource {
    
    private final CommunityFacade communityFacade;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommunityRdo>>> getAllCommunities() {
        List<CommunityRdo> communities = communityFacade.getAllCommunities();
        return ResponseEntity.ok(ApiResponse.success("Communities retrieved successfully", communities));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommunityRdo>> getCommunity(@PathVariable Integer id) {
        CommunityRdo community = communityFacade.getCommunity(id);
        return ResponseEntity.ok(ApiResponse.success("Community retrieved successfully", community));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CommunityRdo>> registerCommunity(
            @Valid @RequestBody CommunityRegisterRequest request) {
        CommunityRegisterSdo sdo = CommunityRegisterSdo.builder()
                .type(request.getType())
                .thumbnail(request.getThumbnail())
                .name(request.getName())
                .description(request.getDescription())
                .managerId(request.getManagerId())
                .managerName(request.getManagerName())
                .managerEmail(request.getManagerEmail())
                .registrant(request.getRegistrant())
                .allowSelfJoin(request.isAllowSelfJoin())
                .secretNumber(request.getSecretNumber())
                .build();
        
        CommunityRdo community = communityFacade.registerCommunity(sdo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Community registered successfully", community));
    }
}
```

#### Rest Module - Request/Response DTOs
```java
// REST-specific request DTO with validation
package com.sunic.community.rest.rest.community.dto;

import com.sunic.community.spec.community.entity.CommunityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityRegisterRequest {
    
    @NotNull(message = "Community type is required")
    private CommunityType type;
    
    private String thumbnail;
    
    @NotBlank(message = "Community name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Manager ID is required")
    private String managerId;
    
    @NotBlank(message = "Manager name is required")
    private String managerName;
    
    @NotBlank(message = "Manager email is required")
    private String managerEmail;
    
    @NotNull(message = "Registrant is required")
    private Integer registrant;
    
    private boolean allowSelfJoin;
    
    private String secretNumber;
}
```

#### Rest Module - Global Exception Handling
```java
// Centralized exception handling for REST layer
package com.sunic.community.rest.config;

import com.sunic.community.rest.config.ApiResponse;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CommunityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommunityNotFound(CommunityNotFoundException ex) {
        log.error("Community not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }
}
```

#### Client Module - External Integration
```java
// Reactive client for external integration
package com.sunic.community.client.client;

import com.sunic.community.spec.community.facade.sdo.CommunityRdo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityClientImpl implements CommunityClient {
    
    private final WebClient webClient;
    private static final String COMMUNITIES_PATH = "/api/v1/communities";
    
    @Override
    @CircuitBreaker(name = "community-service", fallbackMethod = "getAllCommunitiesFallback")
    public Flux<CommunityRdo> getAllCommunities() {
        return webClient.get()
                .uri(COMMUNITIES_PATH)
                .retrieve()
                .bodyToFlux(ApiResponse.class)
                .map(response -> (CommunityRdo) response.getData())
                .onErrorResume(throwable -> {
                    log.error("Error getting all communities", throwable);
                    return Flux.empty();
                });
    }
    
    public Flux<CommunityRdo> getAllCommunitiesFallback(Exception ex) {
        log.warn("Fallback: getAllCommunities failed", ex);
        return Flux.empty();
    }
}
```

## Build Configuration

### Multi-Module Gradle Setup
```gradle
// Root build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.4-SNAPSHOT' apply false
    id 'io.spring.dependency-management' version '1.1.7' apply false
}

allprojects {
    group = 'com.sunic'
    version = '0.0.1-SNAPSHOT'
    
    repositories {
        mavenCentral()
        maven { url = 'https://repo.spring.io/snapshot' }
    }
}

subprojects {
    apply plugin: 'java'
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
    
    dependencies {
        compileOnly 'org.projectlombok:lombok'
        annotationProcessor 'org.projectlombok:lombok'
        
        testImplementation 'org.junit.jupiter:junit-jupiter'
        testImplementation 'org.assertj:assertj-core'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    }
    
    test {
        useJUnitPlatform()
    }
}

// Module-specific configurations
configure(project(':community-spec')) {
    description = 'Domain contracts and specifications'
}

configure(project(':community-aggregate')) {
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'
    description = 'Business logic and data access layer'
    
    bootJar { enabled = false }
    jar { enabled = true; archiveClassifier = '' }
}

configure(project(':community-boot')) {
    apply plugin: 'org.springframework.boot'
    apply plugin: 'io.spring.dependency-management'
    description = 'Application bootstrap and runtime'
    
    bootJar { 
        enabled = true
        archiveClassifier = 'boot'
        archiveFileName = 'community-service.jar'
    }
    jar { enabled = false }
}
```

### Module Dependencies
```gradle
// community-spec/build.gradle
dependencies {
    implementation 'jakarta.validation:jakarta.validation-api:3.0.2'
    implementation 'com.fasterxml.jackson.core:jackson-annotations:2.15.2'
}

// community-aggregate/build.gradle
dependencies {
    implementation project(':community-spec')
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework:spring-context'
    implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
}

// community-rest/build.gradle
dependencies {
    implementation project(':community-aggregate')
    implementation project(':community-spec')
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
}

// community-boot/build.gradle
dependencies {
    implementation project(':community-rest')
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9'
}
```

## Configuration Management

### Application Configuration (community-boot)
```yaml
# application.yml
spring:
  application:
    name: community-service
  profiles:
    active: local
    
  datasource:
    url: jdbc:mysql://localhost:3306/sunic
    username: mysuni
    password: mysuni
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        show_sql: false
        use_sql_comments: false
        format_sql: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        
  jwt:
    secretKey: Y2hsZGtkdWRyaGsgZGJkbGZybmpzZG1sIHRqZG5mY2hzc2hhIGR1Z29kZG1zIHJQdGhyZWhsc2VrLiBka3ZkbWZoZWggclB0aHI=

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

springdoc:
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    com.sunic.community: INFO

---
# Profile-specific configurations
spring:
  config:
    activate:
      on-profile: local
  jpa:
    show-sql: true

logging:
  level:
    com.sunic.community: DEBUG

---
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false

logging:
  level:
    com.sunic.community: WARN
```

## Code Style Standards

### 1. Formatting Rules
- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters maximum
- **Blank lines**: Single line between methods and logical blocks
- **Imports**: Organized by groups with blank lines between

### 2. Lombok Usage
- Use `@Builder` for immutable objects in spec module
- Use `@RequiredArgsConstructor` for dependency injection
- Use `@Getter` instead of `@Data` for better encapsulation
- Use `@ToString` with `exclude` for entities with relationships

### 3. Exception Handling
```java
// Domain-specific exceptions in spec module
public class CommunityNotFoundException extends RuntimeException {
    public CommunityNotFoundException(String message) {
        super(message);
    }
    
    public CommunityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 4. Testing Strategy
```java
// Unit test for Logic classes
@ExtendWith(MockitoExtension.class)
class CommunityLogicTest {
    
    @Mock
    private CommunityStore communityStore;
    
    @InjectMocks
    private CommunityLogic communityLogic;
    
    @Test
    @DisplayName("Should create community when valid input provided")
    void shouldCreateCommunity_WhenValidInput() {
        // Given
        CommunityRegisterSdo sdo = CommunityRegisterSdo.builder()
                .name("Test Community")
                .type(CommunityType.OPEN)
                .build();
        
        // When & Then
        assertThat(result).isNotNull();
    }
}
```

## Best Practices

### 1. Module Design
- Keep spec module pure (no framework dependencies)
- Use immutable objects in spec module
- Implement business logic in aggregate module
- Keep controllers thin in rest module
- Use reactive clients in client module

### 2. Security
- Validate all inputs at REST layer
- Use parameterized queries in repositories
- Never log sensitive information
- Implement proper authentication/authorization

### 3. Performance
- Use lazy loading for JPA relationships
- Implement appropriate caching strategies
- Use pagination for large datasets
- Optimize database queries with indexes

### 4. Monitoring
- Use structured logging with correlation IDs
- Implement health checks in boot module
- Monitor application metrics
- Set up proper error alerting

## Migration Guide

### From Monolithic to Multi-Module
1. **Create module structure** following the pattern
2. **Move domain models** to spec module (remove JPA annotations)
3. **Create JPOs** in aggregate module with JPA mappings
4. **Implement Logic classes** with CQRS pattern
5. **Create Store classes** for data access orchestration
6. **Move controllers** to rest module and update imports
7. **Update build configuration** for multi-module setup
8. **Test module dependencies** and fix any circular references

## Quick Start Checklist

### For New Projects
- [ ] Create multi-module structure with proper naming
- [ ] Set up Gradle build configuration for all modules
- [ ] Implement domain entities in spec module
- [ ] Create facade interfaces and SDOs/RDOs
- [ ] Implement business logic with CQRS pattern
- [ ] Set up JPA entities and repositories in aggregate
- [ ] Create REST controllers in rest module
- [ ] Configure security and documentation in boot module
- [ ] Set up testing strategy for each module
- [ ] Configure monitoring and health checks

### Code Review Checklist
- [ ] **Architecture**: Proper module separation and dependency flow
- [ ] **Naming**: Consistent use of Jpo/Sdo/Rdo/Logic/Store patterns
- [ ] **CQRS**: Proper separation of command and query operations
- [ ] **Security**: Proper validation and no hardcoded secrets
- [ ] **Testing**: Adequate test coverage for business logic
- [ ] **Performance**: Optimized queries and proper caching
- [ ] **Documentation**: API endpoints properly documented
- [ ] **Configuration**: Environment-specific settings externalized

## Conclusion

This multi-module hexagonal architecture coding style guide ensures consistent, maintainable, and scalable Spring Boot applications. The clear separation of concerns, proper dependency management, and standardized naming conventions create a solid foundation for complex enterprise applications.

Teams should regularly review and update this guide based on project needs and Spring Boot evolution while maintaining consistency across all modules.