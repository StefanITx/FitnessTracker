# FitnessTracker — Project Methodology

This document defines the vision, architecture, data model, feature scope, and development roadmap for the FitnessTracker application. It exists to prevent scope drift, keep design decisions documented, and serve as a shared reference throughout development.

---

## 1. Project Vision

A **multi-user fitness tracking web application** where each user can log and monitor their workouts over time. The application is built as a learning project with a real production target: deployment on AWS.

The interface is structured around activity domains (Workouts, Nutrition, Body Metrics) but **only the Workout domain is actively implemented**. Other sections exist in the navigation as "coming soon" placeholders.

---

## 2. Tech Stack

| Layer | Technology | Status |
|---|---|---|
| Backend | Spring Boot 4.1.0 / Java 25 / Maven | In progress |
| Database | PostgreSQL 16 | In progress |
| Frontend | Vite + React (TypeScript) | Not started |
| Authentication | TBD (JWT likely) | Not started |
| Deployment | AWS | Future goal |

The project is organized as two independent sub-projects:

```
FitnessTrackerProject/
├── backend/    ← Spring Boot (Maven)
└── frontend/   ← Vite + React (not yet created)
```

---

## 3. High-Level System Architecture

```mermaid
flowchart TD
    subgraph Client["Client Layer"]
        Browser["Browser\n(Vite + React)"]
    end

    subgraph Server["Server Layer — AWS EC2 / ECS (future)"]
        API["Spring Boot REST API\n:8080"]
    end

    subgraph Data["Data Layer — AWS RDS (future)"]
        DB[("PostgreSQL\nfitnesstracker")]
    end

    Browser -- "HTTP/JSON REST" --> API
    API -- "JPA / Hibernate" --> DB
```

**Key principle:** The frontend and backend are completely decoupled. The frontend is a pure React SPA that communicates with the backend exclusively through REST API calls. No server-side rendering.

---

## 4. Data Model

### 4.1 Entity Overview

The workout domain is built around five core entities:

| Entity | Role |
|---|---|
| `User` | An account. Owns all data. |
| `WorkoutSession` | A single training day — e.g. "Monday July 14". Belongs to one User. |
| `Exercise` | A global template — "Bench Press", "Pull-up". Has an image and muscle group. Shared across all users. |
| `WorkoutExercise` | The bridge: "I did Bench Press during my Monday session". Holds ordering and rest time config. |
| `ExerciseSet` | A single set within a WorkoutExercise — reps, weight, completion status. |

### 4.2 Class Diagram

```mermaid
classDiagram
    class User {
        +Long id
        +String username
        +String email
        +String password
    }

    class WorkoutSession {
        +Long id
        +User user
        +LocalDate date
        +String notes
    }

    class Exercise {
        +Long id
        +String name
        +String muscleGroup
        +String description
        +String imageUrl
    }

    class WorkoutExercise {
        +Long id
        +WorkoutSession workoutSession
        +Exercise exercise
        +Integer restTimeSeconds
        +Integer orderIndex
    }

    class ExerciseSet {
        +Long id
        +WorkoutExercise workoutExercise
        +Integer setNumber
        +Integer reps
        +Double weight
        +Boolean isBodyweight
        +Boolean completed
    }

    User "1" --> "0..*" WorkoutSession : owns
    WorkoutSession "1" --> "1..*" WorkoutExercise : contains
    Exercise "1" --> "0..*" WorkoutExercise : referenced by
    WorkoutExercise "1" --> "1..*" ExerciseSet : has
```

### 4.3 Entity Relationship Diagram

```mermaid
erDiagram
    USER {
        bigint id PK
        varchar username
        varchar email
        varchar password
    }

    WORKOUT_SESSION {
        bigint id PK
        bigint user_id FK
        date date
        text notes
    }

    EXERCISE {
        bigint id PK
        varchar name
        varchar muscle_group
        text description
        varchar image_url
    }

    WORKOUT_EXERCISE {
        bigint id PK
        bigint workout_session_id FK
        bigint exercise_id FK
        int rest_time_seconds
        int order_index
    }

    EXERCISE_SET {
        bigint id PK
        bigint workout_exercise_id FK
        int set_number
        int reps
        numeric weight
        boolean is_bodyweight
        boolean completed
    }

    USER ||--o{ WORKOUT_SESSION : "owns"
    WORKOUT_SESSION ||--|{ WORKOUT_EXERCISE : "contains"
    EXERCISE ||--o{ WORKOUT_EXERCISE : "referenced by"
    WORKOUT_EXERCISE ||--|{ EXERCISE_SET : "has"
```

