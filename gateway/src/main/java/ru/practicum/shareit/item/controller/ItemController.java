package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.itemdto.CreateItemDto;
import ru.practicum.shareit.item.dto.itemdto.UpdateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(name = requestHeader) Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(name = requestHeader) Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                         @NotNull @RequestParam(required = false) String text) {
        log.info("search text {}", text);
        return itemClient.searchItems(text, userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(name = requestHeader) long userId, @PathVariable long id) {
        log.info("GetMapping itemService  getById requestHeader {},  itemId {}", userId, id);
        return itemClient.getItem(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = requestHeader) Long ownerId,
                                         @Valid @RequestBody CreateItemDto item) {
        log.info("PostMapping itemService create requestHeader {}, item {}, requestId {}", ownerId, item, item.getRequestId());
        item.setOwnerId(ownerId);
        return itemClient.createItem(item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = requestHeader) Long userId,
                                             @PathVariable("itemId") Long itemId, @Valid @RequestBody CommentCreate comment) {
        log.info("PostMapping itemService  addComment requestHeader {},  text {}", userId, comment.getText());
        return itemClient.createComment(comment, itemId, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(name = requestHeader) long userId, @PathVariable("id") long itemId,
                                         @RequestBody UpdateItemDto item) {
        log.info("PatchMapping itemService  update requestHeader {},  itemId {}, item {}", userId, itemId, item.toString());
        return itemClient.updateItem(item, itemId, userId);
    }
}
