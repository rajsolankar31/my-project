package com.productintel.review.dto;

import java.util.List;

/**
 * Summary of review authenticity signals detected for a product.
 */
public record AuthenticityDto(
    /** Overall authenticity health: CLEAN | MINOR_CONCERN | MAJOR_CONCERN */
    String overallHealth,

    /** Percentage of reviews marked NORMAL (0–100) */
    double cleanPercentage,

    /** Number of reviews flagged as SUSPICIOUS_BURST */
    int suspiciousBurstCount,

    /** Number of reviews flagged as BOT_PATTERN */
    int botPatternCount,

    /** Number of reviews flagged as UNVERIFIED_SPIKE */
    int unverifiedSpikeCount,

    /** Plain-English interpretation of the signals */
    String summary,

    /** Any specific warnings to surface in the UI */
    List<String> warnings
) {}