### 4.4 Set Model — Weighted vs Bodyweight

`ExerciseSet` supports both tracking styles using two fields together:

| Scenario | `weight` | `isBodyweight` |
|---|---|---|
| Weighted (e.g. Bench Press 80kg) | `80.0` | `false` |
| Pure bodyweight (e.g. Pull-up) | `null` | `true` |
| Weighted bodyweight (e.g. weighted dip +20kg) | `20.0` | `true` |

`weight` is nullable (`Double`, not `double`) — this is intentional and must be respected in validation logic.

---

## 5. Feature Scope

### 5.1 Workout Domain — In Scope

```mermaid
mindmap
  root((Workout Tracker))
    Exercise Library
      Name and description
      Muscle group category
      Machine image
    Workout Sessions
      Create a session for a date
      Add exercises to session
      Reorder exercises
    Set Tracking
      Log reps and weight per set
      Bodyweight flag
      Mark set as completed
    Rest Timer
      Configurable rest time per exercise
      Countdown timer in UI
      Audio or visual alert
    Progression View
      History per exercise
      Weight and rep trends over time
```

### 5.2 Navigation Domains — Out of Scope Now

These sections appear in the navigation but are not implemented. They exist only as visual placeholders.

| Domain | Status |
|---|---|
| Workouts | **Active** |
| Nutrition | Coming soon |
| Body Metrics | Coming soon |
| Sleep | Coming soon |

---

## 6. Planned REST API

All endpoints are prefixed `/api`. Authentication headers (once implemented) will be required on all routes except registration and login.

```mermaid
flowchart LR
    subgraph Auth["Auth"]
        A1["POST /api/auth/register"]
        A2["POST /api/auth/login"]
    end

    subgraph Users["Users"]
        U1["GET /api/users/me"]
    end

    subgraph Exercises["Exercise Library"]
        E1["GET /api/exercises"]
        E2["POST /api/exercises"]
        E3["GET /api/exercises/{id}"]
    end

    subgraph Sessions["Workout Sessions"]
        S1["GET /api/workouts"]
        S2["POST /api/workouts"]
        S3["GET /api/workouts/{id}"]
        S4["DELETE /api/workouts/{id}"]
    end

    subgraph WorkoutExercises["Workout Exercises"]
        WE1["POST /api/workouts/{id}/exercises"]
        WE2["DELETE /api/workout-exercises/{id}"]
        WE3["PATCH /api/workout-exercises/{id}/order"]
    end

    subgraph Sets["Sets"]
        ST1["POST /api/workout-exercises/{id}/sets"]
        ST2["PATCH /api/sets/{id}"]
        ST3["DELETE /api/sets/{id}"]
    end
```

---

## 7. Frontend Structure

### 7.1 Page Navigation Flow

```mermaid
flowchart TD
    Login["Login Page\n/login"]
    Register["Register Page\n/register"]
    Dashboard["Dashboard\n/dashboard"]

    Workouts["Workout Tracker\n/workouts — ACTIVE"]
    WorkoutDetail["Session Detail\n/workouts/:id"]

    Nutrition["Nutrition\n/nutrition — SOON"]
    Metrics["Body Metrics\n/metrics — SOON"]

    Login --> Dashboard
    Register --> Dashboard
    Dashboard --> Workouts
    Dashboard --> Nutrition
    Dashboard --> Metrics
    Workouts --> WorkoutDetail
```

### 7.2 Workout Session User Flow

This describes what a user does during a typical training session:

