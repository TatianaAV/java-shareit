package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    Item item1;
    Item item2;
    User user1;
    User user2;

    ItemRequest itemRequest1;
    ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {

     user1 = userRepository.save(new User(
                1,
                "John Doe",
                "john.doe@yandex.com"));

        user2 = userRepository.save(new User(
                2,
                "John Doe",
                "john.doe@mail.com"));

        item1 = itemRepository.save(new Item(1L, "дрель по дереву", "Дрель без функции перфоратора", true, user1, null));
        item2 = itemRepository.save(new Item(2L, "дрель универсальная", "Дрель без функции перфоратора", true, user1, null));

        itemRequest1 = new ItemRequest(null, "Нужна мощная дрель по дереву просверлить дубовую столешницу", LocalDateTime.now().plusHours(4), user2);
        itemRequest2 = requestRepository.save(new ItemRequest(null, "Нужна мощная дрель по бетону", LocalDateTime.of(2022, 2, 25, 19, 55, 0), user2));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAll() {

        List<ItemRequest> itemRequests = requestRepository.findAllByRequestor(user2);

        assertNotNull(itemRequests);
        assertEquals(itemRequests.size(), 1);
        assertEquals(itemRequest2.getRequestId(), itemRequests.get(0).getRequestId());
        assertEquals(itemRequest2.getDescription(), itemRequests.get(0).getDescription());

        assertEquals(itemRequest2.getRequestor().getId(), itemRequests.get(0).getRequestor().getId());
        assertEquals(itemRequest2.getRequestor().getName(), itemRequests.get(0).getRequestor().getName());
        assertEquals(itemRequest2.getRequestor().getEmail(), itemRequests.get(0).getRequestor().getEmail());

        assertEquals(itemRequest2.getCreated(), itemRequests.get(0).getCreated());
    }

    @Test
    void add() {
        ItemRequest itemRequest = requestRepository.save(itemRequest1);

        assertNotNull(itemRequest);
        assertEquals(4, itemRequest.getRequestId());
        assertEquals(itemRequest1.getDescription(), itemRequest.getDescription());

        assertEquals(itemRequest1.getRequestor().getId(), itemRequest.getRequestor().getId());
        assertEquals(itemRequest1.getRequestor().getName(), itemRequest.getRequestor().getName());
        assertEquals(itemRequest1.getRequestor().getEmail(), itemRequest.getRequestor().getEmail());

        assertEquals(itemRequest1.getCreated(), itemRequest.getCreated());
    }

    @Test
    void searchRequests() {
        ItemRequest itemRequest = requestRepository.save(itemRequest1);
        PageRequest request = PageRequest.of(0, 2, Sort.by("created").descending());
        Page<ItemRequest> itemRequestsPage = requestRepository.findAllByRequestorIsNot(user1, request);
        List<ItemRequest> itemRequests = itemRequestsPage.toList();

        assertNotNull(itemRequests);

        assertEquals(itemRequests.size(),2);

        assertEquals(itemRequest.getRequestId(), itemRequests.get(0).getRequestId());
        assertEquals(itemRequest.getDescription(), itemRequests.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), itemRequests.get(0).getCreated());

        assertEquals(itemRequest2.getRequestId(), itemRequests.get(1).getRequestId());
        assertEquals(itemRequest2.getDescription(), itemRequests.get(1).getDescription());
        assertEquals(itemRequest2.getCreated(), itemRequests.get(1).getCreated());

    }

    @Test
    void getById() {
        ItemRequest itemRequest = requestRepository.findById(itemRequest2.getRequestId()).orElseThrow();

        assertNotNull(itemRequest);
        assertEquals(itemRequest2.getRequestId(), itemRequest.getRequestId());
        assertEquals(itemRequest2.getDescription(), itemRequest.getDescription());

        assertEquals(itemRequest2.getRequestor().getId(), itemRequest.getRequestor().getId());
        assertEquals(itemRequest2.getRequestor().getName(), itemRequest.getRequestor().getName());
        assertEquals(itemRequest2.getRequestor().getEmail(), itemRequest.getRequestor().getEmail());

        assertEquals(itemRequest2.getCreated(), itemRequest.getCreated());
    }

    @Test
    void getByIdException() {
        Optional<ItemRequest> itemRequest = requestRepository.findById(100L);

        assertEquals(Optional.empty(), itemRequest);
    }
}