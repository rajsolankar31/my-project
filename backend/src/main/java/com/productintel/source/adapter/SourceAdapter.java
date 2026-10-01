package com.productintel.source.adapter;

public interface SourceAdapter {
    String getSourceName();
    boolean supports(String url);
    ResolvedUrlDto parseUrl(String url);
}