```mermaid
flowchart TD
    Start(["Open App"])
    NewSession["Create New Workout Session\n(today's date auto-filled)"]
    SearchExercise["Search / Browse Exercise Library"]
    AddExercise["Add Exercise to Session"]
    LogSet["Log a Set\n(reps + weight OR bodyweight)"]
    MarkComplete["Mark Set as Completed"]
    RestTimer["Rest Timer Starts\n(countdown in UI)"]
    MoreSets{More sets\nfor this exercise?}
    MoreExercises{Add another\nexercise?}
    Finish(["Session Saved"])

    Start --> NewSession
    NewSession --> SearchExercise
    SearchExercise --> AddExercise
    AddExercise --> LogSet
    LogSet --> MarkComplete
    MarkComplete --> RestTimer
    RestTimer --> MoreSets
    MoreSets -- Yes --> LogSet
    MoreSets -- No --> MoreExercises
    MoreExercises -- Yes --> SearchExercise
    MoreExercises -- No --> Finish
```

---

## 8. Development Roadmap

```mermaid
gantt
    title FitnessTracker — Development Phases
    dateFormat  YYYY-MM-DD
    axisFormat  Phase %s

    section Backend
    Phase 1 — User Auth (JWT)         :p1, 2026-07-14, 14d
    Phase 2 — Exercise Library CRUD   :p2, after p1, 7d
    Phase 3 — Workout Session CRUD    :p3, after p2, 7d
    Phase 4 — Set Tracking CRUD       :p4, after p3, 7d
    Phase 5 — Progression Queries     :p5, after p4, 7d

    section Frontend
    Phase 6 — Vite + React Setup      :p6, after p1, 5d
    Phase 7 — Auth Pages              :p7, after p6, 7d
    Phase 8 — Workout UI              :p8, after p4, 14d
    Phase 9 — Rest Timer Component    :p9, after p8, 5d
    Phase 10 — Progression Charts     :p10, after p5, 7d

    section Deployment
    Phase 11 — AWS Setup + Deploy     :p11, after p10, 14d
```

### Phase Descriptions

| Phase | Goal | Key Deliverable |
|---|---|---|
| 1 | User Auth | JWT login/register, Spring Security filter chain |
| 2 | Exercise Library | Exercise CRUD with image URL, muscle group |
| 3 | Workout Sessions | Create/list/delete sessions scoped to logged-in user |
| 4 | Set Tracking | Full CRUD for WorkoutExercise + ExerciseSet |
| 5 | Progression | Queries returning history per exercise for a user |
| 6 | Frontend Bootstrap | Vite + React project, routing, API client setup |
| 7 | Auth UI | Login and register pages wired to backend |
| 8 | Workout UI | Full workout tracking page |
| 9 | Rest Timer | Frontend-only countdown component |
| 10 | Charts | Progression visualization (weight/reps over time) |
| 11 | AWS | EC2/ECS + RDS deployment, environment config |

---

## 9. Architecture Decisions Log

| Decision | Choice | Reason |
|---|---|---|
| Frontend/backend coupling | Fully decoupled (REST) | Enables independent deployment on AWS; cleaner separation of concerns |
| Set model | Single entity with `isBodyweight` flag + nullable `weight` | Avoids a separate table for a minor variant; simpler queries |
| Exercise images | `imageUrl` string on `Exercise` | Images are stored externally (S3 in future); DB stores only the reference |
| Schema management | Hibernate `ddl-auto=update` | Sufficient for development; will migrate to Flyway or Liquibase before AWS deployment |
| Auth | Not yet decided | Deliberately deferred — one phase at a time |

### Auth process

```mermaid
sequenceDiagram
    actor Client
    participant AuthController
    participant AuthService
    participant UserService
    participant JwtUtils
    participant DB

    Client->>AuthController: POST /api/auth/login {email, password}
    AuthController->>AuthService: login(LoginRequest)
    AuthService->>DB: find user by email
    DB-->>AuthService: User
    AuthService->>AuthService: verify password hash
    AuthService->>JwtUtils: generateToken(email)
    JwtUtils-->>AuthService: JWT string
    AuthService-->>AuthController: AuthResponse(token)
    AuthController-->>Client: 200 OK {token: "eyJ..."}

    Client->>JwtAuthFilter: GET /api/workouts\nAuthorization: Bearer eyJ...
    JwtAuthFilter->>JwtUtils: validateToken(token)
    JwtUtils-->>JwtAuthFilter: email = "stefan@..."
    JwtAuthFilter->>DB: load user by email
    JwtAuthFilter->>SecurityContext: set authenticated user
    JwtAuthFilter->>WorkoutController: request passes through
    WorkoutController-->>Client: 200 OK [workouts]