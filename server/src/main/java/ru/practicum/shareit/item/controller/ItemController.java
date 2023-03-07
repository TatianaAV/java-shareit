package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemService itemClient;

    @GetMapping
    public List<ItemForOwnerDto> getAll(@RequestHeader(name = requestHeader) Long userId) {
        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(name = requestHeader) Long userId,
                                @RequestParam(required = false) String text) {
        log.info("search text {}", text);
        if (text.isBlank()) {
            return List.of();
        }
        return itemClient.search(text, userId);
    }

    @GetMapping("/{id}")
    public ItemForOwnerDto getById(@RequestHeader(name = requestHeader) long userId,
                                   @PathVariable long id) {
        log.info("GetMapping itemService  getById requestHeader {},  itemId {}", userId, id);
        return itemClient.getById(id, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = requestHeader) Long ownerId,
                          @RequestBody CreateItemDto item) {
        log.info("PostMapping itemService create requestHeader {}, item {}, requestId {}", ownerId, item, item.getRequestId());
        return itemClient.add(CreateItemDto.of(ownerId, item));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = requestHeader) Long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @RequestBody CommentCreate comment) {
        log.info("PostMapping itemService  addComment requestHeader {},  text {}", userId, comment.getText());
        return itemClient.addComment(userId, comment, itemId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = requestHeader) long userId, @PathVariable("id") long itemId,
                          @RequestBody UpdateItemDto item) {
        log.info("PatchMapping itemService  update requestHeader {},  itemId {}, item {}", userId, itemId, item.toString());
        return itemClient.update(itemId, userId, item);
    }
}
