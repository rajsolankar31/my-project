package com.productintel.source.adapter;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AmazonAdapter implements SourceAdapter {

    private static final Pattern ASIN_PATTERN = Pattern.compile("(?:/dp/|/gp/product/|/product/)([A-Z0-9]{10})");

    @Override
    public String getSourceName() {
        return "Amazon India";
    }

    @Override
    public boolean supports(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        return lower.contains("amazon.in") || lower.contains("amazon.com");
    }

    @Override
    public ResolvedUrlDto parseUrl(String url) {
        String asin = null;
        Matcher matcher = ASIN_PATTERN.matcher(url);
        if (matcher.find()) {
            asin = matcher.group(1);
        }

        // Extract product name slug hint from URL path if available
        String hint = null;
        try {
            String path = url.split("\\?")[0];
            int dpIdx = path.indexOf("/dp/");
            if (dpIdx > 0) {
                String prefix = path.substring(0, dpIdx);
                int lastSlash = prefix.lastIndexOf('/');
                if (lastSlash >= 0) {
                    hint = prefix.substring(lastSlash + 1).replace('-', ' ');
                }
            }
        } catch (Exception ignored) {}

        String cleanUrl = asin != null ? "https://www.amazon.in/dp/" + asin : url;
        return new ResolvedUrlDto(getSourceName(), asin, cleanUrl, hint);
    }
}
