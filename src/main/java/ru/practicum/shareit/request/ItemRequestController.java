package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

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

    @GetMapping("/search")
    public List<ItemRequestDto> search(@RequestParam(required = false) String text) {
        /*GET /requests/all?from={from}&size={size}
    — получить список запросов, созданных другими пользователями.
    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
    на которые они могли бы ответить.
    Запросы сортируются по дате создания: от более новых к более старым.
     Результаты должны возвращаться постранично.
     Для этого нужно передать два параметра: from — индекс первого элемента,
     начиная с 0, и size — количество элементов для отображения.
     */
        return itemRequestService.searchRequest(text);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(@PathVariable long id) {
       /*GET /requests/{requestId}
— получить данные об одном конкретном
 запросе вместе с данными об ответах на него в том же формате,
 что и в эндпоинте GET /requests.
 Посмотреть данные об отдельном запросе может любой пользователь.*/
        return itemRequestService.getById(id);
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(name = requestHeader) int userId,
                                 @Valid @RequestBody ItemRequest item) {
        return itemRequestService.add(userId, item);
        /*POST /requests
— добавить новый запрос вещи.
 Основная часть запроса — текст запроса,
  где пользователь описывает,
  какая именно вещь ему нужна.
 */
    }
}
