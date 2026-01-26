# 🚀 Docker Setup - Quick Reference

## Start Deployment

```bash
cd /home/tushar/project/job-portal
docker-compose up --build
```

Wait 30-60 seconds for all services to start, then:

## Access Application

| Service | URL | Purpose |
|---------|-----|---------|
| **Frontend** | http://localhost | React App - User Interface |
| **Backend API** | http://localhost:3000 | Express API - Endpoints |
| **Database** | localhost:27017 | MongoDB - Data Storage |

## Common Commands

```bash
# View all services status
docker-compose ps

# View logs
docker-compose logs -f                    # All services
docker-compose logs -f backend            # Specific service
docker-compose logs -f frontend           # Specific service

# Stop/Start
docker-compose stop                       # Stop all
docker-compose up -d                      # Start in background
docker-compose restart backend            # Restart specific

# Clean up
docker-compose down                       # Remove containers
docker-compose down -v                    # Remove containers + volumes

# Execute commands
docker-compose exec backend bash          # Shell into backend
docker-compose exec mongo mongosh job-portal  # MongoDB shell
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 80 already in use | `lsof -i :80` then kill process or change port |
| Port 3000 already in use | Same as above, or edit docker-compose.yml |
| MongoDB connection error | Wait 30s, check `docker-compose logs mongo` |
| Frontend can't reach API | Verify backend is running: `docker-compose logs backend` |
| Need to rebuild | `docker-compose down && docker-compose up --build` |

## Architecture

```
Browser (http://localhost)
  ↓
Nginx (Frontend Container)
  ├─ Static Assets (React App)
  └─ /api/* → Proxy to Backend
       ↓
    Express Backend (Backend Container)
       ↓
    MongoDB (Database Container)
```

## Browser Flow

1. Open http://localhost
2. React app loads (served by Nginx)
3. Click any link → React Router handles (no page refresh)
4. Click "Apply Job" or "Login" → API call to http://localhost:3000
5. Backend processes → Checks MongoDB → Responds with JSON
6. Frontend updates UI

## Environment Files

**Must exist before running docker-compose:**

### Backend: `full-stack-job-portal-server-main/.env`
```
MONGODB_URI=mongodb://mongo:27017/job-portal
JWT_SECRET=your-secret-key
COOKIE_SECRET=your-secret-key
COOKIE_NAME=jobPortalToken
CORS_ORIGIN=http://localhost
NODE_ENV=production
PORT=3000
```

### Frontend: `full-stack-job-portal-client-main/.env`
```
VITE_API_BASE_URL=http://localhost:3000
```

**Quick setup:**
```bash
cp full-stack-job-portal-server-main/.env.example full-stack-job-portal-server-main/.env
cp full-stack-job-portal-client-main/.env.example full-stack-job-portal-client-main/.env
```

## Files Created/Modified

### New Files
- `docker-compose.yml` - Service orchestration
- `DOCKER_DEPLOYMENT.md` - Complete guide
- `IMPLEMENTATION_SUMMARY.md` - What was done

### Backend
- `Dockerfile` - Container image
- `.dockerignore` - Exclude files
- `.env.example` - Template variables
- `App.js` - Updated CORS config

### Frontend  
- `Dockerfile` - Container image
- `.dockerignore` - Exclude files
- `.env.example` - Template variables
- `nginx.conf` - Web server config
- **Updated 16 files** - Dynamic API URLs

## First Run Expected Time

- **First build**: 2-5 minutes (downloading images, installing deps)
- **Subsequent runs**: 10-20 seconds
- **After code changes**: 1-2 minutes (rebuild only changed services)

## Verify It's Working

```bash
# Check all services are running
docker-compose ps
# All should show "Up"

# Test backend
curl http://localhost:3000/
# Should return: "Job Hunter Server is running!"

# Open browser
open http://localhost  # macOS
xdg-open http://localhost  # Linux
start http://localhost  # Windows
```

## Database Persistence

- MongoDB data is stored in Docker volume `mongo-data`
- Persists between container restarts
- Removed only when you run: `docker-compose down -v`

## For Development Changes

If you modify code:

```bash
# After code change
docker-compose build backend        # Rebuild only backend
docker-compose up backend           # Restart backend

# Or rebuild and restart
docker-compose up -d --build
```

## Need Full Restart?

```bash
docker-compose down      # Stop and remove containers
docker-compose up --build # Rebuild and start
```

## Docs

- **Complete Guide**: [DOCKER_DEPLOYMENT.md](./DOCKER_DEPLOYMENT.md)
- **Implementation Details**: [IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md)

---

**Ready?** Run: `docker-compose up --build` 🚀
