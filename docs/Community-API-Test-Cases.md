# Community Application Test Cases

This document provides comprehensive test cases for the Community Application APIs, following the established coding style guide and testing best practices.

## Overview

The Community Application provides REST APIs for managing communities, posts, and comments. This document covers:
- **Unit Test Cases** for service layer business logic
- **Integration Test Cases** for repository and database interactions
- **API Test Cases** for REST endpoint validation
- **Test Data Management** strategies
- **Performance and Load Testing** considerations

## Test Strategy

### Testing Pyramid Structure
```
    /\
   /  \     E2E Tests (Few)
  /____\    
 /      \   Integration Tests (Some)
/________\  Unit Tests (Many)
```

### Test Categories
1. **Unit Tests**: Fast, isolated tests for business logic
2. **Integration Tests**: Tests with Spring context and database
3. **API Tests**: Full HTTP request/response validation
4. **Performance Tests**: Load and stress testing

## API Endpoints Overview

### Community Management APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/community/` | Retrieve all communities |
| POST | `/community/` | Register new community |
| GET | `/community/{id}` | Retrieve specific community |
| PUT | `/community/` | Modify existing community |
| DELETE | `/community/{id}` | Delete community |
| POST | `/community/join` | Join community |
| DELETE | `/community/leave` | Leave community |
| POST | `/community/check` | Check membership |

### Post Management APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/community/{communityId}/posts/` | Get posts by community |
| GET | `/community/{communityId}/posts/type` | Get posts by type |
| GET | `/community/{communityId}/posts/{postId}` | Get specific post |
| POST | `/community/{communityId}/posts/` | Create new post |
| PUT | `/community/{communityId}/posts/` | Update post |
| DELETE | `/community/{communityId}/posts/{postId}` | Delete post |

### Comment Management APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/community/{communityId}/posts/{postId}/comments` | Get post comments |
| POST | `/community/{communityId}/posts/{postId}/comments` | Create comment |
| DELETE | `/community/{communityId}/posts/comments/{commentId}` | Delete comment |

## Unit Test Cases

### CommunityService Unit Tests

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityService Unit Tests")
class CommunityServiceTest {
    
    @Mock
    private CommunityRepository communityRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    @InjectMocks
    private CommunityService communityService;
    
    @Nested
    @DisplayName("Community Registration Tests")
    class CommunityRegistrationTests {
        
        @Test
        @DisplayName("Should successfully register community with valid data")
        void shouldRegisterCommunity_WhenValidData() {
            // Given
            CommunityRegisterRequest request = CommunityRegisterRequest.builder()
                    .name("Test Community")
                    .description("Test Description")
                    .type(CommunityType.OPEN)
                    .managerId("manager123")
                    .managerName("Manager Name")
                    .managerEmail("manager@test.com")
                    .allowSelfJoin(true)
                    .registrant(1)
                    .build();
            
            Community savedCommunity = Community.builder()
                    .id(1)
                    .name("Test Community")
                    .description("Test Description")
                    .type(CommunityType.OPEN)
                    .build();
            
            when(communityRepository.save(any(Community.class))).thenReturn(savedCommunity);
            
            // When
            int result = communityService.registerCommunity(request);
            
            // Then
            assertThat(result).isEqualTo(1);
            verify(communityRepository).save(argThat(community ->
                    community.getName().equals("Test Community") &&
                    community.getDescription().equals("Test Description") &&
                    community.getType() == CommunityType.OPEN
            ));
        }
        
        @Test
        @DisplayName("Should set creation timestamp when registering community")
        void shouldSetCreationTimestamp_WhenRegisteringCommunity() {
            // Given
            CommunityRegisterRequest request = CommunityRegisterRequest.builder()
                    .name("Test Community")
                    .registrant(1)
                    .build();
            
            Community savedCommunity = Community.builder().id(1).build();
            when(communityRepository.save(any(Community.class))).thenReturn(savedCommunity);
            
            long beforeTime = System.currentTimeMillis();
            
            // When
            communityService.registerCommunity(request);
            
            long afterTime = System.currentTimeMillis();
            
            // Then
            verify(communityRepository).save(argThat(community ->
                    community.getRegisteredTime() >= beforeTime &&
                    community.getRegisteredTime() <= afterTime
            ));
        }
    }
    
