# Docker Deployment Guide for Job Portal

This guide walks you through deploying the Job Portal application as Docker microservices locally.

## Prerequisites

- **Docker** - Download from [docker.com](https://www.docker.com/)
- **Docker Compose** - Usually included with Docker Desktop
- **Git** - For cloning the repository
- 4GB RAM minimum for comfortable operation

## Project Structure

```
job-portal/
├── docker-compose.yml              # Docker orchestration
├── full-stack-job-portal-server-main/
│   ├── Dockerfile                  # Backend Node.js image
│   ├── .dockerignore               # Files to exclude from image
│   ├── .env.example                # Environment variables template
│   └── ...
└── full-stack-job-portal-client-main/
    ├── Dockerfile                  # Frontend Nginx image
    ├── nginx.conf                  # Nginx configuration
    ├── .dockerignore               # Files to exclude from image
    ├── .env.example                # Environment variables template
    └── ...
```

## Services Overview

The deployment consists of three containerized services:

1. **MongoDB** (mongo)
   - Port: 27017 (internal only)
   - Database: job-portal
   - Persistence: Docker named volume `mongo-data`

2. **Backend API** (backend)
   - Port: 3000
   - Technology: Node.js + Express
   - Database: Connected to MongoDB

3. **Frontend Web Server** (frontend)
   - Port: 80
   - Technology: React + Nginx
   - Serves frontend and reverse-proxies `/api/*` to backend

## Quick Start

### Step 1: Clone and Setup

```bash
cd /home/tushar/project/job-portal
```

### Step 2: Create Environment Files

Copy the example files to create actual `.env` files:

**Backend environment** (`.env` in `full-stack-job-portal-server-main/`):
```bash
cp full-stack-job-portal-server-main/.env.example full-stack-job-portal-server-main/.env
```

**Frontend environment** (`.env` in `full-stack-job-portal-client-main/`):
```bash
cp full-stack-job-portal-client-main/.env.example full-stack-job-portal-client-main/.env
```

Edit these files if you want to customize (the defaults work for local deployment):

```bash
# Backend .env
MONGODB_URI=mongodb://mongo:27017/job-portal
JWT_SECRET=your-secret-key
COOKIE_SECRET=your-secret-key
COOKIE_NAME=jobPortalToken
CORS_ORIGIN=http://localhost
```

```bash
# Frontend .env
VITE_API_BASE_URL=http://localhost:3000
```

### Step 3: Build and Start Containers

Build all images and start services:
```bash
docker-compose up --build
```

For background mode (daemon):
```bash
docker-compose up -d --build
```

### Step 4: Access the Application

Open your browser and navigate to:

- **Frontend**: http://localhost
- **Backend API**: http://localhost:3000
- **MongoDB**: localhost:27017 (for tools like MongoDB Compass)

## Useful Docker Commands

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mongo
```

### Check Service Status

```bash
docker-compose ps
```

### Stop Services

```bash
# Stop all services (keeps data)
docker-compose stop

# Stop and remove containers
docker-compose down

# Remove containers and volumes (careful - deletes database!)
docker-compose down -v
```

### Restart Services

```bash
# Restart all
docker-compose restart

# Restart specific service
docker-compose restart backend
```

### Execute Commands in Container

```bash
# Run command in backend
docker-compose exec backend npm run dev

# Run MongoDB shell commands
docker-compose exec mongo mongosh job-portal
```

### View Specific Container Logs

```bash
docker-compose logs backend --tail=50
```

## Troubleshooting

### Port Already in Use

If you get "port already in use" error:

```bash
# Find what's using the port
lsof -i :80      # Frontend
lsof -i :3000    # Backend
lsof -i :27017   # MongoDB

# Either stop the service or change port in docker-compose.yml
```

### Database Connection Error

Ensure MongoDB container is healthy:

```bash
docker-compose ps  # Check health status
docker-compose logs mongo  # View MongoDB logs
```

### Frontend Cannot Reach Backend

Check backend is running and CORS is configured:

```bash
docker-compose ps backend
docker-compose logs backend
```

Verify `CORS_ORIGIN` environment variable in backend `.env`:
```
CORS_ORIGIN=http://localhost
```

### Services Won't Start

```bash
# Remove old containers and rebuild
docker-compose down
docker-compose up --build

# View detailed logs
docker-compose logs -f
```

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Your Browser                          │
│              http://localhost                           │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │   Frontend (Nginx)     │
        │   Port: 80             │
        │   - React App          │
        │   - SPA Router         │
        └────────────┬───────────┘
                     │
        ┌────────────┴──────────────┐
        │                           │
        │ (Static Assets)    (API Proxy)
        │ index.html etc.    /api/*
        │                           │
        │                           ▼
        │        ┌──────────────────────────┐
        │        │  Backend (Node.js/Expr) │
        │        │  Port: 3000              │
        │        │  - API Endpoints         │
        │        │  - Authentication        │
        │        │  - Business Logic        │
        │        └──────────────┬───────────┘
        │                       │
        │                       ▼
        │        ┌──────────────────────────┐
        │        │   MongoDB Database       │
        │        │   Port: 27017            │
        │        │   - Data Persistence     │
        │        │   - Collections:         │
        │        │     • Users              │
        │        │     • Jobs               │
        │        │     • Applications       │
        │        └──────────────────────────┘
        │
        └──► Static Assets (CSS, JS, images)
             Served from /usr/share/nginx/html
```

## API Endpoints

All API calls from browser go to `http://localhost:3000/api/v1/*`:

- **Authentication**
  - `POST /api/v1/auth/register` - Register user
  - `POST /api/v1/auth/login` - Login user
  - `POST /api/v1/auth/logout` - Logout user
  - `GET /api/v1/auth/me` - Get current user

- **Jobs**
  - `GET /api/v1/jobs` - List all jobs (paginated)
  - `GET /api/v1/jobs/:id` - Get job details
  - `POST /api/v1/jobs` - Create job (recruiter only)
  - `PATCH /api/v1/jobs/:id` - Update job
  - `DELETE /api/v1/jobs/:id` - Delete job

- **Applications**
  - `POST /api/v1/application/apply` - Apply for job
  - `GET /api/v1/application/applicant-jobs` - View applied jobs
  - `GET /api/v1/application/recruiter-jobs` - View applications received

- **Users**
  - `GET /api/v1/users/:id` - Get user profile
  - `PATCH /api/v1/users` - Update user profile

- **Admin**
  - `GET /api/v1/admin/stats` - Get statistics
  - `GET /api/v1/admin/info` - Get admin info

## Environment Variables

### Backend (.env)

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `MONGODB_URI` | MongoDB connection string | - | Yes |
| `JWT_SECRET` | Secret for JWT tokens | - | Yes |
| `COOKIE_SECRET` | Secret for cookie encryption | - | Yes |
| `COOKIE_NAME` | Name of auth cookie | jobPortalToken | No |
| `CORS_ORIGIN` | Allowed CORS origins | http://localhost | No |
| `NODE_ENV` | Environment mode | production | No |
| `PORT` | Server port | 3000 | No |

### Frontend (.env)

| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Backend API base URL | http://localhost:3000 |

## Development Notes

### Making Changes

1. **Frontend Changes**: Rebuild the image
   ```bash
   docker-compose build frontend
   docker-compose up frontend
   ```

2. **Backend Changes**: Rebuild the image
   ```bash
   docker-compose build backend
   docker-compose up backend
   ```

3. **For Rapid Development**: Use bind mount in docker-compose.yml
   ```yaml
   backend:
     volumes:
       - ./full-stack-job-portal-server-main:/app
       - /app/node_modules
   ```
   Then restart: `docker-compose restart backend`

### Database Access

Connect to MongoDB from your local machine:

**Using MongoDB Compass**:
1. Download [MongoDB Compass](https://www.mongodb.com/products/compass)
2. Connection URI: `mongodb://localhost:27017`

**Using mongosh CLI**:
```bash
docker-compose exec mongo mongosh job-portal
```

## Production Considerations

For production deployment:

1. **Use strong secrets** - Change `JWT_SECRET` and `COOKIE_SECRET` to random, strong values
2. **Environment separation** - Create separate `.env.production` files
3. **MongoDB Atlas** - Use cloud MongoDB instead of containerized
4. **Reverse proxy** - Add Traefik or Nginx reverse proxy for HTTPS
5. **Security** - Implement rate limiting, input validation, CORS whitelisting
6. **Scaling** - Use Docker Swarm or Kubernetes for multiple instances
7. **Monitoring** - Add logging and monitoring (ELK stack, Datadog, etc.)

## Support

For issues or questions:

1. Check logs: `docker-compose logs -f`
2. Verify services: `docker-compose ps`
3. Test connectivity: `docker-compose exec backend curl http://mongo:27017`
4. Rebuild: `docker-compose down && docker-compose up --build`

---

**Last Updated**: January 26, 2026
**Docker Compose Version**: 3.8
**Node.js Version**: 18-alpine
**MongoDB Version**: latest
