package com.productintel.price.dto;

import java.time.Instant;

public record ListingPriceDto(
    Long listingId,
    String sourceName,
    String sellerName,
    String title,
    String productUrl,
    Double price,
    Double shippingCost,
    Double totalEstimatedPrice,
    String availability,
    Instant lastObserved,
    boolean isLowestPrice
) {}
