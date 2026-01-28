# Frontend API Routing - Quick Reference

## Environment Variables

### `.env` (Development)
```dotenv
VITE_API_BASE_URL=http://localhost:8080
```

### `.env.example` (Template)
```dotenv
# For local development: http://localhost:8080
# For Docker deployment: http://api-gateway:8080
VITE_API_BASE_URL=http://localhost:8080
```

---

## Frontend API Client

### Location
`src/utils/FetchHandlers.js`

### Key Functions

```javascript
// All requests prepend VITE_API_BASE_URL
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const buildApiUrl = (path) => {
    return `${API_BASE_URL}${path}`;
};

// Example: buildApiUrl('/api/v1/jobs') → 'http://localhost:8080/api/v1/jobs'
```

### HTTP Methods (All Include withCredentials)

```javascript
// GET requests
getAllHandler(url)           // Returns full response
getSingleHandler(url)        // Returns res.data.result

// POST/PATCH/PUT
postHandler({ url, body })
updateHandler({ url, body })
updateHandlerPut({ url, body })

// DELETE
deleteHandler(url)
```

---

## API Gateway Routing

### Port
```
8080 (container: api-gateway:8080)
```

### Routes

| Incoming | Routes To | Service |
|----------|-----------|---------|
| `/api/v1/auth/**` | auth-service:3001 | Authentication |
| `/api/v1/users/**` | user-service:3002 | User Management |
| `/api/v1/admin/**` | user-service:3002 | Admin Operations |
| `/api/v1/jobs/**` | job-service:3003 | Job Management |
| `/api/v1/applications/**` | application-service:3004 | Job Applications |

---

## Authentication Flow

### Cookie-Based JWT

1. **User Logs In**
   - POST `/api/v1/auth/login`
   - Server responds with `Set-Cookie: jobPortalToken=<jwt>`

2. **Subsequent Requests**
   - Browser auto-sends cookie (axios `withCredentials: true`)
   - Gateway extracts JWT from cookie
   - Gateway validates & injects headers:
     - `X-USER-ID: <userId>`
     - `X-USER-ROLE: <role>`

3. **Protected Routes**
   - Require valid JWT in `jobPortalToken` cookie
   - Return 401 Unauthorized if missing/invalid

---

## Public vs Protected Endpoints

### Public (No JWT Required)
```
POST   /api/v1/auth/register
POST   /api/v1/auth/login
GET    /api/v1/jobs
```

### Protected (JWT Required)
```
PATCH  /api/v1/users
GET    /api/v1/users
DELETE /api/v1/users/{id}
GET    /api/v1/admin/**
POST   /api/v1/jobs
PATCH  /api/v1/jobs/{id}
DELETE /api/v1/jobs/{id}
POST   /api/v1/applications/apply
GET    /api/v1/applications
PATCH  /api/v1/applications/{id}
```

---

## Usage Examples

### Frontend Making API Calls

```javascript
import { postHandler, getAllHandler } from '@/utils/FetchHandlers';

// Login
const response = await postHandler({
    url: '/api/v1/auth/login',
    body: { email, password }
});

// Get jobs
const jobs = await getAllHandler('/api/v1/jobs');

// Create job (protected)
const newJob = await postHandler({
    url: '/api/v1/jobs',
    body: { title, description, location }
});
```

### Request Path Transformation

```
Frontend: /api/v1/jobs
          ↓
          buildApiUrl('/api/v1/jobs')
          ↓
          http://localhost:8080/api/v1/jobs
          ↓
          API Gateway routes to job-service:3003
          ↓
          Rewrites path: /api/v1/jobs → /jobs
          ↓
          Job Service handles: GET /jobs
```

---

## Docker Deployment

### Service Network
```
Docker Network: job-portal-network
Frontend (Nginx) → API Gateway → Microservices
```

### DNS Resolution (Inside Docker)
```
api-gateway:8080           (API Gateway)
auth-service:3001          (Auth Service)
user-service:3002          (User Service)
job-service:3003           (Job Service)
application-service:3004   (Application Service)
mongodb:27017              (MongoDB)
```

### Frontend .env in Docker
```dotenv
VITE_API_BASE_URL=http://api-gateway:8080
```

---

## Troubleshooting

### Issue: "Cannot GET /api/v1/..."
**Cause**: Frontend still using old port (3000, 3001, etc.)
**Fix**: Check `VITE_API_BASE_URL=http://localhost:8080` in `.env`

### Issue: "CORS error" or "Cookie not sent"
**Cause**: `withCredentials: true` missing
**Fix**: All methods in FetchHandlers.js must include `withCredentials: true`

### Issue: "401 Unauthorized" on protected endpoints
**Cause**: JWT cookie not present or invalid
**Fix**: 
1. Login first to get cookie
2. Check cookie name: `jobPortalToken`
3. Verify `withCredentials: true` in requests

### Issue: "host not found" nginx error
**Cause**: nginx.conf pointing to non-existent backend
**Fix**: Use `proxy_pass http://api-gateway:8080`

---

## Configuration Files

| File | Purpose |
|------|---------|
| `.env` | Frontend environment variables |
| `src/utils/FetchHandlers.js` | API client & HTTP utilities |
| `full-stack-job-portal-client-main/nginx.conf` | Nginx reverse proxy config |
| `microservices/api-gateway/src/main/resources/application.yml` | Gateway routing rules |
| `docker-compose-microservices.yml` | Service definitions & networking |

---

## Important Notes

✅ **Single Entry Point**: All frontend requests → API Gateway (8080)
✅ **No Service Ports in Frontend**: Frontend only knows about port 8080
✅ **Automatic Cookie Handling**: Axios auto-includes cookies with `withCredentials: true`
✅ **Environment-Specific Config**: VITE_API_BASE_URL configured per environment
✅ **Protected by Design**: Invalid JWT immediately returns 401

---

**Last Updated**: January 28, 2026
