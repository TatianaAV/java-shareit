package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

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
    public List<ItemForOwnerDto> getAll(@RequestHeader(name = requestHeader) int userId) {
        log.info("ItemController getAll {}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(name = requestHeader) Integer userId, @RequestParam(required = false) String text) {
        log.info("ItemController search {}", text);
        return itemService.search(text, userId);
    }

    @GetMapping("/{id}")
    public ItemForOwnerDto getById(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        log.info("ItemController вещь getById {}, запрос userId {}", id, userId);
        return itemService.getById(id);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = requestHeader) int userId, @Validated @RequestBody CreateItemDto item) {
        log.info("ItemController create {}, userId {}", item.getName(), userId);
        return itemService.add(userId, item);
    }

 @PostMapping("/{id}/comment")
    public Comment addComment(@RequestHeader(name = requestHeader) int userId,
                              @PathVariable("id") long itemId, @Validated @RequestBody CommentCreate comment) {
        log.info("addComment  {}, userId {}", comment, userId);
        return itemService.addComment(userId, comment, itemId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(name = requestHeader) int userId, @PathVariable("id") long itemId,
                          @Validated @RequestBody UpdateItemDto item) {
        log.info("ItemController update {}, userId {}", itemId, userId);
        return itemService.update(itemId, userId, item);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        log.info("ItemController delete {}, userId {}", id, userId);
        itemService.delete(id, userId);
    }
    /*   Комментарий можно добавить по эндпоинту POST /items/{itemId}/comment, создайте в контроллере метод для него.
                Реализуйте логику по добавлению нового комментария к вещи в сервисе ItemServiceImpl. Для этого также
                понадобится создать интерфейс CommentRepository. Не забудьте добавить проверку, что пользователь,
                 который пишет комментарий, действительно брал вещь в аренду.
        Осталось разрешить пользователям просматривать комментарии других пользователей. Отзывы можно будет увидеть
        по двум эндпоинтам — по GET /items/{itemId} для одной конкретной вещи и по
        GET /items для всех вещей данного пользователя.*/
}
