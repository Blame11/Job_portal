# Full Stack Job Portal Application

A comprehensive job portal application built with **Java Spring Boot 3** backend, **React** frontend, and **MongoDB** database. Features complete user authentication, job management, job applications, and admin dashboard.

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)
4. [Quick Start](#quick-start)
5. [Installation & Setup](#installation--setup)
6. [Features](#features)
7. [API Documentation](#api-documentation)
8. [Authentication Flow](#authentication-flow)
9. [Database Schema](#database-schema)
10. [Configuration](#configuration)
11. [Troubleshooting](#troubleshooting)
12. [Development Guide](#development-guide)

---

## Project Overview

The Job Portal is a full-featured platform for:
- **Job Seekers**: Register, search jobs, apply for positions, manage applications, update profiles
- **Recruiters**: Post job listings, manage job postings, review applications
- **Administrators**: Manage users, view statistics, moderate content

### Key Features
✅ User Authentication with JWT tokens  
✅ Role-based access control (User, Recruiter, Admin)  
✅ Job posting and management  
✅ Job application tracking  
✅ User profile management  
✅ Admin dashboard with statistics  
✅ Responsive design with Tailwind CSS  
✅ Docker containerization with docker-compose  

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Browser                        │
│                    (React + Tailwind CSS)                    │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTP/HTTPS
                             ↓
┌─────────────────────────────────────────────────────────────┐
│              Nginx Web Server (Port 80/443)                  │
│         - Reverse proxy for frontend and backend             │
│         - Serves static React application                    │
└────────────────────────────┬────────────────────────────────┘
                             │
            ┌────────────────┴────────────────┐
            │                                 │
            ↓                                 ↓
    ┌──────────────────┐           ┌──────────────────────┐
    │ React Frontend   │           │ Spring Boot Backend  │
    │ (Port 5173 Dev)  │           │ (Port 3000)          │
    │                  │           │                      │
    │ - Login/Register │           │ - JWT Authentication │
    │ - Job Listing    │           │ - Validation         │
    │ - Applications   │           │ - Business Logic     │
    │ - Dashboard      │           │ - Database Interface │
    └──────────────────┘           └──────┬───────────────┘
                                          │ Driver
                                          ↓
                                  ┌──────────────────┐
                                  │    MongoDB       │
                                  │  (Port 27017)    │
                                  │                  │
                                  │ - Users          │
                                  │ - Jobs           │
                                  │ - Applications   │
                                  └──────────────────┘
```

### Component Structure

**Backend (Java Spring Boot)**
```
src/
├── Controller/        # REST API endpoints
│   ├── AuthController (register, login, profile)
│   ├── JobController (CRUD for jobs)
│   ├── ApplicationController (applications management)
│   ├── UserController (user management)
│   └── AdminController (admin operations)
├── Model/            # Data models/entities
│   ├── User (User entity with fields)
│   ├── Job (Job entity)
│   └── Application (Job application entity)
├── Middleware/       # Authentication & Authorization
│   ├── JwtAuthenticationFilter
│   └── AuthorizationMiddleware
├── Validation/       # Request data validation
│   ├── RegisterRequest validation
│   ├── JobRequest validation
│   └── Custom validators
└── Utils/            # Helper classes
    ├── JwtTokenProvider
    ├── Constants
    └── Database connections
```

**Frontend (React)**
```
src/
├── components/       # Reusable React components
├── pages/           # Page components
│   ├── Login (Authentication page)
│   ├── Register (User registration)
│   ├── Dashboard (Main dashboard)
│   ├── AllJobs (Job listing)
│   ├── MyJobs (User's posted jobs)
│   └── Admin (Admin panel)
├── context/         # Global state
│   ├── UserContext (Authenticated user)
│   └── JobContext (Jobs data)
├── Layout/          # Layout wrappers
├── Router/          # Route definitions
└── utils/           # Helper functions
```

---

## Technology Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 3.2.1 | Web framework |
| Spring Security | 3.2.1 | Authentication/Authorization |
| MongoDB | 7.0 | NoSQL Database |
| JWT (jjwt) | 0.12.3 | Token-based authentication |
| BCrypt | - | Password hashing |
| Maven | 3.9+ | Build tool |
| Docker | - | Containerization |

### Frontend
| Technology | Version | Purpose |
|-----------|---------|---------|
| React | 18+ | UI framework |
| Vite | 4+ | Build tool |
| Tailwind CSS | 3+ | Styling |
| Axios | 1+ | HTTP client |
| React Hook Form | - | Form management |
| SweetAlert2 | - | User notifications |
| React Router | 6+ | Client-side routing |

### Infrastructure
| Tool | Purpose |
|------|---------|
| Docker | Container runtime |
| Docker Compose | Orchestration |
| Nginx | Web server/Reverse proxy |
| MongoDB | Data persistence |

---

## Quick Start

### 5-Minute Setup with Docker

```bash
# Navigate to project root
cd /home/tushar/project/job-portal

# Start all services
docker-compose up -d --build

# Verify services are running
docker-compose ps
```

**Access the application:**
- Frontend: http://localhost
- Backend API: http://localhost:3000
- MongoDB: localhost:27017

**Test the API:**
```bash
# Get all jobs (public endpoint)
curl http://localhost:3000/api/v1/jobs

# Expected response
{
  "status": true,
  "result": [],
  "totalJobs": 0
}
```

### Test User Credentials

After startup, use these test credentials:

**Admin Account:**
```
Email: admin@gmail.com
Password: AdminTest123@
```

**Recruiter Account:**
```
Email: recruiter@example.com
Password: RecruiterTest123@
```

**User Account:**
```
Email: user@example.com
Password: UserTest123@
```

> **Note:** If accounts don't exist, register new ones with the same passwords and select the appropriate role during registration.

---

## Installation & Setup

### Prerequisites

- Docker & Docker Compose 1.29+
- Java 21 (for local backend development)
- Node.js 16+ (for local frontend development)
- MongoDB 7.0+ (if running locally without Docker)

### Option 1: Using Docker Compose (Recommended)

```bash
# Clone or navigate to project
cd /home/tushar/project/job-portal

# Start all services with build
docker-compose up -d --build

# View logs
docker-compose logs -f backend    # Backend logs
docker-compose logs -f frontend   # Frontend logs
docker-compose logs -f db         # Database logs

# Stop services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

### Option 2: Local Backend Setup (Java + MongoDB)

**Prerequisites:**
- Java 21 JDK installed
- MongoDB running locally on port 27017

```bash
# Navigate to backend directory
cd full-stack-job-portal-server-main

# Install dependencies
mvn clean install

# Configure database (create src/main/resources/application.yml)
# See Configuration section for details

# Run Spring Boot application
mvn spring-boot:run

# Application starts on http://localhost:3000
```

### Option 3: Local Frontend Setup (React)

**Prerequisites:**
- Node.js 16+ installed
- Backend running on http://localhost:3000

```bash
# Navigate to frontend directory
cd full-stack-job-portal-client-main

# Install dependencies
npm install

# Start development server
npm run dev

# Application runs on http://localhost:5173
```

---

## Features

### 🔐 Authentication & Authorization

#### User Registration
- Email validation
- Strong password requirements (8-20 chars, uppercase, lowercase, digit, special char)
- Three user roles: User, Recruiter, Admin
- Admin registration requires admin code
- Email verification (optional enhancement)

#### User Login
- Email and password authentication
- JWT token generation (24-hour expiry)
- HTTP-only cookie storage
- Session management

#### Password Requirements
```
✓ Length: 8-20 characters
✓ Uppercase letter: At least one (A-Z)
✓ Lowercase letter: At least one (a-z)
✓ Digit: At least one (0-9)
✓ Special character: @#$%^&*! (at least one)

Valid example: TestPass123@
Invalid examples:
  - testpass123@    (no uppercase)
  - TESTPASS123@    (no lowercase)
  - TestPass@       (no digit)
  - TestPass123     (no special char)
  - Pass1@          (less than 8 chars)
```

### 💼 Job Management

#### For Recruiters
- Create job listings
- Edit published jobs
- Delete job postings
- View applications for each job
- Filter applications by status

#### For Job Seekers
- Browse all jobs with filters
- Apply for jobs
- Track application status
- View job details
- Search by job title, location, company

#### For Admins
- View all jobs across system
- Manage job categories
- Monitor job quality
- Take down inappropriate listings

### 📋 Application Management

- Track job applications
- View application status (Applied, Reviewing, Rejected, Accepted)
- Application timeline
- Applicant details and resume links
- Accept/Reject functionality for recruiters

### 👤 User Profile Management

- Update personal information
- Change password
- Upload profile picture
- Update resume
- Manage skills and experience

### 📊 Admin Dashboard

- User statistics (total users, by role)
- Job statistics (total jobs, active, archived)
- Application statistics
- User management interface
- Job management interface
- System monitoring

---

## API Documentation

### Base URL
```
Production: http://localhost:3000
Local Dev: http://localhost:3000
```

### Authentication
All protected endpoints require JWT token in cookie or Authorization header:
```
Authorization: Bearer <JWT_TOKEN>
```

### Response Format
```json
{
  "status": true,
  "message": "Success message",
  "result": {},
  "statusCode": 200
}
```

### Error Response
```json
{
  "status": false,
  "message": "Error description",
  "statusCode": 400
}
```

### Authentication Endpoints

#### Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "TestPass123@",
  "confirmPassword": "TestPass123@",
  "role": "USER",
  "adminCode": null
}

Response (201):
{
  "status": true,
  "message": "User registered successfully",
  "result": {
    "id": "507f1f77bcf86cd799439011",
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

#### Login User
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "TestPass123@"
}

Response (200):
{
  "status": true,
  "message": "Login successful",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "507f1f77bcf86cd799439011",
      "username": "johndoe",
      "email": "john@example.com",
      "role": "USER"
    }
  }
}

Cookies Set:
- jobPortalToken: <JWT_TOKEN>
  Path: /
  HttpOnly: true
  SameSite: Lax
  Max-Age: 86400 (24 hours)
```

#### Get Current User
```http
GET /api/v1/auth/me
Authorization: Bearer <TOKEN>
Cookie: jobPortalToken=<TOKEN>

Response (200):
{
  "status": true,
  "result": {
    "id": "507f1f77bcf86cd799439011",
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

#### Logout User
```http
POST /api/v1/auth/logout
Response (200): Clears jobPortalToken cookie
```

### Job Endpoints

#### Get All Jobs
```http
GET /api/v1/jobs?page=1&limit=10
Optional Query Parameters:
  - page: Page number (default: 1)
  - limit: Items per page (default: 10)
  - search: Search in title/description
  - category: Filter by job category
  - location: Filter by location

Response (200):
{
  "status": true,
  "result": [
    {
      "id": "507f1f77bcf86cd799439012",
      "title": "Senior Developer",
      "company": "Tech Corp",
      "description": "Looking for experienced developers",
      "location": "New York, NY",
      "salary": {
        "min": 100000,
        "max": 150000,
        "currency": "USD"
      },
      "jobType": "Full-time",
      "category": "Technology",
      "postedBy": "507f1f77bcf86cd799439010",
      "postedAt": "2024-01-15T10:30:00Z",
      "applicationsCount": 5
    }
  ],
  "totalJobs": 25,
  "currentPage": 1,
  "pageCount": 3
}
```

#### Create Job (Recruiter Only)
```http
POST /api/v1/jobs
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "title": "Senior Developer",
  "company": "Tech Corp",
  "description": "Looking for experienced developers",
  "location": "New York, NY",
  "salary": {
    "min": 100000,
    "max": 150000,
    "currency": "USD"
  },
  "jobType": "Full-time",
  "category": "Technology",
  "requirements": ["Java", "Spring Boot", "MongoDB"],
  "applicationDeadline": "2024-02-15"
}

Response (201):
{
  "status": true,
  "message": "Job created successfully",
  "result": { /* created job object */ }
}
```

#### Get Job by ID
```http
GET /api/v1/jobs/:jobId

Response (200):
{
  "status": true,
  "result": { /* job object with applications array */ }
}
```

#### Update Job (Creator Only)
```http
PUT /api/v1/jobs/:jobId
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "title": "Senior Developer (Updated)",
  "description": "Updated description"
}

Response (200):
{
  "status": true,
  "message": "Job updated successfully"
}
```

#### Delete Job (Creator/Admin Only)
```http
DELETE /api/v1/jobs/:jobId
Authorization: Bearer <TOKEN>

Response (200):
{
  "status": true,
  "message": "Job deleted successfully"
}
```

### Application Endpoints

#### Create Job Application
```http
POST /api/v1/applications
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "jobId": "507f1f77bcf86cd799439012",
  "coverLetter": "I am interested in this position..."
}

Response (201):
{
  "status": true,
  "message": "Application submitted successfully",
  "result": {
    "id": "507f1f77bcf86cd799439013",
    "jobId": "507f1f77bcf86cd799439012",
    "applicantId": "507f1f77bcf86cd799439011",
    "status": "Applied",
    "appliedAt": "2024-01-20T15:45:00Z",
    "coverLetter": "I am interested in this position..."
  }
}
```

#### Get User's Applications
```http
GET /api/v1/applications/my-applications
Authorization: Bearer <TOKEN>

Response (200):
{
  "status": true,
  "result": [
    {
      "id": "507f1f77bcf86cd799439013",
      "jobId": "507f1f77bcf86cd799439012",
      "job": { /* job details */ },
      "status": "Applied",
      "appliedAt": "2024-01-20T15:45:00Z"
    }
  ]
}
```

#### Get Job Applications (Recruiter Only)
```http
GET /api/v1/jobs/:jobId/applications
Authorization: Bearer <TOKEN>

Response (200):
{
  "status": true,
  "result": [
    {
      "id": "507f1f77bcf86cd799439013",
      "applicant": { /* applicant details */ },
      "status": "Applied",
      "appliedAt": "2024-01-20T15:45:00Z"
    }
  ]
}
```

#### Update Application Status (Recruiter Only)
```http
PATCH /api/v1/applications/:applicationId/status
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "status": "Accepted"
}

Response (200):
{
  "status": true,
  "message": "Application status updated"
}
```

### User Endpoints

#### Get User Profile
```http
GET /api/v1/users/profile/:userId

Response (200):
{
  "status": true,
  "result": {
    "id": "507f1f77bcf86cd799439011",
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER",
    "profile": {
      "firstName": "John",
      "lastName": "Doe",
      "bio": "Software developer",
      "profilePicture": "url",
      "location": "New York, NY",
      "skills": ["Java", "React", "Spring Boot"]
    }
  }
}
```

#### Update User Profile
```http
PUT /api/v1/users/profile
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "bio": "Senior Software Developer",
  "location": "New York, NY",
  "skills": ["Java", "React", "Spring Boot", "MongoDB"]
}

Response (200):
{
  "status": true,
  "message": "Profile updated successfully"
}
```

#### Change Password
```http
PUT /api/v1/users/change-password
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "currentPassword": "OldPass123@",
  "newPassword": "NewPass456@",
  "confirmPassword": "NewPass456@"
}

Response (200):
{
  "status": true,
  "message": "Password changed successfully"
}
```

### Admin Endpoints

#### Get All Users (Admin Only)
```http
GET /api/v1/admin/users
Authorization: Bearer <TOKEN>

Response (200):
{
  "status": true,
  "result": [ /* array of all users */ ]
}
```

#### Get Statistics (Admin Only)
```http
GET /api/v1/admin/statistics
Authorization: Bearer <TOKEN>

Response (200):
{
  "status": true,
  "result": {
    "totalUsers": 45,
    "totalJobs": 12,
    "totalApplications": 87,
    "usersByRole": {
      "USER": 35,
      "RECRUITER": 8,
      "ADMIN": 2
    },
    "activeJobs": 10,
    "closedJobs": 2
  }
}
```

#### Delete User (Admin Only)
```http
DELETE /api/v1/admin/users/:userId
Authorization: Bearer <TOKEN>

Response (200):
{
  "status": true,
  "message": "User deleted successfully"
}
```

---

## Authentication Flow

### JWT Token Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│ User Enters Credentials (Email, Password)                   │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ Frontend POST /api/v1/auth/login                            │
│ Body: { email, password }                                   │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ Backend: AuthController.login()                             │
│ 1. Validate request format                                  │
│ 2. Find user by email                                       │
│ 3. Compare password (BCrypt)                                │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
        ┌────────────────┬───────────────┐
        │                │               │
    ✓ Match         ✗ Invalid Email   ✗ Invalid Password
        │                │               │
        ↓                ↓               ↓
   ┌─────────┐    ┌──────────────┐  ┌──────────────┐
   │ Generate │    │ Return 401   │  │ Return 401   │
   │ JWT Token│    │ Unauthorized │  │ Unauthorized │
   └────┬────┘    └──────────────┘  └──────────────┘
        │
        ↓
┌─────────────────────────────────────────────────────────────┐
│ JwtTokenProvider.generateToken(userId)                      │
│ ├── Create JWT with claims (userId, iat, exp)              │
│ ├── Sign with HMAC-SHA256 + 256-bit secret                │
│ └── Return encoded token (header.payload.signature)        │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ Response 200 OK                                             │
│ Body: { token, user { id, email, role } }                 │
│ Cookie: jobPortalToken=<TOKEN> (HttpOnly, SameSite=Lax)   │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ Frontend: Store token + Set UserContext                     │
│ 1. Save token to localStorage or state                      │
│ 2. Set user information in context                          │
│ 3. Redirect to dashboard                                    │
└─────────────────────────────────────────────────────────────┘
```

### Token Validation Flow (For Protected Endpoints)

```
┌─────────────────────────────────────────────────────────────┐
│ Frontend: Make Request to Protected Endpoint                │
│ GET /api/v1/jobs with withCredentials: true               │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ Browser Automatically Includes Cookies:                     │
│ - jobPortalToken: <JWT_TOKEN>                              │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ Backend: JwtAuthenticationFilter                            │
│ 1. Check for JWT in cookies (jobPortalToken)               │
│ 2. Extract token string                                     │
│ 3. Validate token                                           │
└────────────────────────────┬────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
    ✓ Valid           ✗ Invalid             ✓ Expired
        │                    │                    │
        ↓                    ↓                    ↓
  ┌──────────────┐    ┌──────────────┐  ┌──────────────┐
  │ Extract userId│    │ Return 401   │  │ Return 401   │
  │ Load user    │    │ Unauthorized │  │ Token expired│
  │ Set context  │    └──────────────┘  └──────────────┘
  └────┬─────────┘
       │
       ↓
┌─────────────────────────────────────────────────────────────┐
│ Request Proceeds with User Context                          │
│ SecurityContext contains authenticated user                 │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│ Return Response with User's Data                            │
└─────────────────────────────────────────────────────────────┘
```

---

## Database Schema

### MongoDB Collections

#### Users Collection
```json
{
  "_id": ObjectId,
  "username": String (unique, 3-30 chars),
  "email": String (unique, valid email),
  "passwordHash": String (BCrypt hash),
  "role": String (USER | RECRUITER | ADMIN),
  "profile": {
    "firstName": String,
    "lastName": String,
    "bio": String,
    "profilePicture": String (URL),
    "location": String,
    "skills": [String],
    "experience": Number (years)
  },
  "companyInfo": {
    "companyName": String,
    "companyDescription": String,
    "website": String,
    "industry": String
  },
  "isActive": Boolean (default: true),
  "createdAt": Date,
  "updatedAt": Date
}

Sample Document:
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "username": "johndoe",
  "email": "john@example.com",
  "passwordHash": "$2a$12$...", // BCrypt hash
  "role": "USER",
  "profile": {
    "firstName": "John",
    "lastName": "Doe",
    "bio": "Software Developer",
    "location": "New York, NY",
    "skills": ["Java", "Spring Boot", "React", "MongoDB"],
    "experience": 5
  },
  "isActive": true,
  "createdAt": ISODate("2024-01-15T10:00:00Z"),
  "updatedAt": ISODate("2024-01-20T15:30:00Z")
}
```

#### Jobs Collection
```json
{
  "_id": ObjectId,
  "title": String,
  "company": String,
  "description": String,
  "requirements": [String],
  "location": String,
  "jobType": String (Full-time | Part-time | Contract | Remote),
  "category": String,
  "salary": {
    "min": Number,
    "max": Number,
    "currency": String,
    "isHidden": Boolean
  },
  "postedBy": ObjectId (reference to Users),
  "postedAt": Date,
  "applicationDeadline": Date,
  "applicationsCount": Number (default: 0),
  "isActive": Boolean (default: true),
  "views": Number (default: 0),
  "createdAt": Date,
  "updatedAt": Date
}

Sample Document:
{
  "_id": ObjectId("507f1f77bcf86cd799439012"),
  "title": "Senior Java Developer",
  "company": "Tech Corp",
  "description": "We are looking for experienced Java developers...",
  "requirements": ["Java 17+", "Spring Boot", "Microservices", "Docker", "5+ years experience"],
  "location": "New York, NY",
  "jobType": "Full-time",
  "category": "Technology",
  "salary": {
    "min": 120000,
    "max": 180000,
    "currency": "USD",
    "isHidden": false
  },
  "postedBy": ObjectId("507f1f77bcf86cd799439010"),
  "postedAt": ISODate("2024-01-15T10:00:00Z"),
  "applicationDeadline": ISODate("2024-02-15T23:59:59Z"),
  "applicationsCount": 15,
  "isActive": true,
  "views": 234,
  "createdAt": ISODate("2024-01-15T10:00:00Z"),
  "updatedAt": ISODate("2024-01-20T15:30:00Z")
}
```

#### Applications Collection
```json
{
  "_id": ObjectId,
  "jobId": ObjectId (reference to Jobs),
  "applicantId": ObjectId (reference to Users),
  "status": String (Applied | Reviewing | Accepted | Rejected),
  "appliedAt": Date,
  "coverLetter": String,
  "resume": String (URL),
  "statusUpdatedAt": Date,
  "statusUpdatedBy": ObjectId (reference to Users, recruiter who updated),
  "notes": String (internal notes from recruiter),
  "createdAt": Date,
  "updatedAt": Date
}

Sample Document:
{
  "_id": ObjectId("507f1f77bcf86cd799439013"),
  "jobId": ObjectId("507f1f77bcf86cd799439012"),
  "applicantId": ObjectId("507f1f77bcf86cd799439011"),
  "status": "Applied",
  "appliedAt": ISODate("2024-01-20T15:45:00Z"),
  "coverLetter": "I am very interested in this position...",
  "resume": "https://s3.amazonaws.com/resumes/johndoe.pdf",
  "createdAt": ISODate("2024-01-20T15:45:00Z"),
  "updatedAt": ISODate("2024-01-20T15:45:00Z")
}
```

### Database Relationships

```
Users (1) ──────────────── (N) Jobs
  ├─ Recruiter posts jobs
  └─ Each job has one recruiter

Users (1) ──────────────── (N) Applications
  ├─ User applies for jobs
  └─ Each application has one applicant

Jobs (1) ───────────────── (N) Applications
  ├─ Job receives multiple applications
  └─ Each application is for one job
```

---

## Configuration

### Backend Configuration (application.yml)

Located in: `full-stack-job-portal-server-main/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: job-portal-api
  
  data:
    mongodb:
      uri: mongodb://mongo:27017/job_portal
      # Local development:
      # uri: mongodb://localhost:27017/job_portal
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

server:
  port: 3000
  servlet:
    context-path: /

jwt:
  secret: M8CcVBFZv8pGo1UFvA3ZSnq+eq7IWTJJcB/fO49H4IA=  # 256-bit base64 encoded
  expiration: 86400000  # 24 hours in milliseconds

cors:
  allowed-origins: http://localhost,http://localhost:3000,http://localhost:5173,http://localhost:80

app:
  admin-code: ADMIN_SECRET_CODE_12345  # Required for admin registration
```

### Frontend Configuration

Located in: `full-stack-job-portal-client-main/vite.config.js`

```javascript
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
}
```

### Docker Configuration

#### docker-compose.yml

```yaml
version: '3.8'

services:
  db:
    image: mongo:7.0
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_DATABASE: job_portal
    volumes:
      - mongodb_data:/data/db
    networks:
      - job-portal-network

  backend:
    build:
      context: ./full-stack-job-portal-server-main
      dockerfile: Dockerfile
    ports:
      - "3000:3000"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://db:27017/job_portal
      JWT_SECRET: M8CcVBFZv8pGo1UFvA3ZSnq+eq7IWTJJcB/fO49H4IA=
      CORS_ORIGINS: http://localhost,http://localhost:3000,http://localhost:5173
    depends_on:
      - db
    networks:
      - job-portal-network

  frontend:
    build:
      context: ./full-stack-job-portal-client-main
      dockerfile: Dockerfile
    ports:
      - "5173:5173"
    environment:
      REACT_APP_API_URL: http://localhost:3000
    depends_on:
      - backend
    networks:
      - job-portal-network

  nginx:
    image: nginx:latest
    ports:
      - "80:80"
    volumes:
      - ./full-stack-job-portal-client-main/nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - frontend
      - backend
    networks:
      - job-portal-network

volumes:
  mongodb_data:

networks:
  job-portal-network:
    driver: bridge
```

#### Backend Dockerfile

```dockerfile
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/job-portal-*.jar app.jar
ENV PORT=3000
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Frontend Dockerfile

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 5173
CMD ["nginx", "-g", "daemon off;"]
```

---

## Environment Variables

### Backend Environment Variables

```bash
# MongoDB Connection
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/job_portal

# JWT Configuration
JWT_SECRET=M8CcVBFZv8pGo1UFvA3ZSnq+eq7IWTJJcB/fO49H4IA=
JWT_EXPIRATION=86400000

# Server Configuration
SERVER_PORT=3000
SERVER_SERVLET_CONTEXT_PATH=/

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost,http://localhost:3000,http://localhost:5173

# Admin Configuration
APP_ADMIN_CODE=ADMIN_SECRET_CODE_12345
```

### Frontend Environment Variables

```bash
# API Configuration
REACT_APP_API_URL=http://localhost:3000

# Optional: Analytics, etc.
REACT_APP_ENV=development
```

---

## Troubleshooting

### Common Issues & Solutions

#### 1. "Connection refused" on startup

**Problem:** Backend cannot connect to MongoDB

```bash
Error: connect ECONNREFUSED 127.0.0.1:27017
```

**Solutions:**

```bash
# Check if MongoDB is running
docker-compose ps | grep db

# If not running, start MongoDB
docker-compose up -d db

# If using local MongoDB, ensure it's running
mongod

# Verify MongoDB is accessible
mongo --eval "db.adminCommand('ping')"
```

#### 2. CORS errors in browser console

**Problem:** Frontend cannot reach backend API

```
Access to XMLHttpRequest at 'http://localhost:3000/api/v1/...' 
from origin 'http://localhost:5173' has been blocked by CORS policy
```

**Solutions:**

```bash
# Check backend logs
docker-compose logs -f backend

# Verify backend is running
curl http://localhost:3000/api/v1/jobs

# Check CORS configuration in application.yml
# Ensure your frontend origin is in allowed-origins

# Restart backend with correct CORS config
docker-compose restart backend
```

#### 3. "Token expired" after login

**Problem:** JWT token expires too quickly

**Solutions:**

```bash
# Check JWT expiration in application.yml
# Default: 86400000 ms (24 hours)

# If token keeps expiring, check:
1. Backend and frontend system clocks are synced
2. JWT secret is consistent across restarts
3. Check JWT expiration timestamp:
   
   # Decode JWT token (online at jwt.io)
   # Verify 'exp' claim shows reasonable expiration time
```

#### 4. "Invalid JWT signature" after restart

**Problem:** JWT secret changed between restarts

**Solutions:**

```bash
# Ensure JWT_SECRET is persistent:
1. Check docker-compose.yml environment variables
2. Verify JWT_SECRET in application.yml

# If changed, users need to re-login:
docker-compose down -v  # Remove volumes
docker-compose up -d --build  # Fresh start
```

#### 5. Registration fails with "400 Bad Request"

**Problem:** Password doesn't meet validation requirements

**Valid password format:**
```
✓ 8-20 characters
✓ At least one uppercase letter (A-Z)
✓ At least one lowercase letter (a-z)
✓ At least one digit (0-9)
✓ At least one special character (@#$%^&*!)

Examples:
✓ TestPass123@    (Valid)
✗ testpass123@    (Missing uppercase)
✗ TestPass@       (Missing digit)
✗ Test1@          (Less than 8 chars)
```

**Solutions:**

```bash
# Use valid password
curl -X POST http://localhost:3000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "ValidPass123@",
    "confirmPassword": "ValidPass123@",
    "role": "USER"
  }'

# Check backend logs for validation errors
docker-compose logs backend
```

#### 6. Cannot upload profile picture

**Problem:** File upload fails or size limit exceeded

**Solutions:**

```bash
# Check upload directory exists
docker exec job_portal_backend ls -la /app/public/uploads

# Create if missing
docker exec job_portal_backend mkdir -p /app/public/uploads

# Max file size configuration (application.yml):
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# Restart backend if you change these values
docker-compose restart backend
```

#### 7. Frontend shows "404" for all routes

**Problem:** React Router not configured correctly

**Solutions:**

```bash
# Check nginx.conf has proper routing
# Ensure try_files directive is set:

try_files $uri $uri/ /index.html;

# This redirects all 404s to index.html for React Router

# Restart frontend
docker-compose restart frontend
```

#### 8. "Maximum 5 failed login attempts"

**Problem:** Account locked due to failed login attempts

**Solutions:**

```bash
# Wait 15 minutes (default lockout period)
# Or restart backend to clear in-memory locks
docker-compose restart backend

# Use correct credentials
curl -X POST http://localhost:3000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "CorrectPass123@"
  }'
```

#### 9. Database connection timeout

**Problem:** MongoDB connection timeout

```bash
MongoTimeoutException: Timed out after 30000 ms
```

**Solutions:**

```bash
# Verify MongoDB is running and accessible
docker-compose logs db

# Check MongoDB disk space
docker exec job_portal_db df -h

# Increase connection timeout in application.yml
spring:
  data:
    mongodb:
      uri: mongodb://db:27017/job_portal?serverSelectionTimeoutMS=30000&connectTimeoutMS=30000

# Restart backend
docker-compose restart backend
```

#### 10. Admin registration requires admin code

**Problem:** Cannot register as admin without correct code

**Solutions:**

```bash
# Admin code is configured in application.yml
app:
  admin-code: ADMIN_SECRET_CODE_12345

# Use this code during registration:
{
  "username": "admin",
  "email": "admin@example.com",
  "password": "AdminPass123@",
  "confirmPassword": "AdminPass123@",
  "role": "ADMIN",
  "adminCode": "ADMIN_SECRET_CODE_12345"
}

# Change admin code in application.yml if needed
# Then restart backend
docker-compose restart backend
```

### Debugging Tips

#### Enable Debug Logging

**Backend:**
```yaml
# In application.yml
logging:
  level:
    root: INFO
    com.jobportal: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

**Frontend:**
```javascript
// In main.jsx or index.jsx
if (process.env.NODE_ENV === 'development') {
  localStorage.setItem('debug', 'app:*');
}
```

#### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db

# Last 100 lines
docker-compose logs --tail=100 backend

# Follow logs with timestamps
docker-compose logs -f --timestamps backend
```

#### Access MongoDB Directly

```bash
# Connect to MongoDB
docker exec -it job_portal_db mongosh

# Inside mongosh
use job_portal
show collections
db.users.find()
db.jobs.find()
db.applications.find()

# Find specific user
db.users.findOne({ email: "admin@gmail.com" })

# Count documents
db.users.countDocuments()
```

---

## Development Guide

### Adding a New Feature

#### Example: Add "Skills Required" Filter to Job Search

**1. Update Backend JobController**

```java
@GetMapping
public ResponseEntity<JobResponseDto> getAllJobs(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int limit,
    @RequestParam(required = false) String skill) {
    
    List<Job> jobs;
    if (skill != null && !skill.isEmpty()) {
        jobs = jobService.getJobsBySkill(skill, page, limit);
    } else {
        jobs = jobService.getAllJobs(page, limit);
    }
    return ResponseEntity.ok(new JobResponseDto(true, jobs));
}
```

**2. Update Frontend API Handler**

```javascript
// In utils/FetchHandlers.js
export const fetchJobsBySkill = async (skill, page = 1) => {
    try {
        const response = await axios.get(
            `/api/v1/jobs?skill=${skill}&page=${page}`,
            { withCredentials: true }
        );
        return response.data.result;
    } catch (error) {
        throw error;
    }
};
```

**3. Update React Component**

```jsx
// In components/AllJobsPage/AllJobs.jsx
const [selectedSkill, setSelectedSkill] = useState('');

const handleSkillFilter = async (skill) => {
    setSelectedSkill(skill);
    const jobs = await fetchJobsBySkill(skill);
    setJobs(jobs);
};

return (
    <div>
        <input
            type="text"
            placeholder="Filter by skill"
            onChange={(e) => handleSkillFilter(e.target.value)}
        />
        {/* Display jobs */}
    </div>
);
```

**4. Test the Feature**

```bash
# Backend test
curl "http://localhost:3000/api/v1/jobs?skill=Java"

# Frontend should now show filtered results
```

### Adding a New API Endpoint

**1. Create the DTO (Data Transfer Object)**

```java
// In Model/UpdateJobStatusDto.java
public class UpdateJobStatusDto {
    @NotBlank(message = "Status cannot be blank")
    private String status;
    
    @NotBlank(message = "Notes cannot be blank")
    private String notes;
    
    // Getters and Setters
}
```

**2. Create the Controller Method**

```java
@PatchMapping("/{jobId}/status")
@PreAuthorize("hasRole('RECRUITER') or hasRole('ADMIN')")
public ResponseEntity<?> updateJobStatus(
    @PathVariable String jobId,
    @RequestBody UpdateJobStatusDto request) {
    
    Job updatedJob = jobService.updateJobStatus(jobId, request);
    return ResponseEntity.ok(new ApiResponse(
        true,
        "Job status updated",
        updatedJob
    ));
}
```

**3. Create the Service Method**

```java
public Job updateJobStatus(String jobId, UpdateJobStatusDto dto) {
    Job job = jobRepository.findById(jobId)
        .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    
    job.setStatus(dto.getStatus());
    job.setNotes(dto.getNotes());
    return jobRepository.save(job);
}
```

**4. Test with curl**

```bash
curl -X PATCH http://localhost:3000/api/v1/jobs/507f1f77bcf86cd799439012/status \
  -H "Content-Type: application/json" \
  -H "Cookie: jobPortalToken=<JWT_TOKEN>" \
  -d '{
    "status": "Closed",
    "notes": "Position filled"
  }'
```

### Testing

#### Backend Testing (JUnit)

```java
@SpringBootTest
class JobControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetAllJobs() throws Exception {
        mockMvc.perform(get("/api/v1/jobs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(true));
    }
}
```

#### Frontend Testing (Vitest)

```javascript
import { describe, it, expect, vi } from 'vitest';
import { fetchAllJobs } from './FetchHandlers';

describe('FetchHandlers', () => {
    it('should fetch all jobs', async () => {
        const jobs = await fetchAllJobs();
        expect(jobs).toBeInstanceOf(Array);
    });
});
```

#### End-to-End Testing

```bash
# Run the full stack
docker-compose up -d

# Test critical paths
curl http://localhost/
curl http://localhost:3000/api/v1/jobs
curl -X POST http://localhost:3000/api/v1/auth/login \
  -d '{"email":"test@example.com","password":"Test123@"}'
```

### Performance Tips

**Backend:**
- Add database indexes on frequently queried fields
- Implement pagination for large result sets
- Cache static data with Redis
- Use async processing for heavy operations

**Frontend:**
- Lazy load components with React.lazy()
- Memoize expensive computations
- Optimize bundle size with code splitting
- Use React DevTools Profiler

**Database:**
- Create compound indexes for multi-field queries
- Archive old data periodically
- Monitor connection pool usage

---

## Deployment

### Deploy to Production

#### Using Docker Compose on Server

```bash
# SSH into server
ssh user@production.server.com

# Clone or copy project
git clone <repo-url> job-portal

# Navigate to project
cd job-portal

# Build and start services
docker-compose -f docker-compose.prod.yml up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### Using Kubernetes

```bash
# Create deployment YAML
apiVersion: apps/v1
kind: Deployment
metadata:
  name: job-portal
spec:
  replicas: 3
  selector:
    matchLabels:
      app: job-portal
  template:
    metadata:
      labels:
        app: job-portal
    spec:
      containers:
      - name: backend
        image: job-portal-backend:latest
        ports:
        - containerPort: 3000
      - name: frontend
        image: job-portal-frontend:latest
        ports:
        - containerPort: 5173
```

#### Using Cloud Services

**AWS:**
```bash
# Push to ECR
aws ecr get-login-password | docker login --username AWS --password-stdin <account>.dkr.ecr.us-east-1.amazonaws.com
docker tag job-portal-backend:latest <account>.dkr.ecr.us-east-1.amazonaws.com/job-portal:latest
docker push <account>.dkr.ecr.us-east-1.amazonaws.com/job-portal:latest

# Deploy with ECS or AppRunner
```

**Heroku:**
```bash
# Create app
heroku create job-portal-app

# Set environment variables
heroku config:set JWT_SECRET=<your-secret>
heroku config:set MONGODB_URI=<your-mongodb-uri>

# Deploy
git push heroku main
```

---

## Support & Contributing

### Reporting Issues

When reporting issues, please include:
1. Error message and stack trace
2. Steps to reproduce
3. Expected vs actual behavior
4. System/environment info (OS, Docker version, etc.)
5. Relevant log output

### Contributing Guidelines

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit Pull Request with description

---

## License

This project is licensed under the MIT License - see LICENSE file for details.

---

## Contact & Support

- **Email**: support@jobportal.dev
- **Issues**: GitHub Issues
- **Documentation**: [Full Wiki](./docs/WIKI.md)

---

**Last Updated:** January 2024  
**Version:** 1.0.0  
**Status:** Production Ready
