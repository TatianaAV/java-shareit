package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> searchRequests(GetItemRequest request);

    ItemRequestDto add(ItemRequestCreateDto item);

    List<ItemRequestDto> getAll(long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
