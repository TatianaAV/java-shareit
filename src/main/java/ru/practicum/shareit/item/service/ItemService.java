package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {

    ItemForOwnerDto getById(long itemId, int userId);

    List<ItemForOwnerDto> getAll(int userId);

    ItemDto update(long itemId, int userId, UpdateItemDto item);

    void delete(long id, int userId);

    ItemDto add(int userId, CreateItemDto item);

    List<ItemDto> search(String text, Integer userId);

    CommentDto addComment(int userId, CommentCreate comment, long itemId);
}
