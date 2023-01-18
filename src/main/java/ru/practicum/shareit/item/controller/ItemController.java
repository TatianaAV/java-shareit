package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")

public class ItemController {
    private final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<Item> getAll(@RequestHeader(name = REQUEST_HEADER) int userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(required = false) String text) {
        return itemService.search(text);
    }

    @GetMapping("/{id}")
    public Item getById( @RequestHeader(name = REQUEST_HEADER) int userId, @PathVariable long id) {
        return itemService.getById(id, userId);
    }

    @PostMapping
    public Item create(@RequestHeader(name = REQUEST_HEADER) int userId, @Valid @RequestBody CreateItemDto item) {
        return itemService.add(userId, item);
    }

    @PatchMapping("/{id}")
    public Item update(@RequestHeader(name = REQUEST_HEADER) int userId, @PathVariable("id") long itemId,
                       @Valid @RequestBody UpdateItemDto item) {
        System.out.println( userId + " " + itemId);
        return itemService.update(itemId, userId, item);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(name = REQUEST_HEADER) int userId, @PathVariable long id) {
        itemService.delete(id, userId);
    }
}
