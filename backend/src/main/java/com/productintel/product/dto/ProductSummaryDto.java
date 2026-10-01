package com.productintel.product.dto;

public record ProductSummaryDto(
    Long id,
    String brand,
    String name,
    String model,
    String category,
    String description,
    int variantCount,
    Double startingPrice
) {}
