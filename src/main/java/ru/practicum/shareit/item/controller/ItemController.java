package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path ="/items")
public class ItemController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(name = requestHeader) int userId) {
        log.info("ItemController getAll {}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(name = requestHeader) Integer userId, @RequestParam(required = false) String text) {
        log.info("ItemController search {}", text);
        return itemService.search(text, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        log.info("ItemController вещь getById {}, запрос userId {}", id, userId);
        return itemService.getById(id);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = requestHeader) int userId, @Valid @RequestBody CreateItemDto item) {
        log.info("ItemController create {}, userId {}", item.getName(), userId);
        return itemService.add(userId, item);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = requestHeader) int userId, @PathVariable("id") long itemId,
                          @Valid @RequestBody UpdateItemDto item) {
        log.info("ItemController update {}, userId {}", itemId, userId);
        return itemService.update(itemId, userId, item);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        log.info("ItemController delete {}, userId {}", id, userId);
        itemService.delete(id, userId);
    }
}
