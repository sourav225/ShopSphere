package com.shopsphere.web;

import com.shopsphere.dto.Dtos.*;
import com.shopsphere.service.*;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final OrderQueryService query;
    private final OrderCommandService command;

    @GetMapping("/stats") public StatsDto stats() { return query.stats(); }
    @GetMapping("/orders") public List<OrderDto> orders() { return query.all(); }

    @PatchMapping("/orders/{id}/status")
    public OrderDto status(@PathVariable Long id, @Valid @RequestBody StatusRequest r) { return command.updateStatus(id, r.status()); }
}
