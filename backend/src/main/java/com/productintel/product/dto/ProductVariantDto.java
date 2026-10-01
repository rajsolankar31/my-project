package com.productintel.product.dto;

import java.util.Map;

public record ProductVariantDto(
    Long id,
    String sku,
    String manufacturerModel,
    String color,
    String storage,
    String size,
    Map<String, Object> attributes,
    Double suggestedPrice
) {}
