# Auth Server – OAuth2 Authorization Server

## Overview
This service is a centralized OAuth2 Authorization Server built using Spring Boot and Spring Authorization Server.  
It handles user authentication and issues JWT access tokens that are used to secure downstream microservices.

## Tech Stack
- Java 17  
- Spring Boot 3  
- Spring Security  
- Spring Authorization Server  
- OAuth 2.1 / OpenID Connect  
- JWT  
- Maven  

## OAuth2 Flow
This service implements the Authorization Code flow for browser-based applications.

1. The frontend redirects the user to `/oauth2/authorize`.
2. The user authenticates using the login page.
3. An authorization code is generated after successful login.
4. The frontend exchanges the code for an access token via `/oauth2/token`.
5. A signed JWT access token is returned.
6. The token is sent as a Bearer token when calling protected APIs.

## Public Endpoints
The following endpoints are publicly accessible:
- `/`
- `/health`
- `/.well-known/openid-configuration`
- `/oauth2/jwks`
- `/oauth2/authorize`
- `/oauth2/token`

## Protected Endpoints
All other endpoints require a valid Bearer token.  
Unauthorized requests return `401 Unauthorized`.

## Running Locally
```bash
export SPRING_PROFILES_ACTIVE=local
export OAUTH_CLIENT_SECRET=secret
./mvnw spring-boot:run
