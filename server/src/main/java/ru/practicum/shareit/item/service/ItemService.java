package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;

import java.util.List;

public interface ItemService {

    ItemForOwnerDto getById(long itemId, long userId);

    List<ItemForOwnerDto> getAll(long userId);

    ItemDto update(long itemId, long userId, UpdateItemDto item);

    ItemDto add(CreateItemDto item);

    List<ItemDto> search(String text, Long userId);

    CommentDto addComment(long userId, CommentCreate comment, long itemId);
}
