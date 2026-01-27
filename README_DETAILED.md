# Job Portal Application - Complete Documentation

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Authentication Flow](#authentication-flow)
- [Database Schema](#database-schema)
- [Password Requirements](#password-requirements)
- [Configuration](#configuration)
- [Frontend Details](#frontend-details)
- [Backend Details](#backend-details)
- [Troubleshooting](#troubleshooting)
- [Development Guide](#development-guide)

---

## 🎯 Project Overview

Job Portal is a full-stack web application that connects **job seekers** with **recruiters** and provides **admin management** capabilities. The platform allows users to:

- **Job Seekers (Users)**: Search and apply for jobs, manage their profile and applications
- **Recruiters**: Post jobs, view applicants, manage job postings
- **Admins**: Manage users, jobs, and overall platform statistics

The application is built with a modern tech stack using Spring Boot 3 for the backend and React with Vite for the frontend.

---

## 🏗️ Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Internet / Browser                        │
└────────────┬────────────────────────────────────┬───────────┘
             │                                    │
             ▼                                    ▼
    ┌─────────────────┐              ┌──────────────────┐
    │  Frontend (80)  │              │  Backend (3000)  │
    │  ├─ React       │──HTTP/CORS──▶│  ├─ Spring Boot  │
    │  ├─ Vite        │◀─────────────│  ├─ MongoDB      │
    │  └─ Nginx       │              │  ├─ JWT Auth     │
    └─────────────────┘              │  └─ REST API     │
                                     └────────┬─────────┘
                                              │
                                              ▼
                                     ┌──────────────────┐
                                     │   MongoDB        │
                                     │   (27017)        │
                                     │  ├─ Users        │
                                     │  ├─ Jobs         │
                                     │  └─ Applications │
                                     └──────────────────┘
```

### Data Flow

1. **User Registration/Login**: Frontend sends credentials → Backend validates → JWT token generated → Token stored as HTTP-only cookie
2. **Job Management**: Frontend sends requests with JWT → Backend authenticates → Database operations → Response sent back
3. **Application Management**: Job seekers apply → Backend creates application record → Recruiters view applicants

---

## 🛠️ Technology Stack

### Backend
- **Java 21** - Programming language
- **Spring Boot 3.2.1** - Web framework
- **Spring Security** - Authentication & Authorization
- **Spring Data MongoDB** - Database ORM
- **JWT (jjwt 0.12.3)** - Token-based authentication
- **Maven** - Build tool
- **Lombok** - Code generation
- **Jakarta Bean Validation** - Input validation
- **BCrypt** - Password hashing

### Frontend
- **Node.js** - Runtime
- **React 18+** - UI framework
- **Vite** - Build tool & dev server
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **React Hook Form** - Form state management
- **SweetAlert2** - User notifications
- **Tailwind CSS** - Styling
- **Styled Components** - CSS-in-JS

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Orchestration
- **MongoDB 7.0** - NoSQL database
- **Nginx** - Reverse proxy / Web server

---

## 📁 Project Structure

```
job-portal/
│
├── job-portal-backend/              # Java/Spring Boot backend
│   ├── src/main/java/com/jobportal/
│   │   ├── controller/              # REST endpoints
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── JobController.java
│   │   │   ├── ApplicationController.java
│   │   │   └── AdminController.java
│   │   ├── service/                 # Business logic
│   │   │   ├── UserService.java
│   │   │   ├── JobService.java
│   │   │   ├── ApplicationService.java
│   │   │   ├── FileUploadService.java
│   │   │   └── AdminService.java
│   │   ├── model/                   # Data models
│   │   │   ├── User.java
│   │   │   ├── Job.java
│   │   │   ├── Application.java
│   │   │   ├── Role.java
│   │   │   ├── JobStatus.java
│   │   │   ├── JobType.java
│   │   │   └── ApplicationStatus.java
│   │   ├── repository/              # Database access
│   │   │   ├── UserRepository.java
│   │   │   ├── JobRepository.java
│   │   │   └── ApplicationRepository.java
│   │   ├── security/                # JWT & Authentication
│   │   │   ├── JwtTokenProvider.java
│   │   │   └── JwtAuthenticationFilter.java
│   │   ├── config/                  # Configuration classes
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtProperties.java
│   │   │   └── CorsProperties.java
│   │   ├── dto/                     # Data Transfer Objects
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── ApiResponse.java
│   │   │   └── ...
│   │   └── exception/               # Exception handling
│   │       └── GlobalExceptionHandler.java
│   ├── src/main/resources/
│   │   └── application.yml          # Configuration file
│   ├── Dockerfile                   # Docker build config
│   └── pom.xml                      # Maven dependencies
│
├── full-stack-job-portal-client-main/  # React frontend
│   ├── src/
│   │   ├── components/              # Reusable React components
│   │   │   ├── Logo.jsx
│   │   │   ├── Navbar.jsx
│   │   │   ├── AllJobsPage/
│   │   │   ├── MyJobsPage/
│   │   │   ├── Home Page/
│   │   │   ├── shared/              # Protected routes, layouts
│   │   │   └── index.js
│   │   ├── pages/                   # Page components
│   │   │   ├── Landing.jsx
│   │   │   ├── Register.jsx
│   │   │   ├── Login.jsx
│   │   │   ├── AllJobs.jsx
│   │   │   ├── Profile.jsx
│   │   │   ├── Admin.jsx
│   │   │   ├── ManageJobs.jsx
│   │   │   └── ...
│   │   ├── context/                 # Global state management
│   │   │   ├── UserContext.jsx
│   │   │   └── JobContext.jsx
│   │   ├── utils/                   # Utility functions
│   │   │   ├── FetchHandlers.js     # API calls
│   │   │   ├── JobData.js
│   │   │   └── DashboardNavLinkData.jsx
│   │   ├── Router/
│   │   │   └── Routes.jsx           # Route definitions
│   │   ├── Layout/                  # Layout wrappers
│   │   │   ├── HomeLayout.jsx
│   │   │   └── DashboardLayout.jsx
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── public/                      # Static assets
│   ├── Dockerfile                   # Docker build config
│   ├── nginx.conf                   # Nginx configuration
│   ├── package.json                 # NPM dependencies
│   ├── vite.config.js              # Vite configuration
│   └── .env                         # Environment variables
│
├── docker-compose.yml               # Docker Compose orchestration
├── README.md                        # Quick start guide
└── README_DETAILED.md              # This file

```

---

## ✨ Features

### 🔐 Authentication & Authorization
- User registration with email and secure password
- Login with JWT token generation
- HTTP-only cookie for token storage
- Role-based access control (User, Recruiter, Admin)
- Automatic admin creation for first user
- Protected routes and endpoints

### 👤 User Management
- Create and edit user profiles
- Upload resumes
- View personal job applications
- Update location, gender, and profile information
- Search and filter jobs

### 💼 Job Management (Recruiters)
- Post new job listings
- Edit existing job postings
- Delete job listings
- View job applicants
- Change job status (Open/Closed/On Hold)
- Filter and search job listings

### 📊 Admin Dashboard
- View all users and jobs statistics
- Manage user roles (promote to recruiter/admin)
- Remove users or jobs
- Monthly statistics and analytics
- Overall platform management

### 📋 Job Applications
- Apply for jobs (one-click apply)
- Track application status (Pending/Approved/Rejected)
- View applied jobs history
- View detailed job information

---

## 📋 Prerequisites

### System Requirements
- **OS**: Linux, macOS, or Windows (with WSL2)
- **CPU**: Dual-core or better
- **RAM**: 4GB minimum (8GB recommended)
- **Disk**: 5GB free space

### Required Software
- **Docker**: 20.10+ ([Install Docker](https://docs.docker.com/get-docker/))
- **Docker Compose**: 2.0+ ([Install Docker Compose](https://docs.docker.com/compose/install/))
- **Git**: ([Install Git](https://git-scm.com/))
- **Java 21** (optional, only for local development without Docker)
- **Node.js 18+** (optional, only for local frontend development without Docker)

### Verify Installation
```bash
docker --version        # Should be 20.10 or higher
docker-compose --version  # Should be 2.0 or higher
git --version           # Should work
```

---

## 🚀 Installation & Setup

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd job-portal
```

### Step 2: Navigate to Project Directory
```bash
cd /home/tushar/project/job-portal
```

### Step 3: Create Environment Files (Optional)

The application uses default configuration, but you can customize it:

**Frontend Environment** - `full-stack-job-portal-client-main/.env`
```env
VITE_API_BASE_URL=http://localhost:3000
```

**Backend Configuration** - `job-portal-backend/src/main/resources/application.yml`
```yaml
server:
  port: 3000
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/job-portal
jwt:
  secret: M8CcVBFZv8pGo1UFvA3ZSnq+eq7IWTJJcB/fO49H4IA=
  expiration: 86400000
cors:
  allowed-origins: http://localhost,http://localhost:3000,http://localhost:5173
```

---

## ▶️ Running the Application

### Option 1: Using Docker Compose (Recommended)

#### Build and Start All Services
```bash
# Start all services in background
docker-compose up -d --build

# Check if all containers are running
docker-compose ps

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove all data
docker-compose down -v
```

#### Access the Application
- **Frontend**: http://localhost/
- **Backend API**: http://localhost:3000/api/v1/
- **MongoDB**: mongodb://localhost:27017/job-portal

---

### Option 2: Local Development (Without Docker)

#### Start MongoDB
```bash
# Using Docker for MongoDB only
docker run -d -p 27017:27017 --name job-portal-mongo mongo:7.0

# Or using MongoDB installed locally
mongod
```

#### Start Backend
```bash
cd job-portal-backend

# Build the project
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run

# Backend will start on http://localhost:3000
```

#### Start Frontend
```bash
cd full-stack-job-portal-client-main

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend will start on http://localhost:5173
```

---

## 🔌 API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "role": "user"  // or "recruiter", "admin" (admin requires adminCode)
}

Response (201 Created):
{
  "status": true,
  "result": {
    "id": "...",
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2026-01-27T..."
  },
  "message": "User registered successfully"
}
```

#### Login User
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123!"
}

Response (200 OK):
{
  "status": true,
  "result": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login Successfully"
}

Headers:
Set-Cookie: jobPortalToken=<jwt-token>; HttpOnly; Path=/; Max-Age=86400
```

#### Get Current User
```http
GET /api/v1/auth/me
Cookie: jobPortalToken=<jwt-token>

Response (200 OK):
{
  "status": true,
  "result": {
    "id": "...",
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER",
    "location": "NYC",
    "gender": "Male",
    "resume": "/path/to/resume.pdf"
  }
}
```

### Job Endpoints

#### Get All Jobs (Public)
```http
GET /api/v1/jobs?page=0&size=10

Response (200 OK):
{
  "content": [
    {
      "id": "...",
      "title": "Senior Java Developer",
      "description": "...",
      "salary": "100000-150000",
      "location": "NYC",
      "jobType": "FULL_TIME",
      "status": "OPEN",
      "postedBy": "...",
      "createdAt": "2026-01-27T..."
    }
  ],
  "totalPages": 5,
  "totalElements": 50,
  "currentPage": 0
}
```

#### Post Job (Recruiter Only)
```http
POST /api/v1/jobs
Content-Type: application/json
Cookie: jobPortalToken=<jwt-token>

{
  "title": "Senior Java Developer",
  "description": "Looking for experienced Java developer...",
  "salary": "100000-150000",
  "location": "NYC",
  "jobType": "FULL_TIME"
}

Response (201 Created):
{
  "status": true,
  "result": { "id": "...", ... },
  "message": "Job posted successfully"
}
```

#### Edit Job
```http
PATCH /api/v1/jobs/{jobId}
Content-Type: application/json
Cookie: jobPortalToken=<jwt-token>

{
  "title": "Updated Title",
  "salary": "120000-160000"
}
```

#### Delete Job
```http
DELETE /api/v1/jobs/{jobId}
Cookie: jobPortalToken=<jwt-token>
```

### Application Endpoints

#### Apply for Job
```http
POST /api/v1/applications
Content-Type: application/json
Cookie: jobPortalToken=<jwt-token>

{
  "jobId": "..."
}

Response (201 Created):
{
  "status": true,
  "result": { "id": "...", "status": "PENDING", ... },
  "message": "Application submitted successfully"
}
```

#### Get User Applications
```http
GET /api/v1/applications
Cookie: jobPortalToken=<jwt-token>

Response (200 OK):
{
  "status": true,
  "result": [
    {
      "id": "...",
      "jobId": "...",
      "jobTitle": "Senior Java Developer",
      "status": "PENDING",
      "appliedDate": "2026-01-27T..."
    }
  ]
}
```

#### Get Job Applicants (Recruiter Only)
```http
GET /api/v1/applications/job/{jobId}
Cookie: jobPortalToken=<jwt-token>

Response (200 OK):
{
  "status": true,
  "result": [
    {
      "id": "...",
      "applicantName": "John Doe",
      "applicantEmail": "john@example.com",
      "resume": "/path/to/resume.pdf",
      "status": "PENDING",
      "appliedDate": "2026-01-27T..."
    }
  ]
}
```

#### Update Application Status (Recruiter Only)
```http
PATCH /api/v1/applications/{applicationId}/status
Content-Type: application/json
Cookie: jobPortalToken=<jwt-token>

{
  "status": "APPROVED"  // or "REJECTED"
}
```

### User Endpoints

#### Update Profile
```http
PATCH /api/v1/users
Content-Type: multipart/form-data
Cookie: jobPortalToken=<jwt-token>

Fields:
- username: "new_name"
- location: "NYC"
- gender: "Male"
- resume: <file>

Response (200 OK):
{
  "status": true,
  "result": { ... },
  "message": "Profile updated successfully"
}
```

#### Get All Users (Admin Only)
```http
GET /api/v1/users
Cookie: jobPortalToken=<jwt-token>

Response (200 OK):
{
  "status": true,
  "result": [...]
}
```

---

## 🔐 Authentication Flow

### JWT Token Flow

```
1. User Registration
   ├─ POST /api/v1/auth/register
   ├─ Backend validates input
   ├─ Hash password with BCrypt
   ├─ Save user to MongoDB
   └─ Return user object

2. User Login
   ├─ POST /api/v1/auth/login
   ├─ Backend verifies email & password
   ├─ Generate JWT token (24 hour expiry)
   ├─ Set HTTP-only cookie with token
   └─ Return token in response body

3. Authenticated Requests
   ├─ Frontend sends request with cookie
   ├─ JwtAuthenticationFilter extracts token from cookie
   ├─ JwtTokenProvider validates token signature
   ├─ Load user from database
   ├─ Set Spring Security context
   └─ Process the request

4. Token Refresh (Implicit)
   ├─ Tokens expire after 24 hours
   ├─ User must login again to get new token
   ├─ No refresh token implemented (can be added)
   └─ Frontend auto-redirects to login
```

### Token Structure

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "user-id-in-mongodb",
  "iat": 1674000000,  // Issued at
  "exp": 1674086400   // Expires in 24 hours
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

---

## 🗄️ Database Schema

### MongoDB Collections

#### Users Collection
```javascript
{
  _id: ObjectId,
  username: String,
  email: String (unique),
  password: String (hashed),
  location: String,
  gender: String,
  resume: String (file path),
  role: String (USER, RECRUITER, ADMIN),
  createdAt: Date,
  updatedAt: Date
}
```

#### Jobs Collection
```javascript
{
  _id: ObjectId,
  title: String,
  description: String,
  salary: String,
  location: String,
  jobType: String (FULL_TIME, PART_TIME, CONTRACT, FREELANCE),
  status: String (OPEN, CLOSED, ON_HOLD),
  postedBy: ObjectId (reference to User),
  totalApplications: Number,
  createdAt: Date,
  updatedAt: Date
}
```

#### Applications Collection
```javascript
{
  _id: ObjectId,
  jobId: ObjectId (reference to Job),
  applicantId: ObjectId (reference to User),
  status: String (PENDING, APPROVED, REJECTED),
  appliedAt: Date,
  updatedAt: Date
}
```

---

## 🔑 Password Requirements

Password must contain:
- ✅ Minimum 8 characters, maximum 20 characters
- ✅ At least one uppercase letter (A-Z)
- ✅ At least one lowercase letter (a-z)
- ✅ At least one number (0-9)
- ✅ At least one special character: @#$%^&*!

### Valid Examples:
- `SecurePass123!`
- `MyPass@2024`
- `Job#Portal123`
- `Test$Pass999`

### Invalid Examples:
- `password123!` (no uppercase)
- `PASSWORD123!` (no lowercase)
- `SecurePass!` (no number)
- `SecurePass123` (no special character)
- `Pass1!` (too short)

---

## ⚙️ Configuration

### Backend Configuration (application.yml)

```yaml
spring:
  application:
    name: job-portal-backend
  data:
    mongodb:
      uri: mongodb://mongo:27017/job-portal
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB

server:
  port: 3000

jwt:
  secret: M8CcVBFZv8pGo1UFvA3ZSnq+eq7IWTJJcB/fO49H4IA=
  expiration: 86400000  # 24 hours in milliseconds

cookie:
  name: jobPortalToken
  secret: job-portal-secret-cookie-key-2024

cors:
  allowed-origins: http://localhost,http://localhost:3000,http://localhost:5173

upload:
  dir: public/uploads/

logging:
  level:
    root: INFO
    com.jobportal: DEBUG
```

### Frontend Configuration (vite.config.js)

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
      }
    }
  }
})
```

### Docker Compose Environment Variables

```yaml
services:
  backend:
    environment:
      PORT: 3000
      DB_STRING: mongodb://mongo:27017/job-portal
      JWT_SECRET: M8CcVBFZv8pGo1UFvA3ZSnq+eq7IWTJJcB/fO49H4IA=
      COOKIE_SECRET: job-portal-secret-cookie-key-2024
      COOKIE_NAME: jobPortalToken
      CORS_ORIGIN: http://localhost,http://localhost:3000,http://localhost:5173
```

---

## 🎨 Frontend Details

### Components Structure

#### Layout Components
- **HomeLayout**: Landing page layout with navigation
- **DashboardLayout**: Protected user dashboard with sidebar

#### Page Components
- **Landing.jsx**: Public landing page
- **Register.jsx**: User registration form
- **Login.jsx**: User login form
- **AllJobs.jsx**: Browse all available jobs
- **Profile.jsx**: User profile and resume upload
- **EditProfile.jsx**: Update user information
- **Admin.jsx**: Admin dashboard
- **ManageJobs.jsx**: Recruiter job management
- **ManageUsers.jsx**: Admin user management
- **MyJobs.jsx**: User applied jobs
- **AddJob.jsx**: Recruiter post new job

#### Shared Components
- **Navbar.jsx**: Navigation bar
- **Sidebar.jsx**: Dashboard sidebar
- **ProtectAdminRoute.jsx**: Route guard for admin
- **RecruiterRoute.jsx**: Route guard for recruiters
- **CommonProtectRoute.jsx**: Route guard for authenticated users
- **Loading.jsx**: Loading spinner

### State Management

#### UserContext
```javascript
{
  userLoading: Boolean,
  userError: Object,
  user: User | {},
  handleFetchMe: Function
}
```

Manages:
- Current authenticated user
- Loading states
- Error states
- Fetch current user data

#### JobContext
```javascript
{
  jobs: Job[],
  singleJob: Job,
  loading: Boolean,
  error: Object,
  totalPages: Number
}
```

Manages:
- Job listings
- Single job details
- Loading and error states
- Pagination

### API Communication (FetchHandlers.js)

```javascript
// Get request
const jobs = await getAllHandler('/api/v1/jobs');

// Post request
const response = await postHandler({
  url: '/api/v1/auth/login',
  body: { email, password }
});

// Patch request
await updateHandler({
  url: '/api/v1/jobs/123',
  body: { title: 'New Title' }
});

// Delete request
await deleteHandler('/api/v1/jobs/123');
```

---

## 🔧 Backend Details

### Controllers

Each controller handles specific domain:
- **AuthController**: Registration, login, current user
- **UserController**: User profile operations
- **JobController**: Job CRUD operations
- **ApplicationController**: Job applications
- **AdminController**: Admin statistics and management

### Services

Each service implements business logic:
- **UserService**: User operations (implements UserDetailsService)
- **JobService**: Job operations and search
- **ApplicationService**: Application management
- **FileUploadService**: Resume upload handling
- **AdminService**: Statistics and analytics

### Security Implementation

```
Request Flow:
├─ HTTP Request arrives
├─ JwtAuthenticationFilter
│  ├─ Extract JWT from cookie
│  ├─ Validate JWT signature
│  ├─ Load user from database
│  └─ Set Spring Security context
├─ SecurityFilterChain
│  ├─ Check authorization rules
│  ├─ Permit public endpoints
│  └─ Require authentication for protected endpoints
└─ Controller method executed
```

### Exception Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {
  // Handles validation errors (400)
  // Handles authentication errors (401)
  // Handles authorization errors (403)
  // Handles not found errors (404)
  // Handles server errors (500)
}
```

---

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. **Containers Not Starting**
```bash
# Check Docker daemon
docker ps

# Check logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mongo

# Rebuild from scratch
docker-compose down -v
docker-compose up -d --build
```

#### 2. **Port Already in Use**
```bash
# Check which process is using the port
lsof -i :3000   # Backend
lsof -i :80     # Frontend
lsof -i :27017  # MongoDB

# Kill the process (use with caution)
kill -9 <PID>
```

#### 3. **Registration/Login Failing**

**Password doesn't meet requirements**
- Ensure password has: uppercase, lowercase, number, special character
- Password length: 8-20 characters
- Special characters: @#$%^&*!

**Email already exists**
- Use a different email address

**Database connection failed**
- Ensure MongoDB is running: `docker-compose ps`
- Check MongoDB connectivity: `docker exec job-portal-mongo mongosh`

#### 4. **JWT Token Issues**

**"Key byte array is not secure enough"**
- JWT_SECRET must be at least 256 bits (32 bytes)
- Check docker-compose.yml environment variables
- Restart backend: `docker-compose restart backend`

**"Unable to index into null"**
- Cookie might not be sent properly
- Check browser cookies
- Ensure withCredentials is set in axios calls

#### 5. **CORS Errors**

**"Access to XMLHttpRequest blocked by CORS policy"**
```
Solution:
1. Check CORS_ORIGIN in docker-compose.yml
2. Verify frontend origin matches allowed origins
3. Restart backend: docker-compose restart backend
```

#### 6. **Database Issues**

**"Connection refused"**
```bash
# Check MongoDB is running
docker-compose ps

# Restart MongoDB
docker-compose restart mongo
```

**"Duplicate key error"**
- Email already exists in database
- Check for existing user or clear database

#### 7. **Frontend Not Loading**

**"Cannot GET /"**
- Nginx not serving files properly
- Check nginx.conf
- Rebuild frontend: `docker-compose down && docker-compose up -d --build`

**Blank white page**
- Check browser console for JavaScript errors
- Check network tab for failed requests
- Clear browser cache: `Ctrl+Shift+Delete`

---

## 👨‍💻 Development Guide

### Adding a New Feature

#### 1. Backend - Add API Endpoint

```java
// 1. Create DTO (if needed)
// src/main/java/com/jobportal/dto/YourRequest.java
@Data
public class YourRequest {
  @NotBlank
  private String field;
}

// 2. Add method in Service
// src/main/java/com/jobportal/service/YourService.java
public YourResponse doSomething(YourRequest request) {
  // Business logic
}

// 3. Add endpoint in Controller
// src/main/java/com/jobportal/controller/YourController.java
@PostMapping("/your-endpoint")
public ResponseEntity<ApiResponse<YourResponse>> create(
  @Valid @RequestBody YourRequest request) {
  try {
    YourResponse result = service.doSomething(request);
    return ResponseEntity.ok(ApiResponse.<YourResponse>builder()
      .status(true)
      .result(result)
      .message("Success")
      .build());
  } catch (Exception e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(...);
  }
}
```

#### 2. Frontend - Add Component and Route

```javascript
// 1. Create component
// src/pages/YourPage.jsx
export default function YourPage() {
  const [data, setData] = useState([]);

  useEffect(() => {
    fetchData();
  }, []);

  return <div>Your component</div>;
}

// 2. Add route
// src/Router/Routes.jsx
{
  path: "your-route",
  element: <CommonProtectRoute><YourPage /></CommonProtectRoute>
}
```

#### 3. Test the Feature

```bash
# Test backend
curl -X POST http://localhost:3000/api/v1/your-endpoint \
  -H "Content-Type: application/json" \
  -d '{"field": "value"}'

# Test frontend - navigate to route and check browser console
```

### Building for Production

```bash
# Build frontend
cd full-stack-job-portal-client-main
npm run build

# Build backend
cd ../job-portal-backend
mvn clean package

# Create production Docker images
docker-compose -f docker-compose.yml build

# Deploy with Docker Compose
docker-compose -f docker-compose.yml up -d
```

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/your-feature

# Make changes and commit
git add .
git commit -m "Add: your feature description"

# Push to remote
git push origin feature/your-feature

# Create pull request on GitHub
```

---

## 📞 Support & Contact

For issues, questions, or suggestions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review the [API Endpoints](#api-endpoints) documentation
3. Check application logs: `docker-compose logs -f`
4. Create an issue on GitHub

---

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- React team for UI library
- MongoDB for database
- All contributors and testers

---

**Last Updated**: January 27, 2026
**Version**: 1.0.0
