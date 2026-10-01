package com.productintel.price.dto;

import java.util.List;

public record ProductPricesResponseDto(
    Long productId,
    PriceSummaryDto summary,
    List<PricePointDto> timeline,
    List<ListingPriceDto> listings
) {}
