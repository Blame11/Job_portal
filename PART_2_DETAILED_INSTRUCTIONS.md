# Part 2: Complete User, Job & Application Services

## Quick Reference: What to Create

### User Service - 3 Files Needed

```
user-service/src/main/java/com/jobportal/userservice/
├── service/
│   ├── FileUploadService.java      [NEW]
│   ├── UserService.java            [NEW]
│   └── AdminService.java           [NEW]
├── controller/
│   └── UserController.java         [NEW]
└── config/
    └── GlobalExceptionHandler.java [NEW]
```

### Job Service - Complete Stack

```
job-service/src/main/java/com/jobportal/jobservice/
├── JobServiceApplication.java      [NEW]
├── model/Job.java                  [NEW]
├── repository/JobRepository.java    [NEW]
├── service/JobService.java          [NEW]
├── controller/JobController.java    [NEW]
├── dto/
│   ├── ApiResponse.java            [NEW]
│   ├── CreateJobRequest.java        [NEW]
│   ├── JobResponse.java             [NEW]
│   ├── JobCountDTO.java             [NEW]
│   └── SearchJobRequest.java        [NEW]
├── enums/
│   ├── JobStatus.java              [NEW]
│   ├── JobType.java                [NEW]
│   └── Role.java                   [NEW]
└── config/GlobalExceptionHandler.java [NEW]

job-service/
├── pom.xml                         [NEW]
├── Dockerfile                      [NEW]
└── src/main/resources/application.yml [NEW]
```

### Application Service - Complete Stack

```
application-service/src/main/java/com/jobportal/applicationservice/
├── ApplicationServiceApplication.java [NEW]
├── model/Application.java          [NEW]
├── repository/ApplicationRepository.java [NEW]
├── service/
│   ├── ApplicationService.java      [NEW]
│   └── FileUploadService.java      [NEW]
├── controller/ApplicationController.java [NEW]
├── dto/
│   ├── ApiResponse.java            [NEW]
│   ├── ApplyJobRequest.java        [NEW]
│   ├── ApplicationResponse.java     [NEW]
│   ├── ApplicationCountDTO.java     [NEW]
│   └── JobDTO.java                 [NEW]
├── enums/
│   ├── ApplicationStatus.java       [NEW]
│   └── Role.java                   [NEW]
└── config/GlobalExceptionHandler.java [NEW]

application-service/
├── pom.xml                         [NEW]
├── Dockerfile                      [NEW]
└── src/main/resources/application.yml [NEW]
```

---

## Step-by-Step Execution

### Step 1: Complete User Service Service Layer

**File**: `microservices/user-service/src/main/java/com/jobportal/userservice/service/FileUploadService.java`

```java
package com.jobportal.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${upload.dir:public/uploads/}")
    private String uploadDir;

    private static final String[] ALLOWED_EXTENSIONS = {"pdf", "doc", "docx"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String uploadResume(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);
        
        String fileName = generateFileName(getFileExtension(file.getOriginalFilename()));
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
        
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());
        
        return "/uploads/" + fileName;
    }

    public void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        boolean isAllowed = false;
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowed)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("Only PDF, DOC, DOCX files allowed");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String generateFileName(String extension) {
        return "resume-" + UUID.randomUUID() + "." + extension;
    }
}
```

**File**: `microservices/user-service/src/main/java/com/jobportal/userservice/service/UserService.java`

