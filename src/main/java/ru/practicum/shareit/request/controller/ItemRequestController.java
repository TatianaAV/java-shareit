package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(name = requestHeader) Integer userId,
                                 @Valid @RequestBody ItemRequestCreateDto item) {
        log.info("PostMapping itemRequestService, requestHeader {}, AddItemRequest {}", userId, item);
        return itemRequestService.add(ItemRequestCreateDto.of(userId, item));
    }

    @GetMapping
    public List<ItemRequestDto> getAll(@RequestHeader(name = requestHeader) int userId) {
        log.info("GetMapping itemRequestService, requestHeader {}, getAll", userId);
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> searchRequests(@RequestHeader(name = requestHeader) Integer userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GetMapping itemRequestService, requestHeader {}, from {}, size {}, ", userId, from, size);
        return itemRequestService.searchRequests(GetItemRequest.of(userId, PageRequest.of(from / size, size, Sort.by("created").descending())));
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(@RequestHeader(name = requestHeader) Integer userId, @PathVariable Long id) {
        log.info("GetMapping itemRequestService, requestHeader {}, getById {}", userId, id);
        return itemRequestService.getById(userId, id);
    }
}
