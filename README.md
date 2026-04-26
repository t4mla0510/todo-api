# Todo API – Spring Boot RESTful Service

A production-ready Todo REST API built with Spring Boot, featuring JWT authentication, refresh tokens, rate limiting, and a containerized deployment using Docker + Nginx.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
- [Rate Limiting](#rate-limiting)
- [Security](#security)
- [Key Components](#key-components)

---

## Overview

This project provides a secure and scalable backend for managing todos. It includes:
- Stateless authentication using JWT
- Refresh token mechanism
- Per-user todo management
- Rate limiting at both application and reverse proxy levels
- Dockerized infrastructure with MySQL and Nginx

---

## Features

- RESTful API design
- JWT authentication (access + refresh tokens)
- User registration & login
- Todo CRUD operations
- Pagination and filtering
- Rate limiting with Bucket4j and Nginx
- Global exception handling
- Request validation (JSR-303)
- OpenAPI 3 (Swagger UI)
- Docker + Docker Compose setup
- Reverse proxy with Nginx

## Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                          CLIENT                                 │
│                   (Browser/App/Postman)                         │
└─────────────────────────────────────────────────────────────────┘
                            │
                            │ HTTP/HTTPS
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                  NGINX (Reverse Proxy)                          │
│                 Port 80 (HTTP) / 443 (HTTPS)                    │
│                Load balancing / Rate limiting                   │
└─────────────────────────────────────────────────────────────────┘
                            │
                            │ HTTP
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                       SPRING BOOT APP                           │
│                    (Todo API Application)                       │
│  ┌────────────────┐  ┌───────────────┐  ┌──────────────┐        │
│  │ AuthController │  │ TodoController│  │ Rate Limiter │        │
│  └────────────────┘  └───────────────┘  └──────────────┘        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │   JwtService │  │TodoService   │  │AuthService   │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
│  ┌──────────────┐  ┌────────────────┐                           │
│  │JwtAuthFilter │  │GlobalExcHandler│                           │
│  └──────────────┘  └────────────────┘                           │
└─────────────────────────────────────────────────────────────────┘
                            │
                            │ JDBC
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                          MySQL                                  │
│                     (Todo Database)                             │
│  ┌──────────┐ ┌──────────┐ ┌─────────────┐                      │
│  │  users   │ │  todos   │ │refresh_token│                      │
│  └──────────┘ └──────────┘ └─────────────┘                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Category        | Technology |
|----------------|-----------|
| Framework       | Spring Boot |
| Language        | Java 21 |
| Security        | Spring Security + JWT (jjwt) |
| Database        | MySQL 8 |
| ORM             | Spring Data JPA (Hibernate) |
| Build Tool      | Maven |
| API Docs        | OpenAPI 3 + Swagger UI |
| Validation      | Jakarta Validation |
| Rate Limiting   | Bucket4j + Nginx |
| DevOps          | Docker, Docker Compose, Nginx |
| Utilities       | Lombok |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/todoapi/
│   │   ├── config/              # Configuration classes
│   │   │   ├── RateLimiterConfig.java
│   │   │   ├── RateLimitFilter.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/          # REST API controllers
│   │   │   ├── AuthController.java
│   │   │   └── TodoController.java
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── AuthDto.java
│   │   │   └── TodoDto.java
│   │   ├── exception/           # Custom exceptions
│   │   │   ├── DuplicateResourceException.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── UnauthorizedException.java
│   │   ├── global/              # Global configuration
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── model/               # JPA entities
│   │   │   ├── RefreshToken.java
│   │   │   ├── Todo.java
│   │   │   └── User.java
│   │   ├── repository/          # JPA repositories
│   │   │   ├── RefreshTokenRepository.java
│   │   │   ├── TodoRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/            # Security-related components
│   │   │   ├── JwtAuthFilter.java
│   │   │   └── JwtService.java
│   │   ├── service/             # Business logic services
│   │   │   ├── AuthService.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── TodoService.java
│   │   └── TodoApiApplication.java
│   └── resources/
│       ├── application.properties

```

---

## Database Schema

### users
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK, Auto) | Primary key |
| username | VARCHAR(50, UNIQUE) | User's username |
| email | VARCHAR(100, UNIQUE) | User's email |
| password | VARCHAR(255) | Encrypted password |
| created_at | TIMESTAMP | Registration date |

### todos
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK, Auto) | Primary key |
| user_id | BIGINT (FK) | Owner user |
| title | VARCHAR(100) | Todo title |
| description | TEXT | Todo details |
| completed | BOOLEAN | Completion status |
| created_at | TIMESTAMP | Creation date |
| updated_at | TIMESTAMP | Last update |

### refresh_tokens
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK, Auto) | Primary key |
| user_id | BIGINT (FK) | Associated user |
| token | VARCHAR(255, UNIQUE) | Refresh token value |
| created_at | TIMESTAMP | Issue date |
| expires_at | TIMESTAMP | Expiration timestamp |

---

## Installation

### Prerequisites
- **For Docker**: Docker & Docker Compose
- **For Local**: Java 21+, Maven 3.8+, MySQL 8

### Environment Setup
1. Clone repository
    ```bash
    git clone https://github.com/t4mla0510/todo-api.git
    cd todo-api
    ```
2. Copy the example config from `.env.example` to your local `.env` file and adjust it for your setup:
    ```
    cp .env.example .env
    ```

---

### Run with Docker

```bash
docker compose up -d
```

#### Services

| Service         | Port | Description            |
|-----------------|------|------------------------|
| API (via Nginx) |  80  | Main entry point       |
| MySQL           | 3307 | Database               |
| App (internal)  | 8080 | Spring Boot (internal) |

#### Access Services
- API: http://localhost:80
- Swagger UI: http://localhost:80/swagger-ui/index.html

---

### Run Locally (Without Docker)

#### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8 (running on `localhost:3306`)

#### Setup MySQL Locally

1. Pull and run MySQL container:
```bash
docker pull mysql:8

docker run -d --name mysql-container -e MYSQL_ROOT_PASSWORD=123123 -e MYSQL_DATABASE=tododb -e MYSQL_USER=admin -e MYSQL_PASSWORD=123123 -p 3306:3306 mysql:8
```

2. Update `.env` file for local development:
```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/tododb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### Build and Run

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

### Access Services (Local)
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## API Documentation

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

---

## Authentication

### Login Flow
1. POST `/api/auth/login` → Returns access token + refresh tokens
2. Access token expires in 1h
3. Refresh token used to get new access token

### Token Usage
```
Authorization: Bearer <access_token>
```

---

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh-token` - Refresh access token

### Todos
- `GET /api/todos` - List all todos
- `GET /api/todos/{id}` - Get todo by ID
- `POST /api/todos` - Create todo
- `PUT /api/todos/{id}` - Update todo
- `DELETE /api/todos/{id}` - Delete todo

---

## Security

- JWT-based authentication
- Password encoding (BCrypt)
- SQL injection protection (JPA)
- XSS protection (Spring Security)
- CSRF protection enabled
- Rate limiting on all endpoints

---

## Key Components

| Component | Purpose |
|-----------|---------|
| JwtService | Token generation/validation |
| JwtAuthFilter | Request authentication |
| TodoService | Todo business logic |
| AuthService | Authentication logic |
| RateLimitFilter | Request throttling |
| GlobalExceptionHandler | Unified error response |

---

## Roadmap Backend Project
Link: https://roadmap.sh/projects/todo-list-api