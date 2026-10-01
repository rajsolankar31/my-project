package com.productintel.listing.repository;

import com.productintel.listing.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    @Query("SELECT l FROM Listing l " +
           "JOIN FETCH l.source s " +
           "JOIN FETCH l.productVariant pv " +
           "WHERE pv.product.id = :productId")
    List<Listing> findAllByProductId(@Param("productId") Long productId);

    @Query("SELECT l FROM Listing l " +
           "JOIN FETCH l.source s " +
           "WHERE l.productVariant.id = :variantId")
    List<Listing> findAllByVariantId(@Param("variantId") Long variantId);

    Optional<Listing> findBySourceIdAndExternalProductId(Long sourceId, String externalProductId);

    Optional<Listing> findByProductUrl(String productUrl);

    @Query("SELECT l FROM Listing l " +
           "JOIN FETCH l.source s " +
           "JOIN FETCH l.productVariant pv " +
           "JOIN pv.product p " +
           "WHERE LOWER(s.name) = LOWER(:sourceName) AND LOWER(l.externalProductId) = LOWER(:externalId)")
    Optional<Listing> findBySourceNameAndExternalId(
        @Param("sourceName") String sourceName,
        @Param("externalId") String externalId
    );
}
