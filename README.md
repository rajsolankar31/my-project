# SaleMate — Product Intelligence Platform

A software-only product research and comparison platform that transforms raw, multi-source e-commerce data into structured, transparent shopping intelligence.

> **Status:** Phase 1, Phase 2 & Phase 3 Complete  
> **Backend:** Java 21/26 + Spring Boot 3 + JPA / Hibernate + OpenAPI (Swagger)  
> **Frontend:** React 18 + TypeScript + Vite + Lucide Icons  
> **Database:** PostgreSQL (Production) / In-memory H2 (Zero-dependency local dev profile)

---

## 1. Core Principles
1. **Decision Support, Not Decision Maker:** The platform presents empirical observations, data freshness, source attribution, and structured trade-offs. It never claims absolute physical authenticity from text reviews alone or declares a product universally "best".
2. **Modular Monolith First:** Built with clean domain boundaries in Spring Boot (`category`, `product`, `source`, `listing`, `price`, `review`, `matching`, `recommendation`).
3. **Historical Data Lineage:** Price observations are timestamped and preserved immutably. Never overwrite historical records.
4. **Deterministic Logic Over Opaque AI:** Structured schemas and rule-based filters precede heuristic or ML models.

---

## 2. Capabilities Implemented

### 2.1 Multi-Store Price Comparison (Phase 2)
- Unified store observation table across **Amazon India**, **Flipkart**, **Croma**, and **Reliance Digital**.
- Tracks **Seller Name**, **Stock Status**, **Shipping Charges**, and **Data Freshness** (e.g. *observed 2h ago*).
- Highlights the **Lowest Observed Store Price** with instant direct store links.

### 2.2 Historical Price Intelligence Timeline (Phase 3)
- **Interactive SVG Timeline Chart** mapping price drops, holiday sales, and price surges over time.
- **Derived Metrics Bar**:
  - Current Observed Price
  - All-Time Lowest Observed Price
  - Average Observed Price
  - Highest Observed Historical Peak
  - Relative Position Badge (e.g. `Below Average (-9.6%)` or `All-Time Low`)
- Interactive hover tooltips displaying date, price, store, and seller.
- Time range filters (`30 Days`, `60 Days`, `All Time`).

### 2.3 Instant Store Link Resolution (User Flow B)
- Paste any supported product link directly into the platform:
  - Amazon India (`https://www.amazon.in/dp/...`)
  - Flipkart (`https://www.flipkart.com/.../p/...`)
  - Croma (`https://www.croma.com/p/...`)
- The system parses the URL, identifies the source, extracts ASIN/PID/SKU, and matches it directly to the canonical product profile with all store comparison listings and price timelines.

---

## 3. Quick Start

### 3.1 1-Click Launch (Windows)
Double-click [`start.bat`](start.bat) or run from Command Prompt:
```cmd
start.bat
```

### 3.2 Manual Launch (Two CMD Windows)

#### Window 1: Start Backend (Spring Boot)
```cmd
cd backend
mvnw.cmd spring-boot:run
```
- **API Base:** `http://localhost:8080/api`
- **Interactive Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Schema:** `http://localhost:8080/api-docs`
- **H2 Database Console:** `http://localhost:8080/h2-console`

#### Window 2: Start Frontend (React + Vite)
```cmd
cd frontend
npm run dev
```
Visit **`http://localhost:5173`** in your browser.

---

## 4. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/products/search?q={query}` | Search canonical catalog by keyword |
| `GET` | `/api/products/{id}` | Retrieve canonical product with variants & specifications |
| `GET` | `/api/products/{id}/prices` | Get historical timeline series, stats summary, and store listings |
| `GET` | `/api/products/{id}/listings` | Retrieve multi-store listings with seller & stock status |
| `POST` | `/api/products/resolve` | Resolve a supported product URL into its canonical intelligence page |
| `GET` | `/api/health` | System health status |

---

## 5. Running Automated Tests

```cmd
# Run backend Spring Boot integration tests (9 tests covering search, prices, URL resolution, 404s, health)
cd backend
mvnw.cmd test

# Run frontend TypeScript type checking and production build
cd frontend
npm run build
```
