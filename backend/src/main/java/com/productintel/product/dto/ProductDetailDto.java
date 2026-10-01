package com.productintel.product.dto;

import java.time.Instant;
import java.util.List;

public record ProductDetailDto(
    Long id,
    String brand,
    String name,
    String model,
    String category,
    String description,
    List<ProductVariantDto> variants,
    Instant lastUpdated
) {}