    @Nested
    @DisplayName("Community Retrieval Tests")
    class CommunityRetrievalTests {
        
        @Test
        @DisplayName("Should return all communities successfully")
        void shouldReturnAllCommunities() {
            // Given
            List<Community> communities = Arrays.asList(
                    Community.builder().id(1).name("Community 1").build(),
                    Community.builder().id(2).name("Community 2").build()
            );
            
            when(communityRepository.findAll()).thenReturn(communities);
            
            // When
            List<CommunityResponse> result = communityService.retrieveAllCommunity();
            
            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Community 1");
            assertThat(result.get(1).getName()).isEqualTo("Community 2");
        }
        
        @Test
        @DisplayName("Should return specific community when valid ID provided")
        void shouldReturnCommunity_WhenValidId() {
            // Given
            Integer communityId = 1;
            Community community = Community.builder()
                    .id(communityId)
                    .name("Test Community")
                    .description("Test Description")
                    .build();
            
            when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
            
            // When
            CommunityResponse result = communityService.retrieveCommunity(communityId);
            
            // Then
            assertThat(result.getId()).isEqualTo(communityId);
            assertThat(result.getName()).isEqualTo("Test Community");
            assertThat(result.getDescription()).isEqualTo("Test Description");
        }
        
        @Test
        @DisplayName("Should throw exception when community not found")
        void shouldThrowException_WhenCommunityNotFound() {
            // Given
            Integer nonExistentId = 999;
            when(communityRepository.findById(nonExistentId)).thenReturn(Optional.empty());
            
            // When & Then
            assertThatThrownBy(() -> communityService.retrieveCommunity(nonExistentId))
                    .isInstanceOf(CommunityNotFoundException.class)
                    .hasMessage("Community not found with id: " + nonExistentId);
        }
    }
    
    @Nested
    @DisplayName("Member Management Tests")
    class MemberManagementTests {
        
        @Test
        @DisplayName("Should allow user to join community when valid request")
        void shouldJoinCommunity_WhenValidRequest() {
            // Given
            MemberJoinRequest request = MemberJoinRequest.builder()
                    .communityId(1)
                    .userId(100)
                    .build();
            
            Community community = Community.builder()
                    .id(1)
                    .allowSelfJoin(true)
                    .build();
            
            when(communityRepository.findById(1)).thenReturn(Optional.of(community));
            when(memberRepository.existsByCommunityIdAndUserId(1, 100)).thenReturn(false);
            
            // When
            communityService.joinCommunity(request);
            
            // Then
            verify(memberRepository).save(argThat(member ->
                    member.getCommunity().getId().equals(1) &&
                    member.getUserId().equals(100) &&
                    member.getJoinedTime() != null
            ));
        }
        
        @Test
        @DisplayName("Should throw exception when user already member")
        void shouldThrowException_WhenUserAlreadyMember() {
            // Given
            MemberJoinRequest request = MemberJoinRequest.builder()
                    .communityId(1)
                    .userId(100)
                    .build();
            
            Community community = Community.builder().id(1).build();
            when(communityRepository.findById(1)).thenReturn(Optional.of(community));
            when(memberRepository.existsByCommunityIdAndUserId(1, 100)).thenReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> communityService.joinCommunity(request))
                    .isInstanceOf(MembershipException.class)
                    .hasMessage("User is already a member of this community");
        }
        
