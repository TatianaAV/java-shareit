package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getById(long id, int userId);

    List<ItemDto> getAll(int userId);

    ItemDto update(long itemId, int userId, UpdateItemDto item);

    void delete(long id, int userId);

    ItemDto add(int userId, CreateItemDto item);

    List<ItemDto> search(String text);
}
