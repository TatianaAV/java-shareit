package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.itemdto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.AddItemRequest;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.MapperItemRequest;
import ru.practicum.shareit.request.mapper.MapperItemRequestImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceTest {

    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemRequestServiceImpl itemRequestService;
    MapperItemRequest mapperItemRequest;
    ItemMapper itemMapper;

    User user1;
    User user2;
    Item item1ByOwner1;
    ItemDto itemDto;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);

        mapperItemRequest = new MapperItemRequestImpl(new ItemMapperImpl(new UserMapperImpl()));
        itemMapper = new ItemMapperImpl(new UserMapperImpl());

        itemRequestService = spy(new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository, mapperItemRequest, itemMapper));

        user1 = new User(1, " user 1", "user1@user.ru");
        user2 = new User(2, " user 2", "user2@user.ru");

        itemRequest = new ItemRequest(1L, "Запрос 1 дрели", LocalDateTime.now(), user2);

        item1ByOwner1 = new Item(1L, "дрель по дереву", "Дрель без функции перфоратора", true, user1, itemRequest);
        itemDto = new ItemDto(1L, "дрель по дереву", "Дрель без функции перфоратора", true, 1L);

        itemRequestDto = new ItemRequestDto(1L, "Запрос 1 дрели", LocalDateTime.now(), List.of(itemDto));
    }


    @Test
    void add() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto actualItemRequest = itemRequestService.add(new AddItemRequest("дрель по дереву", userId, LocalDateTime.now()));

        assertEquals(itemRequest.getRequestId(), actualItemRequest.getId());
        assertEquals(itemRequest.getDescription(), actualItemRequest.getDescription());
        assertEquals(itemRequest.getRequestId(), actualItemRequest.getId());

        verify(itemRequestService, times(1))
                .add(any(AddItemRequest.class));

        verify(itemRequestRepository, times(1))
                .save(any(ItemRequest.class));
    }

    @Test
    void addNotFoundUser() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.add((new AddItemRequest("дрель по дереву", userId, LocalDateTime.now()))));

        verify(itemRequestService, times(1))
                .add(any(AddItemRequest.class));

        verify(itemRequestRepository, times(0))
                .save(any(ItemRequest.class));
    }

    @Test
    void getByIdBy() {

        long requestId = 1;
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequest_RequestId(requestId)).thenReturn(List.of(item1ByOwner1));

        ItemRequestDto actualItemReq = itemRequestService.getById(userId, requestId);

        assertEquals(itemRequest.getRequestId(), actualItemReq.getId());
        assertEquals(itemRequest.getCreated(), actualItemReq.getCreated());
        assertEquals(itemRequest.getDescription(), actualItemReq.getDescription());

        assertEquals(1, actualItemReq.getItems().size());
        assertEquals(itemDto.getId(), actualItemReq.getItems().get(0).getId());
        assertEquals(itemDto.getName(), actualItemReq.getItems().get(0).getName());

        verify(itemRequestService, times(1))
                .getById(anyInt(), anyLong());

        verify(itemRequestRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getByIdByRequestorNotFound() {

        long requestId = 1;
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(userId, requestId));

        verify(itemRequestService, times(1))
                .getById(anyInt(), anyLong());

        verify(itemRequestRepository, times(0))
                .findById(anyLong());
    }

    @Test
    void getByIdRequestNotFoundException() {

        long requestId = 1;
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(userId, requestId));

        verify(itemRequestService, times(1))
                .getById(anyInt(), anyLong());

        verify(itemRequestRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getAllUserNotFound() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAll(userId));

        verify(itemRequestService, times(1))
                .getAll(anyInt());

        verify(itemRequestRepository, times(0))
                .findAllByRequestor(any(User.class));
    }

    @Test
    void getAll() {
        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findAllByRequestor(user2)).thenReturn(List.of(itemRequest));
        when(itemRepository
                .findByRequestInOrderByIdDesc(List.of(itemRequest))).thenReturn(List.of(item1ByOwner1));

        List<ItemRequestDto> actualItemReq = itemRequestService.getAll(userId);

        assertEquals(itemRequest.getRequestId(), actualItemReq.get(0).getId());
        assertEquals(itemRequest.getCreated(), actualItemReq.get(0).getCreated());
        assertEquals(itemRequest.getDescription(), actualItemReq.get(0).getDescription());

        assertEquals(1, actualItemReq.get(0).getItems().size());
        assertEquals(itemDto.getId(), actualItemReq.get(0).getItems().get(0).getId());
        assertEquals(itemDto.getName(), actualItemReq.get(0).getItems().get(0).getName());

        verify(itemRequestService, times(1))
                .getAll(anyInt());

        verify(itemRequestRepository, times(1))
                .findAllByRequestor(any(User.class));
        verify(itemRepository, times(1))
                .findByRequestInOrderByIdDesc(List.of(itemRequest));
    }

    @Test
    void searchRequestsUserNotFound() {

        int userId = 1;
        GetItemRequest request = GetItemRequest.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.searchRequests(request));

        verify(itemRequestService, times(1))
                .searchRequests(request);

        verify(itemRequestRepository, times(0))
                .findAllByRequestorIsNot(any(User.class), any(PageRequest.class));
    }

    @Test
    void searchRequestsUserZeroRequests() {
        int userId = 2;

        GetItemRequest request = GetItemRequest.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()));

        //user
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        //requests Page
        when(itemRequestRepository.findAllByRequestorIsNot(any(User.class), any(PageRequest.class)))
                .thenReturn(Page.empty());
        when(itemRepository
                .findByRequestInOrderByIdDesc(anyList()))
                .thenReturn(List.of());

        List<ItemRequestDto> actualItemReq = itemRequestService.searchRequests(request);

        assertEquals(0, actualItemReq.size());

        verify(itemRequestService, times(1))
                .searchRequests(any(GetItemRequest.class));

        verify(itemRequestRepository, times(1))
                .findAllByRequestorIsNot(any(User.class), any(PageRequest.class));
    }

    @Test
    void searchRequests() {
        int userId = 1;

        GetItemRequest request = GetItemRequest.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()));
        //user
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        //requests Page
        when(itemRequestRepository.findAllByRequestorIsNot(any(User.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRepository
                .findByRequestInOrderByIdDesc(anyList()))
                .thenReturn(List.of(item1ByOwner1));

        List<ItemRequestDto> actualItemReq = itemRequestService.searchRequests(request);

        assertEquals(itemRequest.getRequestId(), actualItemReq.get(0).getId());
        assertEquals(itemRequest.getCreated(), actualItemReq.get(0).getCreated());
        assertEquals(itemRequest.getDescription(), actualItemReq.get(0).getDescription());

        assertEquals(1, actualItemReq.get(0).getItems().size());
        assertEquals(itemDto.getId(), actualItemReq.get(0).getItems().get(0).getId());
        assertEquals(itemDto.getName(), actualItemReq.get(0).getItems().get(0).getName());

        verify(itemRequestService, times(1))
                .searchRequests(request);

        verify(itemRequestRepository, times(1))
                .findAllByRequestorIsNot(any(User.class), any(PageRequest.class));

        verify(itemRepository, times(1))
                .findByRequestInOrderByIdDesc(anyList());
    }
}