```java
package com.jobportal.userservice.service;

import com.jobportal.userservice.dto.UpdateProfileRequest;
import com.jobportal.userservice.dto.UserResponse;
import com.jobportal.userservice.model.User;
import com.jobportal.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final RestTemplate restTemplate;

    @Value("${service.urls.job:http://localhost:3003}")
    private String jobServiceUrl;

    public UserResponse updateProfile(String userId, UpdateProfileRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }

        if (request.getLocation() != null && !request.getLocation().isEmpty()) {
            user.setLocation(request.getLocation());
        }

        if (request.getGender() != null && !request.getGender().isEmpty()) {
            user.setGender(request.getGender());
        }

        if (request.getResume() != null && !request.getResume().isEmpty()) {
            String resumePath = fileUploadService.uploadResume(request.getResume());
            user.setResume(resumePath);
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Call job-service to delete all jobs by this user
        try {
            String url = jobServiceUrl + "/internal/jobs/user/" + userId;
            restTemplate.delete(url);
            log.info("Deleted jobs for user: {}", userId);
        } catch (Exception e) {
            log.error("Error deleting jobs for user {}: {}", userId, e.getMessage());
        }

        userRepository.deleteById(userId);
    }

    public UserResponse updateUserRole(String userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(role.toUpperCase());
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public long getUsersByRole(String role) {
        return userRepository.countByRole(role);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setLocation(user.getLocation());
        response.setGender(user.getGender());
        response.setResume(user.getResume());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        response.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        return response;
    }
}
```

**File**: `microservices/user-service/src/main/java/com/jobportal/userservice/service/AdminService.java`

```java
package com.jobportal.userservice.service;

import com.jobportal.userservice.dto.AdminStatsResponse;
import com.jobportal.userservice.dto.MonthlyStatsDTO;
import com.jobportal.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${service.urls.job:http://localhost:3003}")
    private String jobServiceUrl;

    @Value("${service.urls.application:http://localhost:3004}")
    private String applicationServiceUrl;

    public AdminStatsResponse getSystemStats() {
        AdminStatsResponse stats = new AdminStatsResponse();

        // User counts
        stats.setTotalUsers(userRepository.count());
        stats.setTotalAdmins(userRepository.countByRole("ADMIN"));
        stats.setTotalRecruiters(userRepository.countByRole("RECRUITER"));
        stats.setTotalApplicants(userRepository.countByRole("USER"));

        // Job counts (from job-service)
        try {
            String jobUrl = jobServiceUrl + "/internal/stats";
            Object jobStats = restTemplate.getForObject(jobUrl, Object.class);
            log.info("Retrieved job stats: {}", jobStats);
            // Parse and set job counts if needed
        } catch (Exception e) {
            log.error("Error fetching job stats: {}", e.getMessage());
        }

        // Application counts (from application-service)
        try {
            String appUrl = applicationServiceUrl + "/internal/counts";
            Object appStats = restTemplate.getForObject(appUrl, Object.class);
            log.info("Retrieved application stats: {}", appStats);
            // Parse and set application counts if needed
        } catch (Exception e) {
            log.error("Error fetching application stats: {}", e.getMessage());
        }

        return stats;
    }

    public List<MonthlyStatsDTO> getMonthlyStats() {
        try {
            String url = jobServiceUrl + "/internal/monthly-stats";
            List<MonthlyStatsDTO> stats = restTemplate.getForObject(url, List.class);
            return stats;
        } catch (Exception e) {
            log.error("Error fetching monthly stats: {}", e.getMessage());
            return List.of();
        }
    }
}
```

**File**: `microservices/user-service/src/main/java/com/jobportal/userservice/controller/UserController.java`

