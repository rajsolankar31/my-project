package com.productintel.review.dto;

/**
 * Aggregated sentiment statistics for a single review aspect cluster.
 * e.g., "Noise Cancellation": avg sentiment 0.82, 31 reviews.
 */
public record AspectClusterDto(
    String clusterName,
    int    reviewCount,
    double averageSentiment,   // -1.0 → +1.0
    double averageRating,
    /** PRIMARY_POSITIVE | PRIMARY_NEGATIVE | MIXED */
    String sentiment
) {}
