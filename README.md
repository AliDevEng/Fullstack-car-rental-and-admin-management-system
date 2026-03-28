# 🚗 NextCar: Fullstack Car Rental & Admin Management System

<div align="center">
🚘 Modern Car Rental Platform | 📊 Admin Dashboard | ☕ Java Spring Boot Backend
</div>

---

## ✨ Project Overview

NextCar is a comprehensive car rental platform with a dual-interface approach:

- **🌐 Customer-Facing Website**: A responsive, user-friendly interface for browsing vehicles and making reservations
- **⚙️ Admin Dashboard**: A robust management system for monitoring and managing the entire rental operation

The backend is built with **Java & Spring Boot** and is designed to share the same database schema and API contract as a parallel **C# & .NET** implementation — both backends serve the same **React / Next.js / TypeScript** frontend.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend A | ☕ Java 21, Spring Boot 3, Spring Security, JPA/Hibernate |
| Backend B | 🔷 C# .NET (parallel implementation — same schema) |
| Frontend | ⚛️ React, Next.js, TypeScript *(separate repo)* |
| Database | 🐬 MySQL 8.0 (Docker) |
| Auth | 🔐 JWT (HMAC-SHA256), BCrypt |
| Build | 🔧 Maven |

---

## 🗺️ Development Roadmap

### ✅ Phase 1 — Foundation
- [x] Clean architecture setup (MVC + layered package structure)
- [x] Database schema design (matches C# project — `CarsCategories`, `Admins`, `Customers`, `Cars`, `Rentals`, `Payments`)
- [x] Categories API
- [x] CORS configuration
- [x] MySQL via Docker (`mysql-carrental`, port `3307`)

### ✅ Phase 2 — Core API
- [x] Cars CRUD endpoints (Create, Read, Update, Delete)
- [x] Filtering, sorting, and pagination
- [x] Customer management endpoints
- [x] Admin dashboard statistics endpoint
- [x] Input validation (`@Valid`, `@NotBlank`, `@Min`, etc.)
- [x] Global error handling (`GlobalExceptionHandler`)
- [x] Proper HTTP status codes (201, 400, 404, 409, 500)

### ✅ Phase 3 — Authentication & Authorization
- [x] JWT implementation (HMAC-SHA256, configurable expiry via `application.properties`)
- [x] Customer login endpoint (`POST /auth/login`)
- [x] Admin login endpoint (`POST /auth/admin/login`)
- [x] Role-based access control (`ADMIN` / `CUSTOMER`)
- [x] Password hashing with BCrypt
- [x] Global 401 / 403 JSON error handling (no HTML redirects)

### ✅ Phase 4 — Booking System
- [x] Rental creation (`POST /rentals`)
- [x] Availability checking (PENDING/ACTIVE rentals block dates; CANCELLED/COMPLETED do not)
- [x] Booking management (view own, view all as admin, cancel)
- [x] Status tracking (`PENDING`, `ACTIVE`, `COMPLETED`, `CANCELLED`)

### 🔲 Phase 5 — Payments & Deployment
- [ ] Payment gateway integration
- [ ] Transaction management
- [ ] API documentation (Swagger / OpenAPI)
- [ ] Docker containerization (`docker-compose`)
- [ ] CI/CD pipeline

---

## 🗄️ Database Schema

The schema matches the C# project exactly — both backends can share the same database.

```
mr_rent
├── CarsCategories   — vehicle categories (Budget, Economy, SUV, Transport)
├── Admins           — admin accounts with Role + BCrypt password
├── Customers        — registered customers
├── Cars             — vehicle inventory (Status, CategoryId, Price ...)
├── Rentals          — bookings (BookingNumber, Status, PaymentId ...)
└── Payments         — transactions (RentalId, Status, TransactionId ...)
```

---

## 🎯 Key Features

### 🧑‍💼 For Customers
- 🚙 Browse cars with filtering by category, sorting by price, pagination
- 🔍 Check real-time availability by date range
- 👤 Register / login with JWT authentication
- 📅 Create and manage reservations

### 👩‍💻 For Administrators
- 📊 Dashboard with live stats (total cars, customers, rentals, revenue)
- 🚗 Full car inventory management (CRUD)
- 👥 Customer management
- 💰 Payment and booking tracking

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven
- Docker Desktop

### 1 — Start the database

```bash
docker run --name mysql-carrental \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=mr_rent \
  -p 3307:3306 \
  -d mysql:8.0
```

### 2 — Run the SQL schema

Open MySQL Workbench, connect to `localhost:3307` (user: `root`, password: `password`), then run:

```
car-rental/src/main/java/com/nextcar/carrental/guide/database-setup.sql
```

> Replace the `REPLACE_WITH_BCRYPT_HASH` placeholders for admin passwords before running.
> Use https://bcrypt-generator.com with Rounds = 10.

### 3 — Run the Spring Boot app

```bash
mvn spring-boot:run
```

API available at: `http://localhost:8080`

---

## 🏗️ Project Structure

```
car-rental/src/main/java/com/nextcar/carrental/
├── config/          — SecurityConfig (Spring Security, CORS, JWT filter)
├── controller/      — REST controllers (Cars, Customers, Auth, Admin ...)
├── dto/             — Request/Response data transfer objects
├── entity/          — JPA entities (Car, Customer, Rental, Payment ...)
├── exception/       — GlobalExceptionHandler
├── repository/      — Spring Data JPA repositories
├── security/        — JwtTokenUtil, JwtAuthenticationFilter
└── service/         — Business logic layer
```

---

## 🔗 API Endpoints (current)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/login` | Public | Customer login → JWT |
| `POST` | `/auth/admin/login` | Public | Admin login → JWT |
| `POST` | `/customers/register` | Public | Register new customer |
| `GET` | `/cars` | Public | List all cars (paginated) |
| `GET` | `/cars/available` | Public | Available cars by date range |
| `GET` | `/cars/{id}` | Public | Single car |
| `POST` | `/cars` | Admin | Create car |
| `PUT` | `/cars/{id}` | Admin | Update car |
| `DELETE` | `/cars/{id}` | Admin | Delete car |
| `GET` | `/customers` | Admin | List all customers |
| `GET` | `/customers/{id}` | Auth | Customer by ID |
| `PUT` | `/customers/{id}` | Auth | Update customer |
| `DELETE` | `/customers/{id}` | Admin | Delete customer |
| `GET` | `/admin/stats` | Admin | Dashboard statistics |
| `GET` | `/categories` | Public | List car categories |
| `POST` | `/rentals` | Auth | Create a rental booking |
| `GET` | `/rentals` | Admin | List all rentals |
| `GET` | `/rentals/my` | Auth | Authenticated customer's rentals |
| `GET` | `/rentals/{id}` | Auth | Single rental (owner or admin) |
| `PUT` | `/rentals/{id}/cancel` | Auth | Cancel a PENDING rental |
| `PUT` | `/rentals/{id}/status` | Admin | Update rental status |

---

## 🔮 Coming Next

- 📅 Full booking system with availability locking
- 💳 Payment gateway integration
- 📖 Swagger / OpenAPI documentation at `/swagger-ui`
- 🐳 `docker-compose` for one-command startup
- 🔄 CI/CD pipeline

---

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
