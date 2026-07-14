# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A fitness tracking application with a Spring Boot backend. The frontend does not exist yet — only the backend is implemented.

- **Backend**: Spring Boot 4.1.0, Java 25, Maven
- **Database**: PostgreSQL (local, `fitnesstracker` database on port 5432)
- **Base package**: `com.fitnesstracker.app`

## Commands

All commands run from the `backend/` directory.

```bash
# Build
./mvnw clean package

# Run (dev mode with devtools hot-reload)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=BackendApplicationTests

# Manual API test
bash requests/create-user.sh <username> <email> <password>
```

## Architecture

The backend follows a standard Spring layered architecture:

- **`model/`** — JPA entities with Jakarta Validation annotations. `User` maps to the `users` table.
- **`repository/`** — Spring Data JPA interfaces extending `JpaRepository`. Custom finders declared as method signatures (e.g., `findByEmail`).
- **`service/`** — Business logic. `UserService` handles password hashing via `BCryptPasswordEncoder` before persisting.
- **`controller/`** — `@RestController` classes exposing REST endpoints under `/api/`.
- **`exception/`** — `GlobalExceptionHandler` (`@RestControllerAdvice`) centralizes error responses. `EmailAlreadyTakenException` → 409, bean validation errors → 400 with field-level messages.
- **`config/SecurityConfig`** — Spring Security is present but all requests are currently `permitAll()` with CSRF disabled; authentication/authorization is not yet implemented.

## Key Conventions

- Lombok `@Getter`/`@Setter` on entities instead of explicit accessors.
- Validation constraints live on the entity class (not DTOs); `@Valid` in controllers triggers them.
- Passwords are always BCrypt-hashed in `UserService` before saving — never persist raw passwords.
- `spring.jpa.hibernate.ddl-auto=update` — schema is managed automatically by Hibernate in dev; there are no migration scripts yet.

## Database Setup

PostgreSQL must be running locally with:
- Database: `fitnesstracker`
- Username: `postgres`
- Password: (see local `application.properties`, not committed)
- Port: `5432`

## Rules for Claude Code

- Production-quality, self-documenting code. No comments except for unavoidable technical constraints.
- Hyper-descriptive names — `fetchUserDataFromPrimaryDatabase()` over `getData()`.
- Every function does exactly one thing (Single Responsibility Principle).
- Prefer early returns over nested conditionals.
- No magic numbers — named constants only.
- Never guess — if uncertain about a library version, API behavior, or Spring Boot 4 specifics, say so explicitly rather than assuming Spring Boot 3 conventions.
- When writing or modifying code, briefly explain *why*, not just *what* — I am learning, not just shipping.
- Follow existing architecture strictly: model/repository/service/controller/exception/config — do not collapse layers for convenience.
- Ask before adding new dependencies to pom.xml.
- **NEVER write Java code for me. Not snippets, not full files, not "here's an example" code blocks.** I write all Java myself, by hand.
- Your role is limited to: explaining concepts, reviewing code I've already written, pointing out bugs/issues without fixing them directly, answering "why" questions, and describing *what* needs to be built (requirements) — never *how* to write it in code form.
- If I ask you to "just write it" or similar, remind me of this rule instead of complying.
- You may run commands (build, test, git, docker) and read/analyze files, since that's mechanical, not learning.
- You may write non-Java config/infra files only if I explicitly ask (e.g., a specific line in `application.properties`, a `Dockerfile`) — always ask first, default to explaining what to add and why.
- When reviewing my code, point out issues and explain the reasoning, but let me write the fix myself.
