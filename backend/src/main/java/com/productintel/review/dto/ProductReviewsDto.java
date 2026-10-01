package com.productintel.review.dto;

import java.util.List;
import java.util.Map;

/**
 * Complete review intelligence payload for a product.
 */
public record ProductReviewsDto(
    Long            productId,
    int             totalReviews,
    double          averageRating,
    Map<Integer, Long> ratingDistribution,   // {5: 42, 4: 18, 3: 7, 2: 3, 1: 5}

    /** Aggregated sentiment per aspect cluster */
    List<AspectClusterDto> aspectClusters,

    /** Authenticity intelligence summary */
    AuthenticityDto authenticity,

    /** Verdict: GREAT_TIME / WAIT / NEUTRAL */
    String          buyVerdict,
    String          buyVerdictReason,

    /** Top 5 most helpful positive reviews */
    List<ReviewItemDto> topPositiveReviews,

    /** Top 5 most helpful critical reviews */
    List<ReviewItemDto> topCriticalReviews,

    /** All reviews (capped at 50 for API response size) */
    List<ReviewItemDto> allReviews
) {}
