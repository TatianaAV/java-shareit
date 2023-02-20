package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.AddItemRequest;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.MapperItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MapperItemRequest mapper;

    @Override
    public List<ItemRequestDto> searchRequests(GetItemRequest req) {

        Pageable pageRequest = PageRequest.of(req.getFrom(), req.getSize(), Sort.Direction.DESC);
       Page<ItemRequest> request = requestRepository.findAll(pageRequest);

        return mapper.mapToItemDto(request);
    }

    @Transactional
    @Override
    public ItemRequestDto add(AddItemRequest request) {
        final User requestor = userRepository
                .findById(request.getRequestorId()).orElseThrow(() -> new NotFoundException("Для запроса зарегистрируйтесь"));
        ItemRequest requestNew = mapper.toItemRequest(requestor, request);
        ItemRequest requestSaved = requestRepository.save(requestNew);
        return mapper.toItemRequestDto(requestSaved);

    }

    @Override
    public List<ItemRequestDto> getAll(int userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        return mapper.toMapItemRequestDto(requestRepository.findAllByRequestor(user));
    }

    @Override
    public ItemRequestDto getById(Integer userId, Long requestId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Зарегестрируйтесь."));
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден."));
        List<Item>  items =  itemRepository.findByRequest_RequestId(requestId);
        ItemRequestDto requestDto = mapper.toItemRequestDto(request, items);
        return requestDto;
    }
}
