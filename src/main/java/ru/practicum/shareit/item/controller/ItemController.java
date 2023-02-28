package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.dto.itemdto.CreateItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.itemdto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemForOwnerDto> getAll(@RequestHeader(name = requestHeader) int userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(name = requestHeader) Integer userId,
                                @RequestParam String text) {
        log.info("text {}", text);
        if (text.isBlank()) {
            return List.of();
        }
        log.info("return search {}, userId {}", text, userId);
        return itemService.search(text, userId);
    }

    @GetMapping("/{id}")
    public ItemForOwnerDto getById(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        return itemService.getById(id, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = requestHeader) Integer ownerId,
                          @Valid @RequestBody CreateItemDto item) {
        log.info("ownerId {}, item {}, requestId {}", ownerId, item, item.getRequestId());
        return itemService.add(CreateItemDto.of(ownerId, item));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = requestHeader) int userId,
                                 @PathVariable("itemId") long itemId, @Valid @RequestBody CommentCreate comment) {
        return itemService.addComment(userId, comment, itemId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = requestHeader) int userId, @PathVariable("id") long itemId,
                          @RequestBody UpdateItemDto item) {
        return itemService.update(itemId, userId, item);
    }
}
