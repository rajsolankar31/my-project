package com.productintel.listing.entity;

import com.productintel.price.entity.PriceObservation;
import com.productintel.product.entity.ProductVariant;
import com.productintel.source.entity.Source;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"source_id", "external_product_id"})
})
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(name = "external_product_id", nullable = false, length = 100)
    private String externalProductId;

    @Column(name = "seller_name", length = 100)
    private String sellerName;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "product_url", length = 1000)
    private String productUrl;

    @Column(length = 50)
    private String availability = "IN_STOCK";

    @Column(name = "last_seen_at")
    private Instant lastSeenAt = Instant.now();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceObservation> priceObservations = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Listing() {
    }

    public Listing(ProductVariant productVariant, Source source, String externalProductId, String sellerName, String title, String productUrl, String availability) {
        this.productVariant = productVariant;
        this.source = source;
        this.externalProductId = externalProductId;
        this.sellerName = sellerName;
        this.title = title;
        this.productUrl = productUrl;
        this.availability = availability != null ? availability : "IN_STOCK";
        this.lastSeenAt = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void addPriceObservation(PriceObservation observation) {
        priceObservations.add(observation);
        observation.setListing(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getExternalProductId() {
        return externalProductId;
    }

    public void setExternalProductId(String externalProductId) {
        this.externalProductId = externalProductId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public List<PriceObservation> getPriceObservations() {
        return priceObservations;
    }

    public void setPriceObservations(List<PriceObservation> priceObservations) {
        this.priceObservations = priceObservations;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
