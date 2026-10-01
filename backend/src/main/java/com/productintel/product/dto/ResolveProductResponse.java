package com.productintel.product.dto;

import com.productintel.price.dto.ProductPricesResponseDto;

public record ResolveProductResponse(
    boolean resolved,
    String message,
    String sourceName,
    String externalId,
    Long productId,
    ProductDetailDto product,
    ProductPricesResponseDto prices
) {}
