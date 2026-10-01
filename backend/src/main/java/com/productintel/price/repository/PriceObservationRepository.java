package com.productintel.price.repository;

import com.productintel.price.entity.PriceObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceObservationRepository extends JpaRepository<PriceObservation, Long> {

    @Query("SELECT po FROM PriceObservation po " +
           "JOIN FETCH po.listing l " +
           "JOIN FETCH l.source s " +
           "JOIN l.productVariant pv " +
           "WHERE pv.product.id = :productId " +
           "ORDER BY po.observedAt ASC")
    List<PriceObservation> findTimelineByProductId(@Param("productId") Long productId);

    @Query("SELECT po FROM PriceObservation po " +
           "JOIN FETCH po.listing l " +
           "JOIN FETCH l.source s " +
           "WHERE l.productVariant.id = :variantId " +
           "ORDER BY po.observedAt ASC")
    List<PriceObservation> findTimelineByVariantId(@Param("variantId") Long variantId);

    @Query("SELECT po FROM PriceObservation po " +
           "WHERE po.listing.id = :listingId " +
           "ORDER BY po.observedAt DESC LIMIT 1")
    Optional<PriceObservation> findLatestByListingId(@Param("listingId") Long listingId);
}
