package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    @Override
    public ItemRequestDto getById(long id) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAll(int userId) {
        return null;
    }

    @Override
    public ItemRequestDto add(int userId, ItemRequest item) {
        return null;
    }

    @Override
    public List<ItemRequestDto> searchRequest(String text) {
        return null;
    }
}
