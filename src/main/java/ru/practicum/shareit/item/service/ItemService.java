package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.dto.itemdto.CreateItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.itemdto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemForOwnerDto getById(long itemId, int userId);

    List<ItemForOwnerDto> getAll(int userId);

    ItemDto update(long itemId, int userId, UpdateItemDto item);

    void delete(long id, int userId);

    ItemDto add(CreateItemDto item);

    List<ItemDto> search(String text, Integer userId);

    CommentDto addComment(int userId, CommentCreate comment, long itemId);
}
