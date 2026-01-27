# Job Portal Backend - Spring Boot 3.x

A modern REST API backend for a job portal application built with Spring Boot 3.x, Spring Security, MongoDB, and JWT authentication.

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Java Version**: 21
- **Database**: MongoDB 7.0
- **Authentication**: JWT with HTTP-only Cookies
- **Security**: Spring Security 6.x with Method-Level Authorization (@PreAuthorize)
- **Password Encoding**: BCrypt (strength 16)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test, TestContainers

## Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker & Docker Compose (for local development)
- MongoDB 7.0 (or use Docker Compose)

## Setup & Installation

### Option 1: Using Docker Compose (Recommended)

```bash
# From the project root directory
docker-compose up -d

# This will start:
# - MongoDB on port 27017
# - Java Backend on port 3000
# - React Frontend on port 80
```

### Option 2: Local Development

1. **Install MongoDB locally**
   ```bash
   # On macOS with Homebrew
   brew tap mongodb/brew
   brew install mongodb-community
   brew services start mongodb-community

   # On Ubuntu
   sudo apt-get install -y mongodb
   sudo systemctl start mongod
   ```

2. **Build and run the backend**
   ```bash
   cd job-portal-backend
   mvn clean install
   mvn spring-boot:run
   ```

3. **Access the application**
   - Backend API: http://localhost:3000
   - MongoDB: mongodb://localhost:27017/job-portal

## Configuration

### Environment Variables

Create a `.env` file or set environment variables:

