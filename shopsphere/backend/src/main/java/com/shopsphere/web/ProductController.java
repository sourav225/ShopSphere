package com.shopsphere.web;

import com.shopsphere.dto.Dtos.*;
import com.shopsphere.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductQueryService query;
    private final ProductCommandService command;

    @GetMapping
    public PageResponse<ProductDto> list(@RequestParam(defaultValue = "") String q,
                                         @RequestParam(defaultValue = "") String category,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "12") int size) {
        return query.search(q, category, page, size);
    }

    @GetMapping("/{id}") public ProductDto get(@PathVariable Long id) { return query.get(id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody ProductRequest r) { return command.create(r); }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductRequest r) { return command.update(id, r); }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { command.delete(id); }
}
