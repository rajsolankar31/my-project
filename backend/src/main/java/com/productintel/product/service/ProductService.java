package com.productintel.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productintel.common.exception.ResourceNotFoundException;
import com.productintel.product.dto.ProductDetailDto;
import com.productintel.product.dto.ProductSummaryDto;
import com.productintel.product.dto.ProductVariantDto;
import com.productintel.product.entity.Product;
import com.productintel.product.entity.ProductVariant;
import com.productintel.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepository productRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public List<ProductSummaryDto> searchProducts(String query) {
        List<Product> products;
        if (query == null || query.trim().isEmpty()) {
            products = productRepository.findAllWithVariants();
        } else {
            products = productRepository.searchProducts(query.trim());
        }

        return products.stream()
            .map(this::toSummaryDto)
            .toList();
    }

    public ProductDetailDto getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return toDetailDto(product);
    }

    private ProductSummaryDto toSummaryDto(Product product) {
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : "Uncategorized";
        int variantCount = product.getVariants() != null ? product.getVariants().size() : 0;

        Double startingPrice = null;
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            startingPrice = product.getVariants().stream()
                .map(ProductVariant::getSuggestedPrice)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(null);
        }

        return new ProductSummaryDto(
            product.getId(),
            product.getBrand(),
            product.getCanonicalName(),
            product.getModel(),
            categoryName,
            product.getDescription(),
            variantCount,
            startingPrice
        );
    }

    private ProductDetailDto toDetailDto(Product product) {
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : "Uncategorized";
        List<ProductVariantDto> variantDtos = new ArrayList<>();

        if (product.getVariants() != null) {
            for (ProductVariant v : product.getVariants()) {
                Map<String, Object> attributes = parseAttributes(v.getAttributesJson());
                variantDtos.add(new ProductVariantDto(
                    v.getId(),
                    v.getSku(),
                    v.getManufacturerModel(),
                    v.getColor(),
                    v.getStorage(),
                    v.getSize(),
                    attributes,
                    v.getSuggestedPrice()
                ));
            }
        }

        return new ProductDetailDto(
            product.getId(),
            product.getBrand(),
            product.getCanonicalName(),
            product.getModel(),
            categoryName,
            product.getDescription(),
            variantDtos,
            product.getUpdatedAt()
        );
    }

    private Map<String, Object> parseAttributes(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
