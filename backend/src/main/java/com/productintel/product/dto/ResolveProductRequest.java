package com.productintel.product.dto;

import jakarta.validation.constraints.NotBlank;

public record ResolveProductRequest(
    @NotBlank(message = "URL cannot be blank")
    String url
) {}
