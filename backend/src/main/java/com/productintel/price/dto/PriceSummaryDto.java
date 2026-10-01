package com.productintel.price.dto;

import java.time.Instant;

public record PriceSummaryDto(
    Double currentObserved,
    Double lowestObserved,
    Double highestObserved,
    Double averageObserved,
    Double medianObserved,
    String currency,
    int observationCount,
    String pricePosition,
    Double percentDiffFromAverage,
    Instant latestObservationTime
) {}
