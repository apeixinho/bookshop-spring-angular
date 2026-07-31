package com.app.bookshop.repository;

import com.app.bookshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryIdAndActiveTrue(Long id, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Matches product name in any translation locale (or SKU); display language is applied later.
     */
    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN p.translations t
        WHERE p.active = true
          AND (
            LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
            OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :name, '%'))
          )
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
        """)
    Page<Product> findByTranslatedNameContaining(
        @Param("name") String name,
        @Param("categoryId") Long categoryId,
        Pageable pageable);
}
