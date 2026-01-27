# Deployment Checklist - POST /api/v1/jobs Fix

## Pre-Deployment Verification

### Code Changes ✅

- [x] CreateJobRequest.java - @Pattern validation added
- [x] JobService.java - Error handling and case normalization added
- [x] AddJob.jsx - Client validation and payload normalization added
- [x] No compilation errors in backend
- [x] No syntax errors in frontend

### Verification Commands

```bash
# Backend verification
cd /home/tushar/project/Job_portal/job-portal-backend
mvn clean compile

# Frontend verification (if you have Node.js setup)
cd /home/tushar/project/Job_portal/full-stack-job-portal-client-main
npm run build
```

---

## Pre-Deployment Tests

### 1. Backend Build Verification
```bash
cd /home/tushar/project/Job_portal/job-portal-backend
mvn clean package
```

**Expected:** Build succeeds with no errors

---

### 2. Frontend Build Verification
```bash
cd /home/tushar/project/Job_portal/full-stack-job-portal-client-main
npm install
npm run build
```

**Expected:** Build succeeds with no errors

---

### 3. Docker Build Verification (Optional)
```bash
docker-compose build
```

**Expected:** Both services build successfully

---

## Post-Deployment Tests

### 1. Valid Job Submission Test

**Step 1:** Open Postman or use cURL

**Step 2:** Send valid request
```json
POST /api/v1/jobs
Content-Type: application/json

{
  "company": "Test Company Inc",
  "position": "Test Engineer",
  "jobStatus": "PENDING",
  "jobType": "full-time",
  "jobLocation": "Remote",
  "jobVacancy": "3",
  "jobSalary": "$100k-$150k",
  "jobDeadline": "2026-03-27T23:59:59Z",
  "jobDescription": "This is a test job posting.",
  "jobSkills": ["Skill1", "Skill2"],
  "jobFacilities": ["Remote Work", "Health Insurance"],
  "jobContact": "+1-800-555-0123"
}
```

**Expected Response:** 201 Created
```json
{
  "status": true,
  "result": { /* job object */ },
  "message": "Job created successfully"
}
```

---

### 2. Invalid Status Validation Test

**Step 1:** Send invalid jobStatus
```json
{
  "company": "Test Company Inc",
  "position": "Test Engineer",
  "jobStatus": "invalid_status",
  ...
}
```

**Expected Response:** 400 Bad Request
```json
{
  "status": false,
  "message": "Validation failed",
  "result": ["Job status must be 'PENDING', 'INTERVIEW', or 'DECLINED'"]
}
```

---

### 3. Invalid Type Validation Test

**Step 1:** Send invalid jobType
```json
{
  "company": "Test Company Inc",
  "position": "Test Engineer",
  "jobStatus": "PENDING",
  "jobType": "flexible-hours",
  ...
}
```

**Expected Response:** 400 Bad Request
```json
{
  "status": false,
  "message": "Validation failed",
  "result": ["Job type must be 'full-time', 'part-time', or 'internship'"]
}
```

---

### 4. Empty Skills Validation Test

**Frontend Test:** Try submitting without adding skills
- **Expected:** Error popup before API call: "Please add at least one skill"

**Direct API Test:** Send empty array (bypass frontend)
```json
{
  ...
  "jobSkills": [],
  ...
}
```

**Expected Response:** 400 Bad Request
```json
{
  "status": false,
  "message": "Validation failed",
  "result": ["Job skills are required"]
}
```

---

### 5. Case Insensitivity Test

**Step 1:** Send lowercase jobStatus
```json
{
  "company": "Test Company Inc",
  "position": "Test Engineer",
  "jobStatus": "pending",
  ...
}
```

**Expected Response:** 201 Created (normalizes to "PENDING")

---

### 6. Database Verification

After creating a job, verify in database:

```sql
-- MongoDB
db.jobs.findOne({ company: "Test Company Inc" })

-- Expected result should show:
{
  ...
  "jobStatus": "PENDING",      // Enum value
  "jobType": "FULL_TIME",      // Enum value
  ...
}
```

---

## Integration Tests

### Test Case 1: Create Job → Read Job → Verify Data

```bash
# 1. Create job
POST /api/v1/jobs
{...valid payload...}

# Save the job ID from response

# 2. Read job by ID
GET /api/v1/jobs/{jobId}

# 3. Verify enum values are correct
- jobStatus should be "PENDING" (uppercase enum)
- jobType should be "FULL_TIME" (uppercase enum)
- NOT "pending" or "full-time" (lowercase strings)
```

---

### Test Case 2: Update Job with Different Status

```bash
# 1. Update status to INTERVIEW
PATCH /api/v1/jobs/{jobId}/status
{
  "jobStatus": "INTERVIEW"
}

# 2. Verify update
GET /api/v1/jobs/{jobId}

# 3. Confirm jobStatus is now "INTERVIEW"
```

---

### Test Case 3: Edit Job (PATCH request)

```bash
# Send mixed case values to test normalization
PATCH /api/v1/jobs/{jobId}
{
  "jobStatus": "pending",      // lowercase
  "jobType": "FULL-TIME",      // uppercase
  ...
}

# Verify backend normalizes correctly
```

