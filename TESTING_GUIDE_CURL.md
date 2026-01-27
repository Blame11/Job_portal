# Testing Guide - POST /api/v1/jobs with cURL Examples

## Prerequisites

```bash
# Get your authentication token/cookie from login
TOKEN="your-auth-token-or-cookie"
BASE_URL="http://localhost:8080"
```

---

## Test 1: Valid Complete Job Submission ✅

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Technology Solutions",
    "position": "Senior Full Stack Engineer",
    "jobStatus": "PENDING",
    "jobType": "full-time",
    "jobLocation": "San Francisco, CA",
    "jobVacancy": "3",
    "jobSalary": "$150,000 - $180,000",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "We are looking for experienced full-stack engineers with 5+ years of experience. You will work with our team to build scalable web applications using React and Node.js.",
    "jobSkills": ["React", "Node.js", "MongoDB", "PostgreSQL", "AWS", "Docker"],
    "jobFacilities": ["Remote Work", "Health Insurance", "401k", "Flexible Hours", "Professional Development Budget"],
    "jobContact": "+1-415-555-0123 | careers@acmetech.com"
  }'
```

### Expected Response (201 Created)
```json
{
  "status": true,
  "result": {
    "id": "507f1f77bcf86cd799439011",
    "company": "Acme Technology Solutions",
    "position": "Senior Full Stack Engineer",
    "jobStatus": "PENDING",
    "jobType": "FULL_TIME",
    "jobLocation": "San Francisco, CA",
    "jobVacancy": "3",
    "jobSalary": "$150,000 - $180,000",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "We are looking for experienced full-stack engineers with 5+ years of experience. You will work with our team to build scalable web applications using React and Node.js.",
    "jobSkills": ["React", "Node.js", "MongoDB", "PostgreSQL", "AWS", "Docker"],
    "jobFacilities": ["Remote Work", "Health Insurance", "401k", "Flexible Hours", "Professional Development Budget"],
    "jobContact": "+1-415-555-0123 | careers@acmetech.com",
    "createdBy": "recruiter-id-12345",
    "createdAt": "2026-01-27T15:30:00Z",
    "updatedAt": "2026-01-27T15:30:00Z"
  },
  "message": "Job created successfully"
}
```

---

## Test 2: Lowercase jobStatus (Case-Insensitive) ✅

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Tech Innovation Corp",
    "position": "Backend Developer",
    "jobStatus": "pending",
    "jobType": "full-time",
    "jobLocation": "New York, NY",
    "jobVacancy": "5",
    "jobSalary": "$120,000 - $150,000",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Backend developer position for scalable API development.",
    "jobSkills": ["Java", "Spring Boot", "PostgreSQL"],
    "jobFacilities": ["Remote Work", "Health Insurance"],
    "jobContact": "jobs@techinnovation.com"
  }'
```

### Expected Response (201 Created)
```json
{
  "status": true,
  "result": {
    "id": "507f1f77bcf86cd799439012",
    "jobStatus": "PENDING",
    "jobType": "FULL_TIME",
    ...
  },
  "message": "Job created successfully"
}
```

**Note:** Even though we sent `"jobStatus": "pending"` (lowercase), backend normalizes it to `PENDING` enum.

---

## Test 3: Mixed Case jobStatus ✅

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "StartUp Labs Inc",
    "position": "Full Stack Developer",
    "jobStatus": "Pending",
    "jobType": "full-time",
    "jobLocation": "Austin, TX",
    "jobVacancy": "2",
    "jobSalary": "$100,000 - $130,000",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Full stack developer for exciting startup.",
    "jobSkills": ["React", "Python", "MongoDB"],
    "jobFacilities": ["Flexible Hours", "Remote Work"],
    "jobContact": "hr@startuplabs.com"
  }'
```

### Expected Response (201 Created)
```json
{
  "status": true,
  "result": {
    "jobStatus": "PENDING",
    ...
  },
  "message": "Job created successfully"
}
```

**Note:** `"Pending"` gets converted to `PENDING` by `.toUpperCase()`

---

## Test 4: Invalid jobStatus ❌

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Tech Corp",
    "position": "Engineer",
    "jobStatus": "active",
    "jobType": "full-time",
    "jobLocation": "Boston, MA",
    "jobVacancy": "3",
    "jobSalary": "$100k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Job description",
    "jobSkills": ["Java"],
    "jobFacilities": ["Remote"],
    "jobContact": "contact@tech.com"
  }'
```

