package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.AddItemRequest;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> searchRequests(GetItemRequest request);

    ItemRequestDto add(AddItemRequest item);

    List<ItemRequestDto> getAll(int userId);

    ItemRequestDto getById(Integer userId, Long requestId);
}
