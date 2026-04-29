# 🏦 Bank Cards Management System

A secure, role-based REST API backend for managing bank cards, users, and transactions — built with **Spring Boot** and **JWT authentication**.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [API Reference](#api-reference)
- [Security](#security)
- [Data Models](#data-models)
- [Error Handling](#error-handling)

---

## Overview

This backend application provides a full-featured bank card management system with two distinct roles:

- **USER** — can view their own cards, request card blocks, transfer funds between their own cards, and view transaction history.
- **ADMIN** — can create cards, block/activate cards and users, search with filters, view audit logs, and manage all user accounts.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (JJWT) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Validation | Jakarta Bean Validation |
| Build Tool | Gradle |

---

## Project Structure

```
src/main/java/com/backend/bankcards/
├── config/
│   ├── CorsConfig.java                    # CORS configuration
│   ├── JwtFilter.java                     # JWT authentication filter
│   └── SecurityConfig.java                # Spring Security setup
│
├── controller/
│   ├── adminController/
│   │   ├── CardManageController.java      # Admin card management
│   │   └── UserManageController.java      # Admin user management
│   └── userController/
│       ├── AuthController.java            # Register & Login
│       ├── UserController.java            # User profile
│       └── UserCardController.java        # User card operations

├── dto/
│   ├── authDTO/
│   │   ├── AuthResponse.java      
│   │   ├── LoginRequest.java     
│   │   └── RegisterRequest.java
│   └── cardsDTO/
│   │   ├── CardCreateRequestDTO.java
│   │   ├── CardResponseDTO.java
│   │   └── CardSearchFilter.java
│   └── transactionDTO/
│   │   ├── TransactioResponseDTO.java
│   │   └── TransferRequest.java
│   └── usersDTO/
│       ├── ChangePasswordRequest.java
│       ├── UserResponseDTO.java
│       ├── UserSearchFilter.java
│       └── UserUpadateDTO.java
│
├── entity/
│   ├── UserEntity.java
│   ├── Card.java
│   ├── TransactionEntity.java
│   └── AuditLog.java
│
├── enums/
│   ├── CardCategory.java
│   ├── CardStatus.java
│   ├── CardType.java
│   └── Role.java
│
├── repository/
│   ├── AuditLogRepository.java
│   ├── CardRepository.java
│   ├── TransactionRepository.java
│   └── UserRepository.java
│
└── exception/
|    ├── GlobalExceptionHandler.java
|    ├── ResourceNotFoundException.java
|    ├── InsufficientFundsException.java
|    ├── CustomAccessDeniedHandler.java     # 403 handler
│    └── CustomAuthenticationEntryPoint.java # 401 handler
|
|
└── security/
|    ├── CustomeUserDetailService.java
|    ├── EcryptionUtil.java
|    └── JwtUtil.java
|
|
├── service/
│   ├── adminService/
│   │   ├── CardManageService.java      
│   │   ├── CardManageServiceImpl.java     
│   │   ├── UserManageService.java
│   │   └── UserManageServiceImpl.java
│   └── userController/
│       ├── AuthService.java
│       ├── AuthServiceImpl.java
│       ├── UserCardService.java       
│       ├── UserCardServiceImpl.java
│       ├── UserService.java 
│       └── UserServiceImpl.java
│
│
├── BankcardsApplication.java
  
```

---

## Getting Started

### Prerequisites

- Java 25+
- PostgreSQL 15+
- Gradle

### 1. Clone the repository

```bash
git clone https://github.com/Xudoyyor/Bank_REST.git
cd bank-cards-backend
```


### 2. Configure `application.properties`

```properties
spring.application.name=bankcards
spring.datasource.url=jdbc:postgresql://localhost:5442/bank-rest_db
spring.datasource.username=hudoyor
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

jwt.secret=1234567890123456
jwt.expiration=86400000
```

### 3. Build and run

```bash
# Build the jar and start all services
docker-compose up --build
```

The server will start at `http://localhost:8085`.

### 4. Access Swagger UI

```
http://localhost:8085/swagger-ui/index.html
```

> Swagger UI is publicly accessible — no authentication required.

---

## API Reference

### 🔓 Auth — `/api/auth`

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and receive JWT token | No |

**Register request body:**
```json
{
  "username": "johndoe",
  "password": "securepass",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "+998901234567"
}
```

**Login response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "johndoe",
  "role": "ROLE_USER"
}
```

---

### 👤 User Profile — `/api/user/profile`

> Requires: `Bearer <token>` header

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/user/profile` | Get own profile |
| PUT | `/api/user/profile` | Update own profile |
| PUT | `/api/user/profile/password` | Change password |

---

### 💳 User Cards — `/api/user/cards`

> Requires: `Bearer <token>` header

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/user/cards/{id}` | Get a specific card by ID |
| GET | `/api/user/cards/{id}/balance` | Get card balance |
| PATCH | `/api/user/cards/{id}/block-requested` | Request card block |
| POST | `/api/user/cards/transfer` | Transfer between own cards |
| GET | `/api/user/cards/transactions` | All personal transactions |
| GET | `/api/user/cards/transactions/{id}` | Transaction details |
| GET | `/api/user/cards/{id}/transactions` | Transactions for a specific card |

**Transfer request body:**
```json
{
  "fromCardId": 1,
  "toCardId": 2,
  "amount": 250.00
}
```

---

### 🛡️ Admin — Users — `/api/admin/users`

> Requires: `Bearer <token>` + `ROLE_ADMIN`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users/{userId}` | Get user by ID |
| PUT | `/api/admin/users/{userId}` | Update user |
| DELETE | `/api/admin/users/{userId}` | Soft-delete user |
| PATCH | `/api/admin/users/{userId}/block` | Block user |
| PATCH | `/api/admin/users/{userId}/activate` | Activate user |
| GET | `/api/admin/users/{userId}/audit` | Get user audit history |
| GET | `/api/admin/users/{id}/cards` | Get all cards for a user |
| GET | `/api/admin/users/search` | Search users with filters |

**User search query params:**

| Param | Type | Description |
|---|---|---|
| `query` | string | Search by name or username |
| `email` | string | Filter by email |
| `phone` | string | Filter by phone |
| `role` | enum | `ROLE_USER` or `ROLE_ADMIN` |
| `isActive` | boolean | Filter by active status |
| `page` | int | Page number (0-based) |
| `size` | int | Page size |

---

### 🛡️ Admin — Cards — `/api/admin/cards`

> Requires: `Bearer <token>` + `ROLE_ADMIN`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/admin/cards/create` | Create a new card |
| PATCH | `/api/admin/cards/{id}/block` | Block a card |
| PATCH | `/api/admin/cards/{id}/activate` | Activate a card |
| GET | `/api/admin/cards/{id}/history` | Get card audit history |
| GET | `/api/admin/cards/top-balance` | Get top N cards by balance |
| GET | `/api/admin/cards/search` | Search cards with filters |

**Create card request body:**
```json
{
  "userId": 1,
  "cardType": "GOLD",
  "category": "VISA",
  "bankName": "Kapital Bank"
}
```

**Card search query params:**

| Param | Type | Description |
|---|---|---|
| `cardHolderName` | string | Filter by owner name |
| `bankName` | string | Filter by bank name |
| `status` | enum | `ACTIVE`, `BLOCKED`, `EXPIRED`, `BLOCK_REQUESTED` |
| `cardType` | enum | `STANDARD`, `PLATINUM`, `GOLD`, `VIRTUAL` |
| `cardCategory` | enum | `VISA`, `MASTERCARD`, `UZCARD`, `HUMO`, `MIR` |
| `page` | int | Page number |
| `size` | int | Page size |

---

## Security

Authentication is handled via **JWT (JSON Web Tokens)**. After logging in, include the token in every protected request:

```
Authorization: Bearer <your_token_here>
```

**Access control:**

| Path | Access |
|---|---|
| `/api/auth/**` | Public |
| `/swagger-ui/**`, `/v3/api-docs/**` | Public |
| `/api/admin/**` | `ROLE_ADMIN` only |
| All other `/api/**` | Authenticated users |

Unauthorized requests return a structured `401` or `403` JSON response.

---

## Data Models

### Card

| Field | Type | Description |
|---|---|---|
| `cardNumber` | String | Full card number (stored encrypted) |
| `maskedNumber` | String | Display number e.g. `**** **** **** 1234` |
| `status` | Enum | `ACTIVE`, `BLOCKED`, `EXPIRED`, `BLOCK_REQUESTED` |
| `cardType` | Enum | `STANDARD`, `PLATINUM`, `GOLD`, `VIRTUAL` |
| `cardCategory` | Enum | `VISA`, `MASTERCARD`, `UZCARD`, `HUMO`, `MIR` |
| `balance` | BigDecimal | Current balance (non-negative) |
| `expirationDate` | LocalDate | Must be a future date |

### User

| Field | Type | Description |
|---|---|---|
| `username` | String | Unique login name |
| `role` | Enum | `ROLE_USER` or `ROLE_ADMIN` |
| `isActive` | Boolean | Account active status |
| `isBlocked` | Boolean | Whether admin has blocked the user |

### Transaction

| Field | Type | Description |
|---|---|---|
| `fromCard` | Card | Source card |
| `toCard` | Card | Destination card |
| `amount` | BigDecimal | Must be > 0 |
| `status` | String | `SUCCESS` or `FAILED` |

---

## Error Handling

All errors return a structured JSON response:

```json
{
  "timestamp": "2025-04-28T12:34:56",
  "status": 404,
  "error": "Not Found",
  "message": "Card with ID 99 not found"
}
```

| Exception | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 Not Found |
| `InsufficientFundsException` | 402 Payment Required |
| `IllegalStateException` | 400 Bad Request |
| `IllegalArgumentException` | 400 Bad Request |
| `AccessDeniedException` | 403 Forbidden |
| Validation errors | 400 Bad Request |
| All others | 500 Internal Server Error |

---

## 🧪 Testing

To run only unit tests (without database dependency):
```bash
./gradlew test --tests UserCardServiceImplTest

## 📝 Notes

- Card numbers are stored as hashed/masked values — the full number is never exposed in responses.
- User deletion is **soft delete** — records are deactivated, not removed from the database.
- All admin actions are recorded in the **audit log** for traceability.
- The `CORS` policy is currently open (`allowedOrigins("*")`) — restrict this in production.
