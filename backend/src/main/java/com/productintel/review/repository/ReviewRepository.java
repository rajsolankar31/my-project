package com.productintel.review.repository;

import com.productintel.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /** All reviews for all variants of a product, ordered newest first. */
    @Query("SELECT r FROM Review r " +
           "WHERE r.productVariant.product.id = :productId " +
           "ORDER BY r.reviewDate DESC")
    List<Review> findAllByProductId(@Param("productId") Long productId);

    /** Reviews for a specific variant. */
    @Query("SELECT r FROM Review r " +
           "WHERE r.productVariant.id = :variantId " +
           "ORDER BY r.reviewDate DESC")
    List<Review> findAllByVariantId(@Param("variantId") Long variantId);

    /** Count by rating bucket for a product — used for histogram. */
    @Query("SELECT r.rating, COUNT(r) FROM Review r " +
           "WHERE r.productVariant.product.id = :productId " +
           "GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> countByRatingForProduct(@Param("productId") Long productId);

    /** Reviews flagged as non-NORMAL for a product. */
    @Query("SELECT r FROM Review r " +
           "WHERE r.productVariant.product.id = :productId " +
           "AND r.authenticityFlag <> 'NORMAL' " +
           "ORDER BY r.reviewDate DESC")
    List<Review> findFlaggedReviewsByProductId(@Param("productId") Long productId);
}
