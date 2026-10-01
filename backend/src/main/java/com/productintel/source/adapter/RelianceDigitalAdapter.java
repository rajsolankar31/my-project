package com.productintel.source.adapter;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RelianceDigitalAdapter implements SourceAdapter {

    private static final Pattern RELIANCE_PATTERN = Pattern.compile("/p/([0-9]+)");

    @Override
    public String getSourceName() {
        return "Reliance Digital";
    }

    @Override
    public boolean supports(String url) {
        return url != null && url.toLowerCase().contains("reliancedigital.in");
    }

    @Override
    public ResolvedUrlDto parseUrl(String url) {
        String pid = null;
        Matcher matcher = RELIANCE_PATTERN.matcher(url);
        if (matcher.find()) {
            pid = matcher.group(1);
        }
        return new ResolvedUrlDto(getSourceName(), pid, url, null);
    }
}
