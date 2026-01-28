# Job Portal Microservices Implementation - COMPLETE GUIDE

## ✅ COMPLETED (Part 1)

### 1. Infrastructure Files
- ✅ `.env` - Environment variables for all services
- ✅ `init-mongo.js` - MongoDB initialization with 4 databases
- ✅ `docker-compose-microservices.yml` - Full orchestration

### 2. API Gateway (Spring Cloud Gateway)
- ✅ `pom.xml` - Dependencies
- ✅ `Dockerfile` - Multistage build
- ✅ `application.yml` - Gateway routes configuration
- ✅ `ApiGatewayApplication.java` - Main class
- ✅ `JwtValidationFilter.java` - JWT validation & header extraction
- ✅ `JwtValidator.java` - Token validation logic
- ✅ `GatewayConstants.java` - Constants
- ✅ `HealthController.java` - Health endpoint

### 3. Auth Service (Complete)
- ✅ `pom.xml` - Dependencies
- ✅ `Dockerfile` - Build configuration
- ✅ `application.yml` - Service configuration
- ✅ `AuthServiceApplication.java` - Main class
- ✅ `User.java` - Entity
- ✅ `UserRepository.java` - MongoDB repository
- ✅ `JwtTokenProvider.java` - JWT generation & validation
- ✅ `AuthService.java` - Business logic (register, login, getCurrentUser)
- ✅ `AuthController.java` - REST endpoints (/api/v1/auth/*)
- ✅ `ApiResponse.java` - Response wrapper
- ✅ `RegisterRequest.java` - DTO with validation
- ✅ `LoginRequest.java` - DTO
- ✅ `UserDTO.java` - Response DTO
- ✅ `SecurityConfig.java` - Password encoder & CORS

### 4. User Service (Partial)
- ✅ `pom.xml` - Dependencies
- ✅ `Dockerfile` - Build configuration
- ✅ `application.yml` - Service configuration
- ✅ `UserServiceApplication.java` - Main class with RestTemplate bean
- ✅ `User.java` - Entity
- ✅ `UserRepository.java` - MongoDB repository
- ✅ DTOs: `ApiResponse`, `UpdateProfileRequest`, `UserResponse`, `UpdateRoleRequest`, `AdminStatsResponse`, `MonthlyStatsDTO`

---

## 📝 REMAINING TASKS (Part 2 - Complete These)

### User Service - Service & Controller Layers

#### 1. FileUploadService.java
```java
// Location: microservices/user-service/src/main/java/com/jobportal/userservice/service/FileUploadService.java

// Methods needed:
// - uploadResume(MultipartFile file): String → saves file, returns path
// - validateFile(MultipartFile file): void → checks type & size
// - getFileExtension(String filename): String
// - generateFileName(): String → uses UUID

// File storage: ${UPLOAD_DIR}/resume-{uuid}.{ext}
// Allowed types: PDF, DOC, DOCX
// Max size: 5MB
```

#### 2. UserService.java
```java
// Location: microservices/user-service/src/main/java/com/jobportal/userservice/service/UserService.java

// Methods needed:
// - updateProfile(String userId, UpdateProfileRequest): UserResponse
// - getUserById(String userId): UserResponse
// - getAllUsers(): List<UserResponse>
// - deleteUser(String userId): void → calls job-service to delete jobs
// - updateUserRole(String userId, String role): UserResponse (ADMIN only)
// - getUsersByRole(String role): long (for counting)

// RestTemplate calls:
// - DELETE http://job-service:3003/internal/jobs/user/{userId}
```

#### 3. AdminService.java
```java
// Location: microservices/user-service/src/main/java/com/jobportal/userservice/service/AdminService.java

// Methods needed:
// - getSystemStats(): AdminStatsResponse
//   - Call job-service /internal/stats
//   - Call application-service /internal/counts
//   - Count users by role locally
// - getMonthlyStats(): List<MonthlyStatsDTO>
//   - Call job-service /internal/monthly-stats
```

#### 4. UserController.java
```java
// Location: microservices/user-service/src/main/java/com/jobportal/userservice/controller/UserController.java

// Endpoints:
// PATCH /api/v1/users → updateProfile(X-USER-ID header)
// GET /api/v1/users → getAllUsers (ADMIN only)
// PATCH /api/v1/users/{id}/role → updateUserRole (ADMIN only)
// DELETE /api/v1/users/{id} → deleteUser (ADMIN only)
// GET /api/v1/admin/info → getSystemStats (ADMIN only)
// GET /api/v1/admin/stats → getMonthlyStats (ADMIN only)
// GET /internal/users/{id} → getUserById (for inter-service calls)
// GET /health → health check
```

#### 5. GlobalExceptionHandler.java
```java
// Location: microservices/user-service/src/main/java/com/jobportal/userservice/config/GlobalExceptionHandler.java

// Handle:
// - IllegalArgumentException → 400
// - FileUploadException → 400
// - EntityNotFoundException → 404
// - AccessDeniedException → 403
// - General exceptions → 500
```

---

### Job Service (Similar to User Service)

#### 1. Models & DTOs
- `Job.java` entity: title, description, salary, location, jobType, status, createdBy, createdAt, updatedAt
- `JobRepository.java`: findByCreatedBy(), findAll(Pageable), countByStatus(), search query
- DTOs: `ApiResponse`, `CreateJobRequest`, `UpdateJobRequest`, `JobResponse`, `JobCountDTO`
- Enums: `Role`, `JobStatus`, `JobType`

#### 2. Service Layer
- `JobService.java`: 
  - createJob(userId, CreateJobRequest): JobResponse
  - updateJob(jobId, userId, UpdateJobRequest): JobResponse (verify owner)
  - deleteJob(jobId, userId): void (verify owner)
  - getJob(jobId): JobResponse
  - searchJobs(search, page, size): Page<JobResponse>
  - getJobsByRecruiter(userId): List<JobResponse>
  - deleteJobsByUserId(userId): void (called by user-service)

#### 3. Controller
- `JobController.java`:
  - GET /api/v1/jobs?page=0&size=10&search=
  - POST /api/v1/jobs (RECRUITER only)
  - GET /api/v1/jobs/{id}
  - PATCH /api/v1/jobs/{id} (owner only)
  - DELETE /api/v1/jobs/{id} (owner only)
  - GET /internal/jobs/{id} (for application-service)
  - DELETE /internal/jobs/user/{userId} (for user-service)
  - GET /internal/stats (for user-service admin)
  - GET /internal/monthly-stats (for user-service admin)
  - GET /health

---

### Application Service

#### 1. Models & DTOs
- `Application.java` entity: jobId, applicantId, recruiterId, status, resumePath, createdAt, updatedAt
- `ApplicationRepository.java`: 
  - findByApplicantId()
  - findByRecruiterId(Pageable)
  - findByJobIdAndApplicantId() (unique constraint)
- DTOs: `ApiResponse`, `ApplyJobRequest`, `ApplicationResponse`, `ApplicationCountDTO`
- Enum: `ApplicationStatus` (PENDING, ACCEPTED, REJECTED)

#### 2. Service Layer
- `ApplicationService.java`:
  - applyForJob(userId, jobId, resume): ApplicationResponse
    - Validate job exists (call job-service)
    - Check duplicate (jobId + userId)
    - Upload resume via FileUploadService
    - Save application
  - getApplicantApplications(userId): List<ApplicationResponse>
  - getRecruiterApplications(recruiterId, page): Page<ApplicationResponse>
  - updateApplicationStatus(appId, status): ApplicationResponse (RECRUITER only)
  - getApplicationCounts(): CountsDTO (for admin-service)

#### 3. FileUploadService (same as user-service)

#### 4. Controller
- `ApplicationController.java`:
  - POST /api/v1/applications/apply (multipart: file + jobId)
  - GET /api/v1/applications
  - GET /api/v1/applications/recruiter
  - PATCH /api/v1/applications/{id}
  - GET /api/v1/applications/{id}/download-resume
  - GET /internal/counts (for user-service)
  - GET /health

---

## 🚀 Quick Start After Completing Part 2

```bash
# Build all services
cd microservices
mvn clean install -DskipTests

# Start infrastructure
docker-compose -f ../docker-compose-microservices.yml up -d --build

# Verify all containers
docker-compose -f ../docker-compose-microservices.yml ps

# Check logs
docker-compose -f ../docker-compose-microservices.yml logs -f api-gateway

# Test endpoints
curl http://localhost:8080/health

# Register user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123!",
    "confirmPassword": "TestPass123!",
    "role": "user"
  }'
```

---

## 📦 File Structure After Completion

```
microservices/
├── api-gateway/          [✅ DONE]
├── auth-service/         [✅ DONE]
├── user-service/         [🟡 PARTIAL - Need Service & Controller]
├── job-service/          [⬜ NOT STARTED]
├── application-service/  [⬜ NOT STARTED]
├── init-mongo.js         [✅ DONE]
├── docker-compose-microservices.yml  [✅ DONE]
└── .env                  [✅ DONE]
```

---

## Key Implementation Notes

1. **Intentional Duplication**: Each service defines own DTOs, enums, constants. No shared library.

2. **JWT Secret**: Same `JWT_SECRET` in `.env` used by auth-service (generation) and api-gateway (validation).

3. **Internal Service Communication**:
   - application-service → job-service: `http://job-service:3003/internal/jobs/{id}`
   - user-service → job-service: `http://job-service:3003/internal/jobs/user/{userId}`
   - user-service → application-service: `http://application-service:3004/internal/counts`

4. **MongoDB Databases**: 
   - auth-db (Users for login)
   - user-db (Users for profile management)
   - job-db (Jobs)
   - application-db (Applications with resumes)

5. **File Upload**:
   - Both user-service and application-service need FileUploadService
   - Storage: `${UPLOAD_DIR}/resume-{uuid}.{ext}`
   - Validation: PDF, DOC, DOCX only, max 5MB

6. **Frontend Unchanged**: API contracts preserved from monolith. No frontend code changes needed.

---

**Status**: Part 1 (Infrastructure + API Gateway + Auth Service) ✅ COMPLETE
**Next**: Part 2 (User Service Service/Controller + Job Service + Application Service) - Ready for implementation
