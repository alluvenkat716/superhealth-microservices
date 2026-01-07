# SuperHealth Microservices

## Overview
I built this project to demonstrate how authentication and authorization are handled in a
real-world Spring Boot microservices system using **OAuth2, OpenID Connect, and JWT**.

The focus of this project is clean security design, proper service separation, and
production-style configuration.

---

## What This Project Does
- Centralizes authentication using an OAuth2 Authorization Server
- Issues JWT access tokens after successful login
- Secures downstream services using token-based authentication
- Avoids hardcoded secrets by using environment variables
- Exposes only minimal public endpoints

---

## Services

### Auth Server
- Acts as the OAuth2 Authorization Server
- Handles user authentication
- Issues signed JWT access tokens
- Implements OAuth2 Authorization Code flow

**Runs on:** `http://localhost:8080`

---

### Claim Service
- Acts as a JWT-secured Resource Server
- Validates tokens issued by the Auth Server
- Protects claim-related APIs
- Exposes application health using Spring Boot Actuator

**Runs on:** `http://localhost:8081`

---

## Security Flow (High Level)
1. User authenticates via the Auth Server  
2. Auth Server issues a JWT access token  
3. Token is sent as a Bearer token to the Claim Service  
4. Claim Service validates the token before processing the request  

---

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Security
- OAuth2 / OpenID Connect
- JWT
- PostgreSQL
- Maven

---

## Running Locally

### Start Auth Server
```bash
export OAUTH_CLIENT_SECRET=secret
export AUTH_SERVER_ISSUER=http://localhost:8080
cd auth-server
./mvnw spring-boot:run
```

---

### Start Claim Service

```bash
export DB_PASSWORD=postgres
export AUTH_SERVER_ISSUER=http://localhost:8081
cd claim-service
./mvnw spring-boot:run
```

---

## Summary

This project reflects how I built this project as a way to practice and demonstrate how I design and secure backend microservices using Spring Boot,
with a strong emphasis on OAuth2 standards, JWT security, and clean configuration.

