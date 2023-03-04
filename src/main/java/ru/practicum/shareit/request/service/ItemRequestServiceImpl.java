package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.MapperItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MapperItemRequest mapper;
    private final ItemMapper itemMapper;


    @Transactional
    @Override
    public ItemRequestDto add(ItemRequestCreateDto request) {
        final User requestor = userRepository
                .findById(request.getRequestorId()).orElseThrow(() -> new NotFoundException("Для запроса зарегистрируйтесь"));
        ItemRequest requestNew = mapper.toItemRequest(requestor, request);
        ItemRequest requestSaved = requestRepository.save(requestNew);
        return mapper.toItemRequestDto(requestSaved);

    }


    @Override
    public ItemRequestDto getById(Integer userId, Long requestId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Зарегистрируйтесь или войдите в свой аккаунт."));
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден."));
        List<Item> items = itemRepository.findByRequest_RequestId(requestId);
        return mapper.toItemRequestDto(request, items);
    }

    @Override
    public List<ItemRequestDto> getAll(int userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        List<ItemRequest> requests = requestRepository.findAllByRequestor(user);

        Map<Long, List<Item>> items = itemRepository
                .findByRequestInOrderByIdDesc(requests)
                .stream().collect(groupingBy(item -> item.getRequest().getRequestId(), toList()));

        return mapToItemsRequestsList(requests, items);
    }

    @Override
    public List<ItemRequestDto> searchRequests(GetItemRequest getItemRequest) {
        final User user = userRepository.findById(getItemRequest.getUserId()).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        PageRequest pageRequest = getItemRequest.getPageRequest();

        log.info("установлено page {}, size {}", pageRequest.getPageNumber(), pageRequest.getPageSize());
        Page<ItemRequest> page = requestRepository.findAllByRequestorIsNot(user, pageRequest);
        List<ItemRequest> requests = mapper.mapToItem(page);
        Map<Long, List<Item>> items = itemRepository
                .findByRequestInOrderByIdDesc(requests)
                .stream().collect(groupingBy(item -> item.getRequest().getRequestId(), toList()));

        return mapToItemsRequestsList(requests, items);
    }

    private List<ItemRequestDto> mapToItemsRequestsList(List<ItemRequest> itemRequests, Map<Long, List<Item>> items) {
        List<ItemRequestDto> requestsDto = mapper.toMapItemRequestDto(itemRequests);
        return requestsDto.stream()
                .map(request -> {
                    request.setItems(itemMapper.mapItemDto(items.getOrDefault(request.getId(), List.of())));
                    return new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated(), request.getItems());
                })
                .collect(Collectors.toList());
    }
}