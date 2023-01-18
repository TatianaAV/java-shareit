package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item getById(long id, int userId);

    List<Item> getAll(int userId);

    Item update(long itemId, int userId, UpdateItemDto item);

    void delete(long id, int userId);

    Item add(int userId, CreateItemDto item);

    List<Item> search(String text);
}
