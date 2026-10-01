package com.productintel.review.dto;

import java.time.LocalDate;

/**
 * Single review item returned by the API.
 */
public record ReviewItemDto(
    Long      id,
    String    sourceName,
    String    reviewerName,
    int       rating,
    String    title,
    String    body,
    LocalDate reviewDate,
    boolean   verifiedPurchase,
    int       helpfulVotes,
    Double    sentimentScore,
    String    aspectCluster,
    String    authenticityFlag
) {}
