# Todo API – Spring Boot RESTful Service

A production-ready Todo REST API built with Spring Boot, featuring JWT authentication, refresh tokens, rate limiting, and a containerized deployment using Docker + Nginx.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [API Endpoints](#api-endpoints)

## Overview

This project provides a secure and scalable backend for managing todos. It includes:
- Stateless authentication using JWT
- Refresh token mechanism
- Per-user todo management
- Rate limiting at both application and reverse proxy levels
- Dockerized infrastructure with MySQL and Nginx

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
- **For Docker**: Docker
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

### Run Locally (Without Docker)

#### Setup MySQL Locally

1. Pull and run MySQL container:
```bash
docker pull mysql:8

docker run -d --name mysql-container -p 3306:3306 \
      -e MYSQL_ROOT_PASSWORD=123123 \
      -e MYSQL_DATABASE=tododb \
      -e MYSQL_USER=admin \
      -e MYSQL_PASSWORD=123123 mysql:8
```

2. Update `.env` file for local development:
```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/tododb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

#### Run the Project

```bash
./mvnw clean compile spring-boot:run
```

#### Access Services (Local)
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

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

## Roadmap Backend Project
Link: https://roadmap.sh/projects/todo-list-api
