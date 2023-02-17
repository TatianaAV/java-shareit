package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto getById(long id);

    List<ItemRequestDto> getAll(int userId);

    ItemRequestDto add(int userId, ItemRequest item);

    List<ItemRequestDto> searchRequest(String text);
}
