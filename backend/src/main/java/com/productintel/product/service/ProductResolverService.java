package com.productintel.product.service;

import com.productintel.listing.entity.Listing;
import com.productintel.listing.repository.ListingRepository;
import com.productintel.price.dto.ProductPricesResponseDto;
import com.productintel.price.service.PriceService;
import com.productintel.product.dto.ProductDetailDto;
import com.productintel.product.dto.ProductSummaryDto;
import com.productintel.product.dto.ResolveProductResponse;
import com.productintel.source.adapter.ResolvedUrlDto;
import com.productintel.source.adapter.SourceAdapter;
import com.productintel.source.adapter.SourceAdapterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductResolverService {

    private final SourceAdapterRegistry sourceAdapterRegistry;
    private final ListingRepository listingRepository;
    private final ProductService productService;
    private final PriceService priceService;

    public ProductResolverService(
        SourceAdapterRegistry sourceAdapterRegistry,
        ListingRepository listingRepository,
        ProductService productService,
        PriceService priceService
    ) {
        this.sourceAdapterRegistry = sourceAdapterRegistry;
        this.listingRepository = listingRepository;
        this.productService = productService;
        this.priceService = priceService;
    }

    public ResolveProductResponse resolveUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return new ResolveProductResponse(false, "URL cannot be empty", null, null, null, null, null);
        }

        Optional<SourceAdapter> adapterOpt = sourceAdapterRegistry.findAdapter(url);
        if (adapterOpt.isEmpty()) {
            return new ResolveProductResponse(
                false,
                "Unsupported source. Supported sources currently include Amazon India, Flipkart, Croma, and Reliance Digital.",
                null,
                null,
                null,
                null,
                null
            );
        }

        SourceAdapter adapter = adapterOpt.get();
        ResolvedUrlDto parsed = adapter.parseUrl(url);

        // 1a. Exact product URL match (normalized — strip trailing slash)
        String normalizedUrl = parsed.cleanUrl().replaceAll("/$", "");
        Optional<Listing> listingOpt = listingRepository.findByProductUrl(normalizedUrl);
        if (listingOpt.isEmpty()) {
            listingOpt = listingRepository.findByProductUrl(normalizedUrl + "/");
        }

        // 1b. Fall back to keyed lookup by source name + external ID (avoids full table scan)
        if (listingOpt.isEmpty() && parsed.externalId() != null) {
            listingOpt = listingRepository.findBySourceNameAndExternalId(
                parsed.sourceName(), parsed.externalId()
            );
        }

        if (listingOpt.isPresent()) {
            Listing listing = listingOpt.get();
            Long productId = listing.getProductVariant().getProduct().getId();
            ProductDetailDto productDetail = productService.getProductById(productId);
            ProductPricesResponseDto prices = priceService.getProductPrices(productId);

            return new ResolveProductResponse(
                true,
                "Canonical product resolved from source listing.",
                parsed.sourceName(),
                parsed.externalId(),
                productId,
                productDetail,
                prices
            );
        }

        // 2. Fallback: If externalId not in database, attempt heuristic match via query hint
        if (parsed.queryHint() != null && !parsed.queryHint().trim().isEmpty()) {
            List<ProductSummaryDto> candidates = productService.searchProducts(parsed.queryHint());
            if (!candidates.isEmpty()) {
                Long productId = candidates.get(0).id();
                ProductDetailDto productDetail = productService.getProductById(productId);
                ProductPricesResponseDto prices = priceService.getProductPrices(productId);

                return new ResolveProductResponse(
                    true,
                    "Matched to closest canonical product via URL analysis (" + candidates.get(0).name() + ").",
                    parsed.sourceName(),
                    parsed.externalId(),
                    productId,
                    productDetail,
                    prices
                );
            }
        }

        return new ResolveProductResponse(
            false,
            "Recognized " + parsed.sourceName() + " URL, but this specific item is not yet indexed in the canonical database.",
            parsed.sourceName(),
            parsed.externalId(),
            null,
            null,
            null
        );
    }
}
