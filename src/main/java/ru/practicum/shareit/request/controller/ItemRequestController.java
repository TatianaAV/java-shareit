package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddItemRequest;
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
                                 @Valid @RequestBody AddItemRequest item) {
        return itemRequestService.add(AddItemRequest.of(userId, item));
        /*POST /requests
— добавить новый запрос вещи.
 Основная часть запроса — текст запроса,
  где пользователь описывает,
  какая именно вещь ему нужна.
 */
    }

    @GetMapping
    public List<ItemRequestDto> getAll(@RequestHeader(name = requestHeader) int userId) {
        /*GET /requests
 — получить список своих запросов вместе с данными об ответах на них.
 Для каждого запроса должны указываться описание,
 дата и время создания и список ответов в формате:
 id вещи,
 название,
 id владельца.
  Так в дальнейшем, используя указанные id вещей,
   можно будет получить подробную информацию о каждой вещи.
   Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     */
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> searchRequests(@RequestHeader(name = requestHeader) Integer userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {

        return itemRequestService.searchRequests(GetItemRequest.of(userId, PageRequest.of(from / size, size, Sort.by("created").descending())));
        /*GET /requests/all?from={from}&size={size}
    — получить список запросов, созданных другими пользователями.
    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
    на которые они могли бы ответить.
    Запросы сортируются по дате создания: от более новых к более старым.
     Результаты должны возвращаться постранично.
     Для этого нужно передать два параметра: from — индекс первого элемента,
     начиная с 0, и size — количество элементов для отображения.
     */
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(@RequestHeader(name = requestHeader) Integer userId, @PathVariable Long id) {
       /*GET /requests/{requestId}
— получить данные об одном конкретном
 запросе вместе с данными об ответах на него в том же формате,
 что и в эндпоинте GET /requests.
 Посмотреть данные об отдельном запросе может любой пользователь.*/
        return itemRequestService.getById(userId, id);
    }


}