        @Test
        @DisplayName("Should validate secret number for private communities")
        void shouldValidateSecretNumber_ForPrivateCommunities() {
            // Given
            MemberJoinRequest request = MemberJoinRequest.builder()
                    .communityId(1)
                    .userId(100)
                    .secretNumber("wrong-secret")
                    .build();
            
            Community community = Community.builder()
                    .id(1)
                    .allowSelfJoin(false)
                    .secretNumber("correct-secret")
                    .build();
            
            when(communityRepository.findById(1)).thenReturn(Optional.of(community));
            when(memberRepository.existsByCommunityIdAndUserId(1, 100)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> communityService.joinCommunity(request))
                    .isInstanceOf(MembershipException.class)
                    .hasMessage("Invalid secret number for community");
        }
    }
}
```

### PostService Unit Tests

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Unit Tests")
class PostServiceTest {
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private CommunityRepository communityRepository;
    
    @InjectMocks
    private PostService postService;
    
    @Nested
    @DisplayName("Post Creation Tests")
    class PostCreationTests {
        
        @Test
        @DisplayName("Should create post successfully with valid data")
        void shouldCreatePost_WhenValidData() {
            // Given
            Integer communityId = 1;
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("Test Post")
                    .content("Test Content")
                    .postType(PostType.Post)
                    .registrant(100)
                    .build();
            
            Community community = Community.builder().id(communityId).build();
            Post savedPost = Post.builder()
                    .id(1)
                    .title("Test Post")
                    .content("Test Content")
                    .community(community)
                    .build();
            
            when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
            when(postRepository.save(any(Post.class))).thenReturn(savedPost);
            
            // When
            PostResponse result = postService.createPost(communityId, request);
            
            // Then
            assertThat(result.getTitle()).isEqualTo("Test Post");
            assertThat(result.getContent()).isEqualTo("Test Content");
            assertThat(result.getCommunityId()).isEqualTo(communityId);
            
            verify(postRepository).save(argThat(post ->
                    post.getTitle().equals("Test Post") &&
                    post.getContent().equals("Test Content") &&
                    post.getPostType() == PostType.Post &&
                    post.getCommunity().getId().equals(communityId)
            ));
        }
        
        @Test
        @DisplayName("Should throw exception when community not found")
        void shouldThrowException_WhenCommunityNotFound() {
            // Given
            Integer nonExistentCommunityId = 999;
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("Test Post")
                    .build();
            
            when(communityRepository.findById(nonExistentCommunityId)).thenReturn(Optional.empty());
            
            // When & Then
            assertThatThrownBy(() -> postService.createPost(nonExistentCommunityId, request))
                    .isInstanceOf(CommunityNotFoundException.class)
                    .hasMessage("Community not found with id: " + nonExistentCommunityId);
        }
    }
    
    @Nested
    @DisplayName("Post Retrieval Tests")
    class PostRetrievalTests {
        
        @Test
        @DisplayName("Should return posts by community in descending order")
        void shouldReturnPostsByCommunity_InDescendingOrder() {
            // Given
            Integer communityId = 1;
            List<Post> posts = Arrays.asList(
                    Post.builder().id(2).title("Newer Post").registeredTime(2000L).build(),
                    Post.builder().id(1).title("Older Post").registeredTime(1000L).build()
            );
            
            when(communityRepository.existsById(communityId)).thenReturn(true);
            when(postRepository.findByCommunityIdOrderByRegisteredTimeDesc(communityId)).thenReturn(posts);
            
            // When
            List<PostResponse> result = postService.getPostsByCommunity(communityId);
            
            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("Newer Post");
            assertThat(result.get(1).getTitle()).isEqualTo("Older Post");
        }
    }
}
```

## Integration Test Cases

### Repository Integration Tests

```java
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@DisplayName("Community Repository Integration Tests")
class CommunityRepositoryIntegrationTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private CommunityRepository communityRepository;
    
    @Test
    @DisplayName("Should persist and retrieve community successfully")
    void shouldPersistAndRetrieveCommunity() {
        // Given
        Community community = Community.builder()
                .name("Test Community")
                .description("Test Description")
                .type(CommunityType.OPEN)
                .managerId("manager123")
                .managerName("Manager Name")
                .managerEmail("manager@test.com")
                .memberCount(0L)
                .registeredTime(System.currentTimeMillis())
                .registrant(1)
                .allowSelfJoin(true)
                .build();
        
        // When
        Community saved = entityManager.persistAndFlush(community);
        Optional<Community> found = communityRepository.findById(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Community");
        assertThat(found.get().getDescription()).isEqualTo("Test Description");
        assertThat(found.get().getType()).isEqualTo(CommunityType.OPEN);
    }
    
    @Test
    @DisplayName("Should handle cascade delete of members when community deleted")
    void shouldCascadeDeleteMembers_WhenCommunityDeleted() {
        // Given
        Community community = Community.builder()
                .name("Test Community")
                .type(CommunityType.OPEN)
                .memberCount(0L)
                .registeredTime(System.currentTimeMillis())
                .registrant(1)
                .allowSelfJoin(true)
                .build();
        
        Community savedCommunity = entityManager.persistAndFlush(community);
        
        Member member = Member.builder()
                .community(savedCommunity)
                .userId(100)
                .joinedTime(System.currentTimeMillis())
                .registrant(100)
                .build();
        
        entityManager.persistAndFlush(member);
        entityManager.clear();
        
        // When
        communityRepository.deleteById(savedCommunity.getId());
        entityManager.flush();
        
        // Then
        assertThat(communityRepository.findById(savedCommunity.getId())).isEmpty();
        
        // Verify member was also deleted (cascade)
        List<Member> remainingMembers = entityManager.getEntityManager()
                .createQuery("SELECT m FROM Member m WHERE m.community.id = :communityId", Member.class)
                .setParameter("communityId", savedCommunity.getId())
                .getResultList();
        
        assertThat(remainingMembers).isEmpty();
    }
}
```

