package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

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
    public List<ItemSearchDto> search(@RequestHeader(name = requestHeader) Integer userId, @RequestParam String text) {
        return itemService.search(text, userId);
    }

    @GetMapping("/{id}")
    public ItemForOwnerDto getById(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        return itemService.getById(id, userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = requestHeader) int userId, @Valid @RequestBody CreateItemDto item) {
        return itemService.add(userId, item);
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

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        itemService.delete(id, userId);
    }
}
