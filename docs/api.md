# REST API Specification

Base Path: `/api`

## Product Endpoints (Phase 1)

### 1. Search Products
`GET /api/products/search?q={query}`

**Query Parameters:**
- `q` (string, required): Search keyword (brand, model, category, canonical name).

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "brand": "Sony",
    "name": "Sony WH-1000XM5 Wireless Headphones",
    "model": "WH-1000XM5",
    "category": "Headphones",
    "description": "Industry leading noise canceling headphones with Auto NC Optimizer.",
    "variantCount": 2,
    "startingPrice": 26499.0
  }
]
```

### 2. Get Product Details
`GET /api/products/{id}`

**Path Parameters:**
- `id` (long, required): Canonical product ID.

**Response (200 OK):**
```json
{
  "id": 1,
  "brand": "Sony",
  "name": "Sony WH-1000XM5 Wireless Headphones",
  "model": "WH-1000XM5",
  "category": "Headphones",
  "description": "Industry leading noise canceling headphones with Auto NC Optimizer.",
  "variants": [
    {
      "id": 101,
      "sku": "WH1000XM5-BLK",
      "manufacturerModel": "WH-1000XM5/B",
      "color": "Black",
      "attributes": {
        "connectivity": "Bluetooth 5.2",
        "batteryLife": "30 hours",
        "noiseCancellation": "Active Noise Canceling",
        "weight": "250g"
      }
    }
  ]
}
```

### 3. Health Check
`GET /api/health`

**Response (200 OK):**
```json
{
  "status": "UP",
  "timestamp": "2026-10-01T07:15:00Z"
}
```
