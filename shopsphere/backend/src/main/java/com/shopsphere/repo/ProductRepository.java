package com.shopsphere.repo;

import com.shopsphere.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // q and category are never null (empty string = no filter)
    @Query("select p from Product p where p.active = true "
         + "and lower(p.name) like lower(concat('%', :q, '%')) "
         + "and (:category = '' or p.category = :category)")
    Page<Product> search(@Param("q") String q, @Param("category") String category, Pageable pageable);

    long countByActiveTrue();
}
