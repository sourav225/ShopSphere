package com.shopsphere.service;

import com.shopsphere.dto.Dtos;
import com.shopsphere.dto.Dtos.*;
import com.shopsphere.entity.Product;
import com.shopsphere.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** Write side: serializable transactions on PostgreSQL; evicts the Redis read cache. */
@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductRepository products;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Caching(evict = {@CacheEvict(cacheNames = "products", allEntries = true), @CacheEvict(cacheNames = "product", allEntries = true)})
    public ProductDto create(ProductRequest r) {
        Product p = Product.builder().build();
        apply(p, r);
        return Dtos.toDto(products.save(p));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Caching(evict = {@CacheEvict(cacheNames = "products", allEntries = true), @CacheEvict(cacheNames = "product", allEntries = true)})
    public ProductDto update(Long id, ProductRequest r) {
        Product p = products.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
        apply(p, r);
        return Dtos.toDto(p);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Caching(evict = {@CacheEvict(cacheNames = "products", allEntries = true), @CacheEvict(cacheNames = "product", allEntries = true)})
    public void delete(Long id) {
        Product p = products.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
        p.setActive(false); // soft delete keeps order history intact
    }

    private void apply(Product p, ProductRequest r) {
        p.setName(r.name()); p.setDescription(r.description()); p.setPrice(r.price());
        p.setStock(r.stock()); p.setCategory(r.category() == null ? "" : r.category()); p.setImageUrl(r.imageUrl());
    }
}
