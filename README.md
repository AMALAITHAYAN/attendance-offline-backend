# Smart Attendance Backend (Spring Boot)

This project implements the backend phases for the **Offline Smart Attendance** workflow:

- Phase 1: Authentication + Role-based access (TEACHER / STUDENT)
- Phase 2: Session management (start/close/get)
- Phase 3: Attendance storage
- Phase 4: Offline sync validation
- Phase 5: Duplicate + security checks
- Phase 6: Reporting (optional)
- Phase 7: Utilities (SHA-256, token gen, haversine, time-window)

## Tech
- Spring Boot 3.2.x
- Spring Security (JWT)
- Spring Data JPA
- H2 (default) + MySQL driver included

## Run (quick demo)
1. Open `src/main/resources/application.properties`
2. Run:
   - `./mvnw spring-boot:run`
3. H2 console (optional): `http://localhost:8080/h2`

## API

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`

### Session (Teacher)
- `POST /api/session/start`  (ROLE_TEACHER)
- `PUT /api/session/{id}/close` (ROLE_TEACHER)
- `GET /api/session/{id}` (secret hidden)
- `GET /api/session/{id}/teacher-view?includeSecret=true` (ROLE_TEACHER)

### Offline Sync (Student/Teacher)
- `POST /api/offline-sync` (ROLE_STUDENT or ROLE_TEACHER)

### Attendance (Teacher)
- `GET /api/attendance/session/{sessionId}` (ROLE_TEACHER)

### Reports (Teacher)
- `GET /api/reports/session/{sessionId}/summary` (ROLE_TEACHER)

## Token + Proof rules (backend validation)
- `windowTime = floor(verifiedAtEpochSeconds / tokenWindowSeconds)`
- `token = SHA256(sessionId + ':' + windowTime + ':' + sessionSecret)`
- `proof = SHA256(studentId + ':' + sessionId + ':' + token + ':' + deviceId)`

## Sample JSON

### Register
```json
{ "name":"Teacher 1", "email":"teacher@test.com", "password":"pass1234", "role":"TEACHER" }
```

### Start session
```json
{
  "qrRefreshIntervalSeconds": 20,
  "tokenWindowSeconds": 20,
  "allowedRadiusMeters": 50,
  "durationMinutes": 10,
  "teacherLat": 11.0168,
  "teacherLng": 76.9558,
  "maxGpsAccuracyMeters": 50,
  "locationMaxAgeSeconds": 30
}
```

### Offline sync (array)
```json
[
  {
    "studentId": "S001",
    "sessionId": "SES-XXXXXXXXXX",
    "windowTime": 123456,
    "token": "...",
    "proof": "...",
    "confidenceScore": 85,
    "userAgent": "Mozilla/...",
    "screenResolution": "1080x2400",
    "deviceId": "dev-abc",
    "studentLat": 11.0169,
    "studentLng": 76.9557,
    "gpsAccuracyMeters": 12.0,
    "verifiedAt": "2026-02-17T19:00:00",
    "locationCapturedAt": "2026-02-17T19:00:00"
  }
]
```
