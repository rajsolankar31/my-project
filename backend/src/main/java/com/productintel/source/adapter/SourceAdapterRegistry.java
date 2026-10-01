package com.productintel.source.adapter;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SourceAdapterRegistry {

    private final List<SourceAdapter> adapters;

    public SourceAdapterRegistry(List<SourceAdapter> adapters) {
        this.adapters = adapters;
    }

    public Optional<SourceAdapter> findAdapter(String url) {
        if (url == null || url.trim().isEmpty()) {
            return Optional.empty();
        }
        return adapters.stream()
            .filter(a -> a.supports(url))
            .findFirst();
    }

    public List<SourceAdapter> getAllAdapters() {
        return adapters;
    }
}
