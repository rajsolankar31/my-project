package com.productintel.source.adapter;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FlipkartAdapter implements SourceAdapter {

    private static final Pattern PID_PATTERN = Pattern.compile("(?:/p/|pid=)([a-zA-Z0-9]+)");

    @Override
    public String getSourceName() {
        return "Flipkart";
    }

    @Override
    public boolean supports(String url) {
        return url != null && url.toLowerCase().contains("flipkart.com");
    }

    @Override
    public ResolvedUrlDto parseUrl(String url) {
        String pid = null;
        Matcher matcher = PID_PATTERN.matcher(url);
        if (matcher.find()) {
            pid = matcher.group(1);
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

        String cleanUrl = pid != null ? "https://www.flipkart.com/product/p/" + pid : url;
        return new ResolvedUrlDto(getSourceName(), pid, cleanUrl, hint);
    }
}