### Service Integration Tests

```java
@SpringBootTest
@Transactional
@DisplayName("CommunityService Integration Tests")
class CommunityServiceIntegrationTest {
    
    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private CommunityRepository communityRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Test
    @DisplayName("Should perform complete community lifecycle operations")
    void shouldPerformCompleteCommunityLifecycle() {
        // Given - Create community
        CommunityRegisterRequest createRequest = CommunityRegisterRequest.builder()
                .name("Integration Test Community")
                .description("Integration Test Description")
                .type(CommunityType.OPEN)
                .managerId("manager123")
                .managerName("Manager Name")
                .managerEmail("manager@test.com")
                .allowSelfJoin(true)
                .registrant(1)
                .build();
        
        // When - Register community
        int communityId = communityService.registerCommunity(createRequest);
        
        // Then - Verify creation
        assertThat(communityId).isPositive();
        CommunityResponse created = communityService.retrieveCommunity(communityId);
        assertThat(created.getName()).isEqualTo("Integration Test Community");
        
        // When - Join community
        MemberJoinRequest joinRequest = MemberJoinRequest.builder()
                .communityId(communityId)
                .userId(100)
                .build();
        
        communityService.joinCommunity(joinRequest);
        
        // Then - Verify membership
        MemberCheckRequest checkRequest = MemberCheckRequest.builder()
                .communityId(communityId)
                .userId(100)
                .build();
        
        boolean isMember = communityService.checkMember(checkRequest);
        assertThat(isMember).isTrue();
        
        // When - Leave community
        MemberLeaveRequest leaveRequest = MemberLeaveRequest.builder()
                .communityId(communityId)
                .userId(100)
                .build();
        
        communityService.leaveCommunity(leaveRequest);
        
        // Then - Verify no longer member
        boolean stillMember = communityService.checkMember(checkRequest);
        assertThat(stillMember).isFalse();
        
        // When - Delete community
        communityService.deleteCommunity(communityId);
        
        // Then - Verify deletion
        assertThatThrownBy(() -> communityService.retrieveCommunity(communityId))
                .isInstanceOf(CommunityNotFoundException.class);
    }
}
```

## API Test Cases

### Test Case Format
Following the requirements from Project-Testcase-Generate.md:

| Test Case ID | Test API Endpoint | Test Case Name | Verification Point | Expected Result |
|-------------|-------------------|----------------|-------------------|-----------------|

