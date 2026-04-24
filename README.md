# Todo API - Spring Boot REST API

A RESTful Todo API built with Spring Boot 4.0.6, featuring JWT authentication, MySQL database, and rate limiting.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)

## Features

- RESTful API design
- JWT (JSON Web Token) authentication
- User registration and login
- CRUD operations for todos
- Token refresh mechanism
- Rate limiting (60 requests/minute per IP)
- Custom exception handling
- Spring Validation
- OpenAPI 3.0 documentation (Swagger UI)
- Spring Security configuration
MySQL

## Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                    CLIENT (Postman/Browser)                          │
└──────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  SECURITY FILTER CHAIN                               │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐    │
│  │ RateLimitFilter  │  │ JwtAuthFilter    │  │ SecurityFilters  │    │
│  │ (Bucket4j)       │  │ (JWT validation) │  │ (Spring Sec)     │    │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘    │
└──────────────────────────────────────────────────────────────────────┘
                                     │
                     ┌───────────────┼───────────────┐
                     ▼               ▼               ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  CONTROLLERS (REST API)                              │
│  ┌──────────────────┐        ┌──────────────────┐                    │
│  │ AuthController   │        │ TodoController   │                    │
│  │ /api/register    │        │ /api/todos       │                    │
│  │ /api/login       │        │ /api/todos/{id}  │                    │
│  │ /api/refresh...  │        │ /api/todos/...   │                    │
│  └──────────────────┘        └──────────────────┘                    │
└──────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  SERVICE LAYER (Business Logic)                      │
│  ┌──────────────────┐        ┌──────────────────┐                    │
│  │ AuthService      │        │ TodoService      │                    │
│  │ - Authentication │        │ - CRUD Todo      │                    │
│  │ - Token Mgmt     │        │ - Authorization  │                    │
│  └──────────────────┘        └──────────────────┘                    │
└──────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  REPOSITORY LAYER (JPA/DAO)                          │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐    │
│  │ UserRepository   │  │ TodoRepository   │  │ RefreshTokenRepo │    │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘    │
└──────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────┐
│                  DATABASE LAYER                                      │
│  ┌───────────────────────────────────────────────────────────────┐   │
│  │                             MySQL                             │   │
│  └───────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────┘
```

### Request Flow

```
Client Request
      │
      ▼
┌──────────────┐
│ Rate Limit   │  ← Check IP-based quota (60 req/min)
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ JwtAuthFilter│  ← Extract & validate JWT from Authorization header
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ Load User    │  ← Load User by email from JWT
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ Controller   │  ← Handle request
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ Service      │  ← Business logic & authorization
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ Repository   │  ← Database queries
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Database    │  ← MySQL
└──────────────┘
```

## Technology Stack

| Category | Technology |
|----------|-----------|
| **Framework** | Spring Boot 4.0.6 |
| **Language** | Java 21 |
| **Security** | Spring Security + JWT (jjwt) |
| **Database** | MySQL 8.0 |
| **ORM** | Spring Data JPA + Hibernate |
| **Build Tool** | Maven |
| **API Doc** | OpenAPI 3.0 + Swagger UI |
| **Validation** | Spring Validation (JSR-303) |
| **Rate Limiting** | Bucket4j |
| **Code Quality** | Lombok |

## Project Structure

```
src/main/java/com/example/todoapi/
├── TodoApiApplication.java          # Main application entry point
│
├── config/                          # Configuration classes
│   ├── SecurityConfig.java          # Security configuration
│   ├── OpenApiConfig.java           # Swagger configuration
│   ├── RateLimiterConfig.java       # Rate limiting config
│   └── RateLimitFilter.java         # Rate limit middleware
│
├── controller/                      # REST Controllers
│   ├── AuthController.java          # Authentication endpoints
│   └── TodoController.java          # Todo endpoints
│
├── service/                         # Business logic
│   ├── AuthService.java             # Authentication service
│   ├── TodoService.java             # Todo service
│   └── CustomUserDetailsService.java # User details loader
│
├── repository/                      # JPA Repositories
│   ├── UserRepository.java
│   ├── TodoRepository.java
│   └── RefreshTokenRepository.java
│
├── model/                           # Database entities
│   ├── User.java
│   ├── Todo.java
│   └── RefreshToken.java
│
├── dto/                             # Data Transfer Objects
│   ├── AuthDto.java
│   └── TodoDto.java
│
├── security/                        # Security components
│   ├── JwtService.java              # JWT operations
│   └── JwtAuthFilter.java           # JWT filter
│
├── exception/                       # Custom exceptions
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── DuplicateResourceException.java
│
└── global/                          # Cross-cutting concerns
    └── GlobalExceptionHandler.java  # Exception handler