---

## Frontend Integration Tests

### Test 1: Form Validation Display

- [ ] Open Add Job page as recruiter
- [ ] Try submitting without adding skills
- [ ] Verify error popup appears immediately
- [ ] Error message: "Please add at least one skill"
- [ ] No API request should be made

### Test 2: Form Validation for Facilities

- [ ] Open Add Job page as recruiter
- [ ] Add skills but don't add facilities
- [ ] Try submitting
- [ ] Verify error popup appears immediately
- [ ] Error message: "Please add at least one facility"
- [ ] No API request should be made

### Test 3: Successful Submission

- [ ] Open Add Job page as recruiter
- [ ] Fill in all fields with valid values
- [ ] Add at least one skill
- [ ] Add at least one facility
- [ ] Submit form
- [ ] Verify success message appears
- [ ] Check that job was created in database
- [ ] Verify job appears in "My Jobs" page

### Test 4: Enum Values Sent Correctly

- [ ] Open browser DevTools → Network tab
- [ ] Submit job form
- [ ] Click on the POST /api/v1/jobs request
- [ ] View request payload in "Request" tab
- [ ] Verify:
  - jobStatus is in UPPERCASE (e.g., "PENDING")
  - jobType is in lowercase (e.g., "full-time")

---

## Production Deployment Steps

### Step 1: Backup Current Code
```bash
cd /home/tushar/project/Job_portal
git add -A
git commit -m "Backup before validation fix deployment"
```

### Step 2: Verify All Changes
```bash
git status
# Should show the 3 modified files
```

### Step 3: Build Backend
```bash
cd job-portal-backend
mvn clean package -DskipTests
```

**Expected:** Build succeeds, JAR created in `target/`

### Step 4: Build Frontend
```bash
cd ../full-stack-job-portal-client-main
npm install
npm run build
```

**Expected:** Build succeeds, output in `dist/` or `build/`

### Step 5: Deploy with Docker
```bash
cd ..
docker-compose down
docker-compose up -d --build
```

**Expected:** Both services start successfully

### Step 6: Health Check
```bash
# Check backend
curl http://localhost:8080/api/v1/health

# Check frontend
curl http://localhost:3000
```

**Expected:** Both respond successfully

### Step 7: Smoke Tests
- [ ] Login as recruiter
- [ ] Navigate to "Add Job" page
- [ ] Submit valid job → Success
- [ ] Submit invalid job → Error shown
- [ ] Check "My Jobs" page → Job appears

---

## Rollback Plan

If something goes wrong:

### Quick Rollback (Database Only)
If the issue is only with data, keep code but clean database:
```bash
# Connect to MongoDB
mongo

# Drop and recreate collections if needed
db.jobs.drop()
```

### Full Rollback (Code)
If code has issues:

```bash
cd /home/tushar/project/Job_portal

# Revert to previous commit
git revert HEAD

# Rebuild
docker-compose down
docker-compose up -d --build
```

---

## Monitoring Checklist

### Day 1 After Deployment
- [ ] Check error logs for validation exceptions
- [ ] Check success logs for "Job created successfully"
- [ ] Monitor API response times (should be unchanged)
- [ ] Check database for new job records

### Day 1-3 Monitoring
- [ ] Track failed job creation attempts
- [ ] Monitor validation error patterns
- [ ] Check for unexpected error messages
- [ ] Verify job records have correct enum values

### Week 1 Monitoring
- [ ] Compare job creation success rate before/after
- [ ] Check for any regression in other API endpoints
- [ ] Monitor user feedback
- [ ] Verify all enum values in database are correct

---

## Success Criteria

✅ **Fix is successful if:**

1. Valid job submissions return 201 Created
2. Invalid jobStatus values return 400 with clear message
3. Invalid jobType values return 400 with clear message
4. Empty skills/facilities prevented at frontend
5. Case-insensitive jobStatus handling works (pending → PENDING)
6. Database shows correct enum values (PENDING, FULL_TIME, etc.)
7. No errors in application logs
8. Response times unchanged
9. Other endpoints not affected

---

## Documentation Updates

After deployment, update:
- [ ] README.md with fix summary
- [ ] API documentation with validation rules
- [ ] Team Slack/Discord with deployment notification

---

## Post-Deployment Documentation

Keep these docs for reference:
- [x] QUICK_REFERENCE.md
- [x] COMPLETE_FIX_DOCUMENTATION.md
- [x] IMPLEMENTATION_SUMMARY.md
- [x] TESTING_GUIDE_CURL.md
- [x] VALIDATION_FIX_SUMMARY.md

---

## Support Contacts

If issues arise:
- [ ] Check logs: `docker-compose logs job-portal-backend`
- [ ] Check frontend console: Browser DevTools → Console tab
- [ ] Review cURL tests from TESTING_GUIDE_CURL.md
- [ ] Verify database data is correct

---

## Sign-Off

- **Deployed by:** _______________
- **Date:** _______________
- **Test status:** ✅ Passed / ❌ Failed
- **Notes:** _______________

---

**Document Version:** 1.0
**Created:** January 27, 2026
**Last Updated:** January 27, 2026