### Expected Response (400 Bad Request)
```json
{
  "status": false,
  "message": "Validation failed",
  "result": [
    "Job status must be '\''PENDING'\'', '\''INTERVIEW'\'', or '\''DECLINED'\''"
  ]
}
```

**Error:** `"active"` is not a valid job status. Valid values: `PENDING`, `INTERVIEW`, `DECLINED`

---

## Test 5: Invalid jobType ❌

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Tech Corp",
    "position": "Engineer",
    "jobStatus": "PENDING",
    "jobType": "flexible",
    "jobLocation": "Boston, MA",
    "jobVacancy": "3",
    "jobSalary": "$100k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Job description",
    "jobSkills": ["Java"],
    "jobFacilities": ["Remote"],
    "jobContact": "contact@tech.com"
  }'
```

### Expected Response (400 Bad Request)
```json
{
  "status": false,
  "message": "Validation failed",
  "result": [
    "Job type must be 'full-time', 'part-time', or 'internship'"
  ]
}
```

**Error:** `"flexible"` is not a valid job type. Valid values: `full-time`, `part-time`, `internship`

---

## Test 6: Company Name Too Short ❌

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "ABC",
    "position": "Engineer",
    "jobStatus": "PENDING",
    "jobType": "full-time",
    "jobLocation": "Boston, MA",
    "jobVacancy": "3",
    "jobSalary": "$100k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Job description",
    "jobSkills": ["Java"],
    "jobFacilities": ["Remote"],
    "jobContact": "contact@tech.com"
  }'
```

### Expected Response (400 Bad Request)
```json
{
  "status": false,
  "message": "Validation failed",
  "result": [
    "Company must be between 5 and 100 characters"
  ]
}
```

**Error:** Company name `"ABC"` has only 3 characters. Minimum is 5.

---

## Test 7: Position Name Too Short ❌

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Tech",
    "position": "Dev",
    "jobStatus": "PENDING",
    "jobType": "full-time",
    "jobLocation": "Boston, MA",
    "jobVacancy": "3",
    "jobSalary": "$100k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Job description",
    "jobSkills": ["Java"],
    "jobFacilities": ["Remote"],
    "jobContact": "contact@tech.com"
  }'
```

### Expected Response (400 Bad Request)
```json
{
  "status": false,
  "message": "Validation failed",
  "result": [
    "Position must be between 3 and 100 characters"
  ]
}
```

**Error:** Position `"Dev"` has only 3 characters. But wait - minimum is 3, so this should pass!
Actually, the regex for position is `@Size(min = 3, ...)`, so "Dev" (3 chars) is valid.

Let's fix the test:

```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Tech",
    "position": "En",
    "jobStatus": "PENDING",
    "jobType": "full-time",
    "jobLocation": "Boston, MA",
    "jobVacancy": "3",
    "jobSalary": "$100k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Job description",
    "jobSkills": ["Java"],
    "jobFacilities": ["Remote"],
    "jobContact": "contact@tech.com"
  }'
```

### Expected Response (400 Bad Request)
```json
{
  "status": false,
  "message": "Validation failed",
  "result": [
    "Position must be between 3 and 100 characters"
  ]
}
```

**Error:** Position `"En"` has only 2 characters. Minimum is 3.

---

## Test 8: Empty jobSkills Array ❌ (Frontend Validation)

This test simulates bypassing frontend validation by sending an empty array.

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Tech",
    "position": "Senior Engineer",
    "jobStatus": "PENDING",
    "jobType": "full-time",
    "jobLocation": "Boston, MA",
    "jobVacancy": "3",
    "jobSalary": "$100k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Job description",
    "jobSkills": [],
    "jobFacilities": ["Remote"],
    "jobContact": "contact@tech.com"
  }'
```

### Expected Response (400 Bad Request)
```json
{
  "status": false,
  "message": "Validation failed",
  "result": [
    "Job skills are required"
  ]
}
```

**Note:** Frontend should prevent this, but backend catches it too.

---

## Test 9: Multiple Validation Errors ❌