```

## Database Schema

### users
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| name | VARCHAR(255) | NOT NULL | User's full name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Username/Email |
| password | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| created_at | TIMESTAMP | NOT NULL | Account creation time |

### todos
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| title | VARCHAR(255) | NOT NULL | Todo title |
| description | TEXT | | Todo description |
| completed | BOOLEAN | NOT NULL | Completion status |
| user_id | BIGINT | FK → users.id | User owner |
| created_at | TIMESTAMP | NOT NULL | creation time |
| updated_at | TIMESTAMP | | Last update time |

### refresh_tokens
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT | Primary key |
| token | VARCHAR(255) | NOT NULL, UNIQUE | UUID refresh token |
| user_id | BIGINT | FK → users.id, UNIQUE | User owner |
| expiry_date | TIMESTAMP | NOT NULL | Token expiry time |

**Relationships:**
- `users` → `todos` (One-to-Many)
- `users` → `refresh_tokens` (One-to-One)

## Installation

### Prerequisites

- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd todo-api
   ```

2. **Create MySQL Database**
   ```sql
   CREATE DATABASE tododb;
   ```

3. **Configure Database Credentials**

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

6. **Access the Application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/v3/api-docs

For quick development, the project uses MySQL database.

## API Documentation

### Swagger UI

Access the interactive API documentation at:
- **URL**: http://localhost:8080/swagger-ui.html

### OpenAPI JSON

- **URL**: http://localhost:8080/v3/api-docs

## Authentication

### Token Types

| Token | Purpose | Expiry | Storage |
|-------|---------|--------|---------|
| **Access Token** | Authenticate API requests | 24 hours | JWT (stateless) |
| **Refresh Token** | Get new access token | 7 days | Database |

### Authentication Flow

```
┌─────────────┐                              ┌─────────────┐
│   Register  │                              │    Login    │
│ POST /api   │                              │ POST /api   │
└──────┬──────┘                              └──────┬──────┘
       │                                            │
       │ User created                               │ Credentials valid
       ▼                                            ▼
  ┌──────────┐                                  ┌──────────────┐
  │ Database │                                  │ Generate JWT │
  └──────────┘                                  │ Generate Ref │
                                                └──────┬───────┘
                                                       │
                                                       ▼
                                                 ┌──────────────┐
                                                 │ Return Tokens│
                                                 │ - access_tok │
                                                 │ - refresh_to │
                                                 └──────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                     Subsequent Requests                                 │
│  Client sends: Authorization: Bearer <access_token>                     │
│  Server validates JWT → Grants access                                   │
└─────────────────────────────────────────────────────────────────────────┘
```

### Get tokens (login)

```http
POST /api/login HTTP/1.1
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "uuid-here"
}
```

### Refresh token

```http
POST /api/refresh-token HTTP/1.1
Content-Type: application/json

{
  "refreshToken": "uuid-here"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "uuid-new"
}
```

### Use access token

Add the access token to the `Authorization` header:

```http
GET /api/todos HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/register` | Register a new user | No |
| POST | `/api/login` | Login and get tokens | No |
| POST | `/api/refresh-token` | Refresh access token | No |

### Todos (Protected)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/todos` | Get paginated list | Yes |
| GET | `/api/todos/{id}` | Get todo by ID | Yes |
| POST | `/api/todos` | Create new todo | Yes |
| PUT | `/api/todos/{id}` | Update todo | Yes |
| DELETE | `/api/todos/{id}` | Delete todo | Yes |
| PATCH | `/api/todos/{id}/complete` | Mark as complete | Yes |

### Request Examples

#### 1. Register User

```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

#### 2. Login

```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

#### 3. Get All Todos (with pagination)

```bash
curl -X GET http://localhost:8080/api/todos?page=0&size=10 \
  -H "Authorization: Bearer <your_access_token>"
```

#### 4. Create Todo

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Authorization: Bearer <your_access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot",
    "description": "Study Spring Boot 4.0 features"
  }'
```

#### 5. Update Todo

```bash
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Authorization: Bearer <your_access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot 4",
    "completed": true
  }'
```

#### 6. Delete Todo

```bash
curl -X DELETE http://localhost:8080/api/todos/1 \
  -H "Authorization: Bearer <your_access_token>"
```

#### 7. Mark Todo as Complete

```bash
curl -X PATCH http://localhost:8080/api/todos/1/complete \
  -H "Authorization: Bearer <your_access_token>"
```

## Security Configuration

- **JWT Secret**: Configured in `application.properties`
- **Session**: Stateless (no HTTP sessions)
- **CSRF**: Disabled (REST API)
- **Rate Limiting**: 60 requests/minute per IP
- **Password Encoding**: BCrypt

## Roadmap Backend Project
Link: https://roadmap.sh/projects/todo-list-api