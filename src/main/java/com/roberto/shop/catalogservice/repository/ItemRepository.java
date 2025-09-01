package com.roberto.shop.catalogservice.repository;

import com.roberto.shop.catalogservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {}