### Command
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "ABC",
    "position": "En",
    "jobStatus": "invalid",
    "jobType": "flexible",
    "jobLocation": "Boston, MA",
    "jobVacancy": "3",
    "jobSalary": "$100k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Job description",
    "jobSkills": [],
    "jobFacilities": [],
    "jobContact": "contact@tech.com"
  }'
```

### Expected Response (400 Bad Request)
```json
{
  "status": false,
  "message": "Validation failed",
  "result": [
    "Company must be between 5 and 100 characters",
    "Position must be between 3 and 100 characters",
    "Job status must be 'PENDING', 'INTERVIEW', or 'DECLINED'",
    "Job type must be 'full-time', 'part-time', or 'internship'",
    "Job skills are required",
    "Job facilities are required"
  ]
}
```

---

## Test 10: All Valid jobStatus Values ✅

### PENDING Status
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Technology",
    "position": "Engineer",
    "jobStatus": "PENDING",
    ...
  }'
```

### INTERVIEW Status
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Technology",
    "position": "Engineer",
    "jobStatus": "INTERVIEW",
    ...
  }'
```

### DECLINED Status
```bash
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Technology",
    "position": "Engineer",
    "jobStatus": "DECLINED",
    ...
  }'
```

All return 201 Created.

---

## Test 11: All Valid jobType Values ✅

### full-time
```bash
"jobType": "full-time"
```

### part-time
```bash
"jobType": "part-time"
```

### internship
```bash
"jobType": "internship"
```

All return 201 Created.

---

## Quick Test Script

Save this as `test-job-api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
TOKEN="your-session-cookie"

echo "Testing POST /api/v1/jobs..."

# Test 1: Valid submission
echo -e "\n✅ Test 1: Valid submission"
curl -X POST "$BASE_URL/api/v1/jobs" \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Technology Solutions",
    "position": "Senior Engineer",
    "jobStatus": "PENDING",
    "jobType": "full-time",
    "jobLocation": "San Francisco, CA",
    "jobVacancy": "3",
    "jobSalary": "$150k-$180k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Senior engineer for backend systems.",
    "jobSkills": ["Java", "Spring Boot", "PostgreSQL"],
    "jobFacilities": ["Remote Work", "Health Insurance"],
    "jobContact": "+1-415-555-0123"
  }' | jq .

# Test 2: Invalid jobStatus
echo -e "\n❌ Test 2: Invalid jobStatus"
curl -X POST "$BASE_URL/api/v1/jobs" \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Technology Solutions",
    "position": "Senior Engineer",
    "jobStatus": "invalid",
    "jobType": "full-time",
    "jobLocation": "San Francisco, CA",
    "jobVacancy": "3",
    "jobSalary": "$150k-$180k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Senior engineer for backend systems.",
    "jobSkills": ["Java", "Spring Boot"],
    "jobFacilities": ["Remote Work"],
    "jobContact": "+1-415-555-0123"
  }' | jq .

# Test 3: Invalid jobType
echo -e "\n❌ Test 3: Invalid jobType"
curl -X POST "$BASE_URL/api/v1/jobs" \
  -H "Content-Type: application/json" \
  -H "Cookie: sessionId=$TOKEN" \
  -d '{
    "company": "Acme Technology Solutions",
    "position": "Senior Engineer",
    "jobStatus": "PENDING",
    "jobType": "flexible",
    "jobLocation": "San Francisco, CA",
    "jobVacancy": "3",
    "jobSalary": "$150k-$180k",
    "jobDeadline": "2026-03-27T23:59:59Z",
    "jobDescription": "Senior engineer for backend systems.",
    "jobSkills": ["Java", "Spring Boot"],
    "jobFacilities": ["Remote Work"],
    "jobContact": "+1-415-555-0123"
  }' | jq .

echo -e "\nTests complete!"
```

Run it:
```bash
chmod +x test-job-api.sh
./test-job-api.sh
```

---

## Authentication Note

Replace `"sessionId=$TOKEN"` with your actual authentication method:

**If using Bearer token:**
```bash
-H "Authorization: Bearer $TOKEN"
```

**If using session cookie:**
```bash
-H "Cookie: JSESSIONID=$TOKEN"
```

**If using custom cookie:**
```bash
-H "Cookie: authToken=$TOKEN"
```

Check your application's authentication mechanism to use the correct header.

