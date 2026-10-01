package com.productintel.source.adapter;

public record ResolvedUrlDto(
    String sourceName,
    String externalId,
    String cleanUrl,
    String queryHint
) {}
