# Community Domain Implementation Gaps

## Overview
This document identifies unimplemented parts in the community domain and provides implementation guidance.

## Current Status Analysis

### 1. Community Service Implementation - CRITICAL
**File:** `src/main/java/com/sunic/community/community/service/CommunityService.java:28-54`

**Status:** All methods are stubbed with empty implementations or return null/hardcoded values.

**Missing Implementations:**
- `retrieveAllCommunity()` - Returns null instead of querying database
- `registerCommunity()` - Returns hardcoded value 1 instead of saving entity
- `retrieveCommunity()` - Returns null instead of fetching by ID
- `modifyCommunity()` - Empty method body
- `deleteCommunity()` - Empty method body
- `joinCommunity()` - Empty method body
- `leaveCommunity()` - Empty method body
- `checkMember()` - Returns hardcoded true instead of checking membership

**Implementation Priority:** HIGH

### 2. Post Domain - COMPLETELY MISSING
**Location:** `src/main/java/com/sunic/community/post/`

**Missing Components:**
- Controllers (directory exists but empty)
- DTOs (directory exists but empty)
- Services (directory exists but empty)
- Repositories (directory exists but empty)
- Exception handlers (directory exists but empty)

**Domain Entities Present:**
- `Post.java` - Complete entity definition
- `Comment.java` - Complete entity definition
- `PostType.java` - Enum definition

**Implementation Priority:** HIGH

### 3. Member Repository Issues
**File:** `src/main/java/com/sunic/community/community/service/CommunityService.java:25-26`

**Issue:** Repository fields not properly injected (missing @Autowired or constructor injection)

**Implementation Priority:** MEDIUM

## Implementation Roadmap

### Phase 1: Community Service Implementation

#### Step 1: Fix Repository Injection
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityRepository communityRepository;  // Add final
    private final MemberRepository memberRepository;        // Add final
```

#### Step 2: Implement Core CRUD Operations
```java
public List<CommunityResponse> retrieveAllCommunity() {
    return communityRepository.findAll()
        .stream()
        .map(Community::toCommunityResponse)
        .collect(Collectors.toList());
}

@Transactional
public int registerCommunity(CommunityRegisterRequest request) {
    Community community = Community.builder()
        .type(request.getType())
        .name(request.getName())
        .description(request.getDescription())
        .managerId(request.getManagerId())
        .managerName(request.getManagerName())
        .managerEmail(request.getManagerEmail())
        .registeredTime(System.currentTimeMillis())
        .registrant(request.getRegistrant())
        .allowSelfJoin(request.isAllowSelfJoin())
        .build();
    
    return communityRepository.save(community).getId();
}
```

#### Step 3: Implement Member Management
```java
@Transactional
public void joinCommunity(MemberJoinRequest request) {
    Community community = communityRepository.findById(request.getCommunityId())
        .orElseThrow(() -> new CommunityNotFoundException("Community not found"));
    
    Member member = Member.builder()
        .userId(request.getUserId())
        .community(community)
        .joinedTime(System.currentTimeMillis())
        .build();
    
    memberRepository.save(member);
    // Update member count
}
```

### Phase 2: Post Domain Implementation

#### Step 1: Create Post Controller
```java
@RestController
@RequestMapping("/community/{communityId}/posts")
@RequiredArgsConstructor
public class PostController implements PostControllerDocs {
    private final PostService postService;
    
    @PostMapping("/")
    public ResponseEntity<BaseResponse> createPost(
        @PathVariable Integer communityId,
        PostCreateRequest request) {
        // Implementation
    }
    
    @GetMapping("/")
    public ResponseEntity<BaseResponse> getPosts(@PathVariable Integer communityId) {
        // Implementation
    }
}
```

#### Step 2: Create Post Service
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    
    @Transactional
    public PostResponse createPost(Integer communityId, PostCreateRequest request) {
        // Validate community exists
        // Create and save post
        // Return response
    }
}
```

#### Step 3: Create Post Repository
```java
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByCommunityIdOrderByRegisteredTimeDesc(Integer communityId);
    List<Post> findByCommunityIdAndPostType(Integer communityId, PostType postType);
}
```

### Phase 3: Missing DTOs

#### Post DTOs needed:
- `PostCreateRequest`
- `PostUpdateRequest`
- `PostResponse`
- `CommentCreateRequest`
- `CommentResponse`

#### Community DTOs to verify:
- Check if all request/response DTOs have proper validation annotations
- Ensure proper field mapping

### Phase 4: Exception Handling

#### Create Custom Exceptions:
```java
public class CommunityNotFoundException extends RuntimeException {
    public CommunityNotFoundException(String message) {
        super(message);
    }
}

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}

public class MembershipException extends RuntimeException {
    public MembershipException(String message) {
        super(message);
    }
}
```

## Implementation Guidelines

### 1. Follow Existing Patterns
- Use the established coding style from `docs/coding-style-guide.md`
- Follow the same controller → service → repository pattern
- Use `BaseResponse` for all API responses
- Apply proper transaction annotations

### 2. Data Validation
- Add Bean Validation annotations to DTOs
- Implement proper error handling in services
- Validate business rules (e.g., user can only join community once)

### 3. Security Considerations
- Implement proper authorization checks
- Validate user permissions for community operations
- Sanitize user inputs

### 4. Testing Requirements
- Unit tests for all service methods
- Integration tests for controllers
- Repository tests for custom queries

## Priority Implementation Order

1. **IMMEDIATE (P0):** Fix CommunityService repository injection and implement basic CRUD
2. **HIGH (P1):** Complete CommunityService member management methods
3. **HIGH (P1):** Implement Post domain (controller, service, repository)
4. **MEDIUM (P2):** Add comprehensive exception handling
5. **MEDIUM (P2):** Implement Comment functionality
6. **LOW (P3):** Add advanced features (search, pagination, etc.)

## Files Requiring Immediate Attention

1. `CommunityService.java` - Complete implementation required
2. `post/controller/` - Create PostController
3. `post/service/` - Create PostService  
4. `post/repository/` - Create PostRepository
5. `post/dto/` - Create all Post DTOs
6. `community/exception/` - Add custom exceptions
7. `post/exception/` - Add post-related exceptions

## Estimated Implementation Effort

- **Community Service completion:** 2-3 days
- **Post domain implementation:** 4-5 days  
- **Exception handling:** 1 day
- **Testing:** 2-3 days
- **Total:** 9-12 days

This implementation plan prioritizes core functionality and follows the existing architectural patterns established in the codebase.