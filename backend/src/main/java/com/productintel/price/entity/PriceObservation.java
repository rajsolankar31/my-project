package com.productintel.price.entity;

import com.productintel.listing.entity.Listing;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "price_observations", indexes = {
    @Index(name = "idx_price_obs_listing_date", columnList = "listing_id, observed_at")
})
public class PriceObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 10)
    private String currency = "INR";

    @Column(name = "shipping_cost")
    private Double shippingCost = 0.0;

    @Column(length = 50)
    private String availability = "IN_STOCK";

    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;

    public PriceObservation() {
    }

    public PriceObservation(Listing listing, Double price, String currency, Double shippingCost, String availability, Instant observedAt) {
        this.listing = listing;
        this.price = price;
        this.currency = currency != null ? currency : "INR";
        this.shippingCost = shippingCost != null ? shippingCost : 0.0;
        this.availability = availability != null ? availability : "IN_STOCK";
        this.observedAt = observedAt != null ? observedAt : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Instant getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(Instant observedAt) {
        this.observedAt = observedAt;
    }
}