```bash
PORT=3000
DB_STRING=mongodb://localhost:27017/job-portal
JWT_SECRET=job-portal-secret-jwt-key-2024
COOKIE_NAME=jobPortalToken
COOKIE_SECRET=job-portal-secret-cookie-key-2024
CORS_ORIGIN=http://localhost,http://localhost:3000,http://localhost:5173
```

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: ${DB_STRING:mongodb://localhost:27017/job-portal}

server:
  port: ${PORT:3000}

jwt:
  secret: ${JWT_SECRET:job-portal-secret-jwt-key-2024}
  expiration: 86400000  # 1 day in milliseconds

cors:
  allowed-origins: ${CORS_ORIGIN:http://localhost}
```

## Project Structure

```
src/main/java/com/jobportal/
├── controller/           # REST API endpoints
├── service/             # Business logic layer
├── model/               # MongoDB entities
├── repository/          # Data access layer
├── dto/                 # Data transfer objects
├── security/            # JWT and authentication
├── config/              # Spring configuration
└── exception/           # Global exception handling

src/main/resources/
├── application.yml      # Application configuration

src/test/
├── controller/          # Integration tests for endpoints
├── security/            # Security and password tests
└── resources/
    └── application-test.yml  # Test configuration
```

## API Endpoints

### Authentication (`/api/v1/auth`)

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/register` | No | Register a new user |
| POST | `/login` | No | Login and get JWT token |
| GET | `/me` | Yes | Get current user profile |
| POST | `/logout` | Yes | Logout and clear cookie |

### Jobs (`/api/v1/jobs`)

| Method | Endpoint | Auth Required | Role |
|--------|----------|---------------|------|
| GET | `/` | No | Anyone can view jobs |
| POST | `/` | Yes | Recruiter only |
| GET | `/my-jobs` | Yes | Recruiter only |
| GET | `/{id}` | No | Anyone |
| PATCH | `/{id}` | Yes | Recruiter (owner only) |
| DELETE | `/{id}` | Yes | Recruiter (owner only) |
| PATCH | `/{id}/status` | Yes | Recruiter (owner only) |

### Users (`/api/v1/users`)

| Method | Endpoint | Auth Required | Role |
|--------|----------|---------------|------|
| GET | `/` | Yes | Admin only |
| PATCH | `/` | Yes | Any authenticated user |
| GET | `/{id}` | Yes | Any authenticated user |
| DELETE | `/{id}` | Yes | Admin only |
| PATCH | `/{id}/role` | Yes | Admin only |

### Applications (`/api/v1/application`)

| Method | Endpoint | Auth Required | Role |
|--------|----------|---------------|------|
| POST | `/apply` | Yes | User only |
| GET | `/` | Yes | User only |
| GET | `/recruiter-applications` | Yes | Recruiter only |
| PATCH | `/{id}` | Yes | Recruiter only |
| GET | `/{id}/download-resume` | Yes | Applicant or Recruiter |

### Admin (`/api/v1/admin`)

| Method | Endpoint | Auth Required | Role |
|--------|----------|---------------|------|
| GET | `/info` | Yes | Admin only |
| GET | `/monthly-stats` | Yes | Admin only |

## Authentication Flow

1. **Registration**: POST `/api/v1/auth/register` with username, email, password, role
2. **Login**: POST `/api/v1/auth/login` receives JWT token in HTTP-only cookie
3. **Access Protected Endpoints**: JWT automatically sent via cookie
4. **Token Validation**: JWT token expires in 24 hours
5. **Logout**: POST `/api/v1/auth/logout` clears the cookie

## Security Features

### Spring Security Implementation

- **Method-Level Authorization**: Using `@PreAuthorize` with SpEL expressions
  ```java
  @PreAuthorize("hasRole('RECRUITER')")
  @PreAuthorize("hasRole('ADMIN')")
  @PreAuthorize("@jobService.isJobOwner(#jobId, authentication.principal.email)")
  ```

- **Role-Based Access Control**:
  - `ADMIN`: Full system access
  - `RECRUITER`: Can create/edit jobs and view applications
  - `USER`: Can apply for jobs

- **JWT Authentication**: Tokens stored in secure HTTP-only cookies
- **Password Hashing**: BCrypt with strength 16 (compatible with Node.js bcrypt)
- **CORS Configuration**: Configured for frontend origin
- **Stateless Sessions**: JWT-based stateless authentication

## File Upload

- **Supported Types**: PDF, DOC, DOCX
- **Storage Location**: `public/uploads/`
- **Max File Size**: 5MB
- **Authorization**: Only applicant or recruiter can download resumes

## Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=AuthControllerIntegrationTest
mvn test -Dtest=JobControllerIntegrationTest
```

### Run with Coverage

```bash
mvn clean test jacoco:report
```

### Test Coverage

- **Auth Endpoints**: Registration, Login, Password validation
- **Job CRUD**: Create, Read, Update, Delete with authorization checks
- **User Management**: Profile updates, role management
- **Applications**: Application lifecycle, duplicate prevention
- **Security**: Authentication, authorization, role-based access
- **Password Hashing**: BCrypt compatibility with Node.js bcrypt

## Development Tips

### Hot Reload

For faster development, use Spring Boot DevTools:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### Debugging

Run with debug enabled:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

## Database Migration from Node.js

The Java backend maintains the same MongoDB schema as the Node.js backend:

1. **User Collection**: Same fields and validation
2. **Job Collection**: Identical structure
3. **Application Collection**: Compatible schema

Existing MongoDB data will work seamlessly with the Java backend.

## Troubleshooting

### MongoDB Connection Error

```
Error: Unable to connect to MongoDB
```

**Solution**: Ensure MongoDB is running and connection string is correct
```bash
# Check if MongoDB is running
docker ps | grep mongo

# Or for local installation
ps aux | grep mongod
```

### Port Already in Use

```
Error: Port 3000 is already in use
```

**Solution**: Change the port in environment variables or kill the process
```bash
export PORT=3001
# Or
lsof -i :3000  # Find process
kill -9 <PID>  # Kill the process
```

### JWT Token Invalid

```
Error: JWT token invalid or expired
```

**Solution**: Ensure JWT_SECRET matches across all instances and clear cookies
```bash
# In browser DevTools, delete jobPortalToken cookie and login again
```

## Production Deployment

### Build Docker Image

```bash
cd job-portal-backend
docker build -t job-portal-backend:latest .
```

### Push to Registry

```bash
docker tag job-portal-backend:latest your-registry/job-portal-backend:latest
docker push your-registry/job-portal-backend:latest
```

### Deploy with Docker Compose

```bash
docker-compose -f docker-compose.yml up -d
```

## Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [JWT (JJWT)](https://github.com/jwtk/jjwt)

## License

This project is part of the Job Portal application suite.
