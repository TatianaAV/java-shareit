package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {


    List<Item> findAll();

    List<Item> getAllByOwnerId(int ownerId);

    Optional<Item> findById(long itemId);

    Optional<Item> create(Item item);

    void delete(long itemId);

    Optional<Item> update(long itemId, Item item);

    List<Item> search(String text);
}

