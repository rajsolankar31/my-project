# Development Guide

## Prerequisites
- **Java:** JDK 21 or later (compatible with JDK 26)
- **Node.js:** Node 20+ with npm
- **Docker & Docker Compose** (optional for local PostgreSQL & Redis)

## Getting Started

### 1. Database & Infrastructure
By default, the backend runs with the `local` profile, which automatically spins up an in-memory **H2 database** and seeds it with canonical product data. No external database installation is required for instant local testing!

To run with PostgreSQL and Redis (Production profile):
```bash
docker compose up -d postgres redis
```

### 2. Backend (Spring Boot)
From the `backend` directory:

```bash
# Run unit and integration tests
./mvnw clean test

# Start the Spring Boot application
./mvnw spring-boot:run
```
The server starts at `http://localhost:8080`.
- **API Documentation (Swagger UI):** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`
- **H2 Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:productintel`, user: `sa`, no password)

To run with PostgreSQL:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### 3. Frontend (React + Vite + TypeScript)
From the `frontend` directory:

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build production bundle
npm run build
```
The frontend dev server starts at `http://localhost:5173`.

### 4. Running the Complete System
1. Terminal 1: Run the backend
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
2. Terminal 2: Run the frontend
   ```bash
   cd frontend
   npm run dev
   ```
3. Open `http://localhost:5173` in your browser.