```java
package com.jobportal.userservice.controller;

import com.jobportal.userservice.dto.ApiResponse;
import com.jobportal.userservice.dto.AdminStatsResponse;
import com.jobportal.userservice.dto.MonthlyStatsDTO;
import com.jobportal.userservice.dto.UpdateProfileRequest;
import com.jobportal.userservice.dto.UpdateRoleRequest;
import com.jobportal.userservice.dto.UserResponse;
import com.jobportal.userservice.service.AdminService;
import com.jobportal.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdminService adminService;

    @PatchMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestHeader("X-USER-ID") String userId,
            @ModelAttribute UpdateProfileRequest request) {
        try {
            UserResponse response = userService.updateProfile(userId, request);
            return ResponseEntity.ok(new ApiResponse<>(true, response, "Profile updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            log.error("Update profile error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to update profile"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestHeader("X-USER-ROLE") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, null, "Only admins can access this resource"));
        }

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, users, "Users retrieved successfully"));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable String id,
            @RequestHeader("X-USER-ROLE") String userRole,
            @RequestBody UpdateRoleRequest request) {
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, null, "Only admins can update user roles"));
        }

        try {
            UserResponse response = userService.updateUserRole(id, request.getRole());
            return ResponseEntity.ok(new ApiResponse<>(true, response, "Role updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            log.error("Update role error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to update role"));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String id,
            @RequestHeader("X-USER-ROLE") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, null, "Only admins can delete users"));
        }

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse<>(true, null, "User deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            log.error("Delete user error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "Failed to delete user"));
        }
    }

    @GetMapping("/admin/info")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getAdminStats(
            @RequestHeader("X-USER-ROLE") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, null, "Only admins can access this resource"));
        }

        AdminStatsResponse stats = adminService.getSystemStats();
        return ResponseEntity.ok(new ApiResponse<>(true, stats, "Admin stats retrieved"));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<ApiResponse<List<MonthlyStatsDTO>>> getMonthlyStats(
            @RequestHeader("X-USER-ROLE") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, null, "Only admins can access this resource"));
        }

        List<MonthlyStatsDTO> stats = adminService.getMonthlyStats();
        return ResponseEntity.ok(new ApiResponse<>(true, stats, "Monthly stats retrieved"));
    }

    @GetMapping("/internal/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body("{\"status\": \"UP\"}");
    }
}
```

**File**: `microservices/user-service/src/main/java/com/jobportal/userservice/config/GlobalExceptionHandler.java`

```java
package com.jobportal.userservice.config;

import com.jobportal.userservice.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, "An unexpected error occurred"));
    }
}
```

---

### Step 2: Create Job Service (Complete)

**To save space, following the same pattern as above, create:**

1. **pom.xml** - Same as user-service
2. **Dockerfile** - Same pattern
3. **application.yml** - Same pattern with MONGODB_URI for job-db
4. **JobServiceApplication.java** - Main class with RestTemplate bean
5. **Job.java** - Entity with fields: title, description, salary, location, jobType, status, createdBy, createdAt, updatedAt
6. **JobRepository.java** - Methods: findByCreatedBy, findAll(Pageable), countByStatus, custom search
7. **JobService.java** - CRUD methods + search + stats methods
8. **JobController.java** - All endpoints
9. **DTOs**: ApiResponse, CreateJobRequest, JobResponse, JobCountDTO
10. **Enums**: JobStatus, JobType, Role
11. **GlobalExceptionHandler.java** - Error handling

---

### Step 3: Create Application Service (Complete)

**Same approach as Job Service with:**

1. **Application.java** - Entity: jobId, applicantId, recruiterId, status, resumePath
2. **ApplicationService.java** - apply() calls job-service to validate job
3. **FileUploadService.java** - Same as user-service
4. **ApplicationController.java** - CRUD + download-resume endpoint
5. **DTOs**: ApiResponse, ApplyJobRequest, ApplicationResponse, ApplicationCountDTO
6. **Enums**: ApplicationStatus, Role
7. **GlobalExceptionHandler.java**

---

## Testing the Full Stack

```bash
# Terminal 1: Start all services
cd e:\Projects\new\Job_portal
docker-compose -f docker-compose-microservices.yml up -d --build

# Terminal 2: Watch logs
docker-compose -f docker-compose-microservices.yml logs -f

# Terminal 3: Run tests
# Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"Test123!@","confirmPassword":"Test123!@","role":"recruiter"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!@"}' \
  -c cookies.txt

# Post job (with auth)
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"title":"Java Dev","description":"...","salary":"100k","location":"NYC","jobType":"FULL_TIME"}'

# Search jobs
curl http://localhost:8080/api/v1/jobs?page=0&size=10
```

---

## Estimated Completion Time

- User Service: 1-2 hours
- Job Service: 2-3 hours  
- Application Service: 2-3 hours
- **Total Part 2**: 5-8 hours

**Start Date**: January 28, 2026
**Target Completion**: Within 1-2 days
