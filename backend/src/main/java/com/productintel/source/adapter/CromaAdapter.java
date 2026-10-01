package com.productintel.source.adapter;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CromaAdapter implements SourceAdapter {

    private static final Pattern CROMA_PATTERN = Pattern.compile("/p/([0-9]+)");

    @Override
    public String getSourceName() {
        return "Croma";
    }

    @Override
    public boolean supports(String url) {
        return url != null && url.toLowerCase().contains("croma.com");
    }

    @Override
    public ResolvedUrlDto parseUrl(String url) {
        String sku = null;
        Matcher matcher = CROMA_PATTERN.matcher(url);
        if (matcher.find()) {
            sku = matcher.group(1);
        }

        String hint = null;
        try {
            String path = url.split("\\?")[0];
            int pIdx = path.indexOf("/p/");
            if (pIdx > 0) {
                String prefix = path.substring(0, pIdx);
                int lastSlash = prefix.lastIndexOf('/');
                if (lastSlash >= 0) {
                    hint = prefix.substring(lastSlash + 1).replace('-', ' ');
                }
            }
        } catch (Exception ignored) {}

        return new ResolvedUrlDto(getSourceName(), sku, url, hint);
    }
}
