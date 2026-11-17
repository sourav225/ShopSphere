package com.shopsphere.service;

import com.shopsphere.dto.Dtos;
import com.shopsphere.dto.Dtos.*;
import com.shopsphere.entity.Product;
import com.shopsphere.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side: read-only transactions, results cached in Redis. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {
    private final ProductRepository products;

    @Cacheable(cacheNames = "products", key = "#q + ':' + #category + ':' + #page + ':' + #size")
    public PageResponse<ProductDto> search(String q, String category, int page, int size) {
        Page<Product> p = products.search(q == null ? "" : q.trim(), category == null ? "" : category,
                PageRequest.of(page, Math.min(size, 50), Sort.by("id").descending()));
        return new PageResponse<>(p.getContent().stream().map(Dtos::toDto).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Cacheable(cacheNames = "product", key = "#id")
    public ProductDto get(Long id) {
        return products.findById(id).filter(Product::isActive).map(Dtos::toDto)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}
