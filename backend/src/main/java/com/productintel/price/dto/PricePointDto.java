package com.productintel.price.dto;

import java.time.Instant;

public record PricePointDto(
    Instant timestamp,
    String formattedDate,
    Double price,
    String sourceName,
    String sellerName
) {}
