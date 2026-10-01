# Database Schema & Entity Design

## Entity Relationship Overview

```text
Category
   │
   └──< Product
          │
          └──< ProductVariant
                    │
                    └──< Listing >── Source
                              │
                              ├──< PriceObservation
                              │
                              └──< Review
                                         │
                                         └── ReviewAnalysis
```

## Schema Entities (Phase 1 Focus)

### `categories`
- `id` (BIGSERIAL PRIMARY KEY)
- `name` (VARCHAR(100) NOT NULL UNIQUE)
- `slug` (VARCHAR(100) NOT NULL UNIQUE)
- `created_at` (TIMESTAMP NOT NULL)

### `products`
- `id` (BIGSERIAL PRIMARY KEY)
- `brand` (VARCHAR(100) NOT NULL)
- `canonical_name` (VARCHAR(255) NOT NULL)
- `model` (VARCHAR(100))
- `category_id` (BIGINT REFERENCES categories(id))
- `description` (TEXT)
- `created_at` (TIMESTAMP NOT NULL)
- `updated_at` (TIMESTAMP NOT NULL)

### `product_variants`
- `id` (BIGSERIAL PRIMARY KEY)
- `product_id` (BIGINT REFERENCES products(id) NOT NULL)
- `sku` (VARCHAR(100))
- `manufacturer_model` (VARCHAR(100))
- `color` (VARCHAR(50))
- `storage` (VARCHAR(50))
- `size` (VARCHAR(50))
- `attributes_json` (TEXT)
- `created_at` (TIMESTAMP NOT NULL)
- `updated_at` (TIMESTAMP NOT NULL)

### Extended Tables (Phase 2-4)
- `sources`
- `listings`
- `price_observations`
- `reviews`
- `review_analysis`
