# System Architecture

## Overview
The Product Intelligence Platform is a software-only product research and comparison platform. It normalizes product listings across multiple supported sources, observes timestamped prices, detects review patterns, and provides requirement-based alternatives.

## Core Principles
1. **Decision Support, Not Decision Maker:** Present empirical observations, data freshness, source attribution, and trade-offs. Never claim definitive authenticity or declare a product universally "best".
2. **Modular Monolith:** Built as a clean, cohesive Spring Boot modular monolith.
3. **Data Lineage:** Store raw observations with timestamps; never overwrite historical price observations.
4. **Deterministic Rules First:** Use rules, SQL, and normalization before introducing machine learning or AI models.

## High-Level Data Flow
```text
Supported Data Sources (Mock / API)
        ↓
Data Ingestion & Normalization
        ↓
Product / Variant Resolution (Canonical Catalog)
        ↓
Price & Review Intelligence
        ↓
Recommendation & Requirement Matcher
        ↓
REST API (DTO Boundaries)
        ↓
React / TypeScript UI
```

## Technology Stack
- **Backend:** Java (JDK 21/26) + Spring Boot 3
- **Database:** PostgreSQL (with H2 in-memory profile for zero-dependency local testing)
- **Frontend:** React 18 + TypeScript + Vite
- **Cache / Broker:** Redis (optional, introduced after core features)
- **Containerization:** Docker Compose for local PostgreSQL and Redis
