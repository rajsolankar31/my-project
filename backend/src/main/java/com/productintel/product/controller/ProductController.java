package com.productintel.product.controller;

import com.productintel.price.dto.ListingPriceDto;
import com.productintel.price.dto.ProductPricesResponseDto;
import com.productintel.price.service.PriceService;
import com.productintel.product.dto.ProductDetailDto;
import com.productintel.product.dto.ProductSummaryDto;
import com.productintel.product.dto.ResolveProductRequest;
import com.productintel.product.dto.ResolveProductResponse;
import com.productintel.product.service.ProductResolverService;
import com.productintel.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Product Intelligence API", description = "Endpoints for canonical product search, URL resolution, and price intelligence")
public class ProductController {

    private final ProductService productService;
    private final PriceService priceService;
    private final ProductResolverService productResolverService;

    public ProductController(
        ProductService productService,
        PriceService priceService,
        ProductResolverService productResolverService
    ) {
        this.productService = productService;
        this.priceService = priceService;
        this.productResolverService = productResolverService;
    }

    @GetMapping("/products/search")
    @Operation(summary = "Search canonical products by keyword")
    public ResponseEntity<List<ProductSummaryDto>> searchProducts(
        @RequestParam(name = "q", required = false) String query
    ) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get detailed canonical product with all variants and specifications")
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/products/{id}/prices")
    @Operation(summary = "Get historical price timeline, comparison listings, and statistical summary")
    public ResponseEntity<ProductPricesResponseDto> getProductPrices(@PathVariable("id") Long id) {
        return ResponseEntity.ok(priceService.getProductPrices(id));
    }

    @GetMapping("/products/{id}/listings")
    @Operation(summary = "Get active multi-source listings for a product")
    public ResponseEntity<List<ListingPriceDto>> getProductListings(@PathVariable("id") Long id) {
        ProductPricesResponseDto prices = priceService.getProductPrices(id);
        return ResponseEntity.ok(prices.listings());
    }

    @PostMapping("/products/resolve")
    @Operation(summary = "Resolve a supported store URL (Amazon, Flipkart, Croma) into a canonical product")
    public ResponseEntity<ResolveProductResponse> resolveProductUrl(@Valid @RequestBody ResolveProductRequest request) {
        return ResponseEntity.ok(productResolverService.resolveUrl(request.url()));
    }

    @GetMapping("/health")
    @Operation(summary = "System health check")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Product Intelligence Platform Backend",
            "timestamp", Instant.now().toString()
        ));
    }
}
