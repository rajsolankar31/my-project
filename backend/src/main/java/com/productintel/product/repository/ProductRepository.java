package com.productintel.product.repository;

import com.productintel.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category c " +
           "LEFT JOIN FETCH p.variants v " +
           "WHERE LOWER(p.canonicalName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR LOWER(p.model) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "   OR (c IS NOT NULL AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(@Param("query") String query);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category c LEFT JOIN FETCH p.variants")
    List<Product> findAllWithVariants();
}
