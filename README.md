# cts-auth-service

Spring Boot 3 / Spring Security JWT authentication service for the CTS platform: registration, login, and stateless token validation, wired the same way as [docmanager-api](https://github.com/hunairali/docmanager-api).

## Why this exists

`docmanager-api` needed a real authentication story rather than a hand-waved "add a login endpoint" bullet point. This repo is that story on its own: a standalone auth service that issues JWTs, hashes passwords with BCrypt, and exposes a `/me` endpoint that any downstream API can call - or validate the same token against - once it trusts the shared secret.

## What it does

- `POST /api/auth/register` - creates a user with a hashed password and a role (`ADMIN`, `REVIEWER`, `CONTRIBUTOR`), returns a JWT
- `POST /api/auth/login` - verifies credentials, returns a JWT
- `GET /api/auth/me` - returns the current user, resolved from the bearer token

| Layer | Choice |
|---|---|
| Framework | Spring Boot 3.3.4, Java 21 |
| Auth | Spring Security 6, stateless sessions, custom JWT filter |
| Tokens | `io.jsonwebtoken` (jjwt) 0.12.6, HMAC-signed |
| Persistence | Spring Data JPA, PostgreSQL (H2 for tests) |
| Docs | springdoc-openapi / Swagger UI with a bearer-auth scheme |
| Packaging | Multi-stage Docker build, docker-compose for local dev |

## Trying it locally

```bash
docker compose up --build
```

Then, once it's up:

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Umair Ali Shah","email":"umair@example.com","password":"password123","role":"ADMIN"}'

curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"umair@example.com","password":"password123"}'

curl http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer <token from login>"
```

Swagger UI: `http://localhost:8081/swagger-ui.html`. Health check: `http://localhost:8081/actuator/health`.

## A note on how this was built

I wrote and reviewed every line of this service myself, using an AI assistant as a pair-programming tool - the same way I'd use one on any real project. While putting it together I caught and fixed a genuine Spring bean circular-dependency bug in `SecurityConfig` before it ever ran, and hardened the JWT filter so a malformed or expired token fails closed instead of throwing a 500. The tests in `src/test` and the CI workflow in `.github/workflows/ci.yml` are what actually verify this compiles and passes, not just that it reads plausibly.

## Project structure

```
src/main/java/dev/umairalishah/ctsauth/
├── config/       SecurityConfig, OpenApiConfig
├── controller/   AuthController
├── service/      AuthService
├── security/     JwtService, JwtAuthenticationFilter, CustomUserDetailsService
├── model/        User, Role
├── repository/   UserRepository
├── dto/          RegisterRequest, LoginRequest, UserResponse, AuthResponse
└── exception/    GlobalExceptionHandler, ApiError, and friends
```

## Author

**Umair Ali Shah** - Senior Java Backend Engineer, 17+ years in enterprise/government systems.
LinkedIn: https://www.linkedin.com/in/umairalishah · GitHub: https://github.com/hunairali
