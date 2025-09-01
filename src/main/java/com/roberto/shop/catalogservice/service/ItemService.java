package com.roberto.shop.catalogservice.service;

import com.roberto.shop.catalogservice.dto.*;
import com.roberto.shop.catalogservice.model.Item;
import com.roberto.shop.catalogservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repo;

    public ItemResponse create(ItemRequest req) {
        Item i = Item.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .stock(req.stock())
                .category(req.category())
                .build();
        return ItemMapper.toResponse(repo.save(i));
    }

    public List<ItemResponse> list() {
        return repo.findAll().stream().map(ItemMapper::toResponse).toList();
    }

    public ItemResponse get(Long id) {
        return repo.findById(id).map(ItemMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
    }

    public ItemResponse update(Long id, ItemRequest req) {
        Item i = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
        i.setName(req.name());
        i.setDescription(req.description());
        i.setPrice(req.price());
        i.setStock(req.stock());
        i.setCategory(req.category());
        return ItemMapper.toResponse(repo.save(i));
    }

    public void delete(Long id) { repo.deleteById(id); }
}
