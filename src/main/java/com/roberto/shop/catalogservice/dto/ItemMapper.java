package com.roberto.shop.catalogservice.dto;

import com.roberto.shop.catalogservice.model.Item;

public class ItemMapper {
    public static ItemResponse toResponse(Item i) {
        return new ItemResponse(i.getId(), i.getName(), i.getDescription(),
                i.getPrice(), i.getStock(), i.getCategory(), i.getUpdatedAt());
    }
}
