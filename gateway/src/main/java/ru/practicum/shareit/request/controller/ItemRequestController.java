package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object>   create(@RequestHeader(name = requestHeader) Long userId,
                                           @Valid @RequestBody ItemRequestCreateDto item) {
        log.info("PostMapping itemRequestService, requestHeader {}, ItemRequestCreateDto {}", userId, item);
        return itemRequestClient.createItemRequest(item, userId);
    }

    @GetMapping
    public ResponseEntity<Object>   getAll(@RequestHeader(name = requestHeader) Long userId) {
        log.info("GetMapping itemRequestService, requestHeader {}, getAll", userId);
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object>  searchRequests(@RequestHeader(name = requestHeader) Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GetMapping itemRequestService, requestHeader {}, from {}, size {}, ", userId, from, size);
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(name = requestHeader) Long userId, @PathVariable Long id) {
        log.info("GetMapping itemRequestService, requestHeader {}, getById {}", userId, id);
        return itemRequestClient.getItemRequest(id, userId);
    }
}
