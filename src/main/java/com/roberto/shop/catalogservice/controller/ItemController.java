package com.roberto.shop.catalogservice.controller;

import com.roberto.shop.catalogservice.dto.ItemRequest;
import com.roberto.shop.catalogservice.dto.ItemResponse;
import com.roberto.shop.catalogservice.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@RequestBody @Valid ItemRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<ItemResponse> list() { return service.list(); }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) { return service.get(id); }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @RequestBody @Valid ItemRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