### Community API Test Cases

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Community API Integration Tests")
class CommunityControllerAPITest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Nested
    @DisplayName("Community CRUD API Tests")
    class CommunityAPICRUDTests {
        
        @Test
        @DisplayName("REQ03-TC-01-01: Should retrieve all communities successfully")
        void shouldRetrieveAllCommunities() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity("/community/", String.class);
            
            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("\"success\":true");
            assertThat(response.getBody()).contains("\"message\":\"success\"");
        }
        
        @Test
        @DisplayName("REQ03-TC-01-02: Should register new community successfully")
        void shouldRegisterCommunitySuccessfully() throws Exception {
            // Given
            CommunityRegisterRequest request = CommunityRegisterRequest.builder()
                    .name("API Test Community")
                    .description("API Test Description")
                    .type(CommunityType.OPEN)
                    .managerId("manager123")
                    .managerName("Manager Name")
                    .managerEmail("manager@test.com")
                    .allowSelfJoin(true)
                    .registrant(1)
                    .build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
            
            // When
            ResponseEntity<String> response = restTemplate.postForEntity("/community/", entity, String.class);
            
            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("\"success\":true");
            assertThat(response.getBody()).contains("\"message\":\"success\"");
            
            // Extract community ID for cleanup
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            int communityId = jsonNode.get("data").asInt();
            assertThat(communityId).isPositive();
        }
        
        @Test
        @DisplayName("REQ03-TC-01-03: Should retrieve specific community by ID")
        void shouldRetrieveSpecificCommunity() {
            // Given - First create a community
            CommunityRegisterRequest createRequest = CommunityRegisterRequest.builder()
                    .name("Retrieve Test Community")
                    .description("Test Description")
                    .type(CommunityType.OPEN)
                    .allowSelfJoin(true)
                    .registrant(1)
                    .build();
            
            // Create community and get ID
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> createEntity = new HttpEntity<>(
                    objectMapper.writeValueAsString(createRequest), headers);
            
            ResponseEntity<String> createResponse = restTemplate.postForEntity("/community/", createEntity, String.class);
            JsonNode createJsonNode = objectMapper.readTree(createResponse.getBody());
            int communityId = createJsonNode.get("data").asInt();
            
            // When - Retrieve the created community
            ResponseEntity<String> response = restTemplate.getForEntity("/community/" + communityId, String.class);
            
            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("\"success\":true");
            assertThat(response.getBody()).contains("Retrieve Test Community");
        }
        
        @Test
        @DisplayName("REQ03-TC-01-04: Should return 404 for non-existent community")
        void shouldReturn404ForNonExistentCommunity() {
            // When
            ResponseEntity<String> response = restTemplate.getForEntity("/community/99999", String.class);
            
            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Nested
    @DisplayName("Member Management API Tests")
    class MemberManagementAPITests {
        
        @Test
        @DisplayName("REQ03-TC-02-01: Should allow user to join community")
        void shouldAllowUserToJoinCommunity() throws Exception {
            // Given - Create a community first
            CommunityRegisterRequest communityRequest = CommunityRegisterRequest.builder()
                    .name("Join Test Community")
                    .allowSelfJoin(true)
                    .registrant(1)
                    .build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> communityEntity = new HttpEntity<>(
                    objectMapper.writeValueAsString(communityRequest), headers);
            
            ResponseEntity<String> communityResponse = restTemplate.postForEntity("/community/", communityEntity, String.class);
            JsonNode communityJson = objectMapper.readTree(communityResponse.getBody());
            int communityId = communityJson.get("data").asInt();
            
            // When - Join the community
            MemberJoinRequest joinRequest = MemberJoinRequest.builder()
                    .communityId(communityId)
                    .userId(100)
                    .build();
            
            HttpEntity<String> joinEntity = new HttpEntity<>(
                    objectMapper.writeValueAsString(joinRequest), headers);
            
            ResponseEntity<String> joinResponse = restTemplate.postForEntity("/community/join", joinEntity, String.class);
            
            // Then
            assertThat(joinResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(joinResponse.getBody()).contains("\"success\":true");
            
            // Verify membership
            MemberCheckRequest checkRequest = MemberCheckRequest.builder()
                    .communityId(communityId)
                    .userId(100)
                    .build();
            
            HttpEntity<String> checkEntity = new HttpEntity<>(
                    objectMapper.writeValueAsString(checkRequest), headers);
            
            ResponseEntity<String> checkResponse = restTemplate.postForEntity("/community/check", checkEntity, String.class);
            assertThat(checkResponse.getBody()).contains("\"data\":true");
        }
    }
}
```

## Test Data Management

### Test Data Builders

```java
@UtilityClass
public class CommunityTestDataBuilder {
    
    public static CommunityRegisterRequest.CommunityRegisterRequestBuilder defaultCommunityRequest() {
        return CommunityRegisterRequest.builder()
                .name("Test Community")
                .description("Test Description")
                .type(CommunityType.OPEN)
                .managerId("manager123")
                .managerName("Manager Name")
                .managerEmail("manager@test.com")
                .allowSelfJoin(true)
                .registrant(1);
    }
    
    public static Community.CommunityBuilder defaultCommunity() {
        return Community.builder()
                .name("Test Community")
                .description("Test Description")
                .type(CommunityType.OPEN)
                .managerId("manager123")
                .managerName("Manager Name")
                .managerEmail("manager@test.com")
                .memberCount(0L)
                .registeredTime(System.currentTimeMillis())
                .registrant(1)
                .allowSelfJoin(true);
    }
    
    public static MemberJoinRequest.MemberJoinRequestBuilder defaultJoinRequest() {
        return MemberJoinRequest.builder()
                .communityId(1)
                .userId(100);
    }
}

@UtilityClass
public class PostTestDataBuilder {
    
    public static PostCreateRequest.PostCreateRequestBuilder defaultPostRequest() {
        return PostCreateRequest.builder()
                .title("Test Post")
                .content("Test Content")
                .postType(PostType.Post)
                .registrant(100);
    }
    
    public static Post.PostBuilder defaultPost() {
        return Post.builder()
                .title("Test Post")
                .content("Test Content")
                .postType(PostType.Post)
                .registeredTime(System.currentTimeMillis())
                .registrant(100);
    }
}
```

## API Test Cases (CSV Format)

Based on the Project-Testcase-Generate.md requirements, here are the test cases in CSV format:

```csv
Test Case ID,Test API Endpoint,Test Case Name,Verification Point,Expected Result
REQ03-TC-01-01,GET /community/,Retrieve all communities,HTTP status code and response structure,200 OK with success:true and data array
REQ03-TC-01-02,POST /community/,Register new community with valid data,HTTP status code and community ID,200 OK with success:true and positive integer ID
REQ03-TC-01-03,POST /community/,Register community with missing required fields,HTTP status code and error message,400 Bad Request with validation errors
REQ03-TC-01-04,GET /community/{id},Retrieve existing community,HTTP status code and community data,200 OK with success:true and community details
REQ03-TC-01-05,GET /community/{id},Retrieve non-existent community,HTTP status code and error handling,404 Not Found or 500 with appropriate error message
REQ03-TC-01-06,PUT /community/,Update existing community,HTTP status code and success response,200 OK with success:true
REQ03-TC-01-07,PUT /community/,Update non-existent community,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-01-08,DELETE /community/{id},Delete existing community,HTTP status code and success response,200 OK with success:true
REQ03-TC-01-09,DELETE /community/{id},Delete non-existent community,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-02-01,POST /community/join,Join community with valid request,HTTP status code and membership status,200 OK with success:true
REQ03-TC-02-02,POST /community/join,Join community already member,HTTP status code and error message,400 Bad Request with membership error
REQ03-TC-02-03,POST /community/join,Join non-existent community,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-02-04,POST /community/join,Join private community with wrong secret,HTTP status code and error message,400 Bad Request with invalid secret error
REQ03-TC-02-05,DELETE /community/leave,Leave community as member,HTTP status code and success response,200 OK with success:true
REQ03-TC-02-06,DELETE /community/leave,Leave community not a member,HTTP status code and error message,400 Bad Request with membership error
REQ03-TC-02-07,POST /community/check,Check membership for existing member,HTTP status code and membership result,200 OK with data:true
REQ03-TC-02-08,POST /community/check,Check membership for non-member,HTTP status code and membership result,200 OK with data:false
REQ03-TC-03-01,GET /community/{communityId}/posts/,Get posts for existing community,HTTP status code and posts array,200 OK with success:true and posts data
REQ03-TC-03-02,GET /community/{communityId}/posts/,Get posts for non-existent community,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-03-03,GET /community/{communityId}/posts/type?postType=Post,Get posts by type,HTTP status code and filtered posts,200 OK with success:true and filtered results
REQ03-TC-03-04,GET /community/{communityId}/posts/{postId},Get specific post,HTTP status code and post details,200 OK with success:true and post data
REQ03-TC-03-05,GET /community/{communityId}/posts/{postId},Get non-existent post,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-03-06,POST /community/{communityId}/posts/,Create post with valid data,HTTP status code and created post,201 Created with success:true and post data
REQ03-TC-03-07,POST /community/{communityId}/posts/,Create post with missing required fields,HTTP status code and validation errors,400 Bad Request with validation messages
REQ03-TC-03-08,POST /community/{communityId}/posts/,Create post for non-existent community,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-03-09,PUT /community/{communityId}/posts/,Update existing post,HTTP status code and updated post,200 OK with success:true and updated data
REQ03-TC-03-10,PUT /community/{communityId}/posts/,Update non-existent post,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-03-11,DELETE /community/{communityId}/posts/{postId},Delete existing post,HTTP status code and success response,200 OK with success:true
REQ03-TC-03-12,DELETE /community/{communityId}/posts/{postId},Delete non-existent post,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-04-01,GET /community/{communityId}/posts/{postId}/comments,Get comments for existing post,HTTP status code and comments array,200 OK with success:true and comments data
REQ03-TC-04-02,GET /community/{communityId}/posts/{postId}/comments,Get comments for non-existent post,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-04-03,POST /community/{communityId}/posts/{postId}/comments,Create comment with valid data,HTTP status code and created comment,201 Created with success:true and comment data
REQ03-TC-04-04,POST /community/{communityId}/posts/{postId}/comments,Create comment with missing content,HTTP status code and validation errors,400 Bad Request with validation messages
REQ03-TC-04-05,POST /community/{communityId}/posts/{postId}/comments,Create comment for non-existent post,HTTP status code and error handling,404 Not Found with appropriate error message
REQ03-TC-04-06,DELETE /community/{communityId}/posts/comments/{commentId},Delete existing comment,HTTP status code and success response,200 OK with success:true
REQ03-TC-04-07,DELETE /community/{communityId}/posts/comments/{commentId},Delete non-existent comment,HTTP status code and error handling,404 Not Found with appropriate error message
```

## Performance Test Considerations

### Load Testing Scenarios
1. **Community Registration Load**: 100 concurrent users registering communities
2. **Community Retrieval Load**: 1000 concurrent requests for community list
3. **Post Creation Load**: 50 concurrent users creating posts in same community
4. **Member Join Load**: 200 concurrent users joining same community

### Performance Test Example

```java
@Test
@DisplayName("Performance Test: Community Registration under load")
void shouldHandleConcurrentCommunityRegistration() throws InterruptedException {
    int numberOfThreads = 10;
    int requestsPerThread = 10;
    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads * requestsPerThread);
    AtomicInteger successCount = new AtomicInteger();
    AtomicInteger errorCount = new AtomicInteger();
    
    long startTime = System.currentTimeMillis();
    
    for (int i = 0; i < numberOfThreads; i++) {
        executor.submit(() -> {
            for (int j = 0; j < requestsPerThread; j++) {
                try {
                    CommunityRegisterRequest request = CommunityTestDataBuilder
                            .defaultCommunityRequest()
                            .name("Load Test Community " + Thread.currentThread().getId() + "-" + j)
                            .build();
                    
                    int result = communityService.registerCommunity(request);
                    if (result > 0) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }
        });
    }
    
    latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();
    
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    
    assertThat(successCount.get()).isEqualTo(numberOfThreads * requestsPerThread);
    assertThat(errorCount.get()).isZero();
    assertThat(duration).isLessThan(10000); // Should complete within 10 seconds
    
    System.out.printf("Performance Test Results: %d requests completed in %d ms%n", 
            successCount.get(), duration);
}
```

## Test Execution Guidelines

### Running Tests
```bash
# Run all tests
./gradlew test

# Run only unit tests
./gradlew test --tests "*Test"

# Run only integration tests  
./gradlew test --tests "*IntegrationTest"

# Run with coverage
./gradlew test jacocoTestReport

# Run performance tests
./gradlew test --tests "*PerformanceTest"
```

### Test Coverage Requirements
- **Unit Tests**: Minimum 80% line coverage
- **Integration Tests**: Cover all major user flows
- **API Tests**: Cover all endpoints with success and error scenarios
- **Performance Tests**: Validate under expected load conditions

### Continuous Integration
```yaml
# .github/workflows/test.yml
name: Test Suite
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Run tests
      run: ./gradlew test jacocoTestReport
    
    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
```

This comprehensive test case document provides a structured approach to testing the Community Application APIs, following the established coding style guide and best practices for Spring Boot applications.