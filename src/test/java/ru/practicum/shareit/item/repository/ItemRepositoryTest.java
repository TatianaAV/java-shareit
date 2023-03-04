package ru.practicum.shareit.item.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository requestRepository;

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

        itemRequest1 = requestRepository.save(new ItemRequest(null, "Нужна мощная дрель по дереву просверлить дубовую столешницу", LocalDateTime.now().plusHours(4), user2));
        itemRequest2 = requestRepository.save(new ItemRequest(null, "Нужна мощная дрель по бетону", LocalDateTime.of(2022, 2, 25, 19, 55, 0), user2));

        item1 = itemRepository
                .save(new Item(1L, "дрель по дереву", "Дрель без функции перфоратора", true, user1, itemRequest1));
        item2 = itemRepository
                .save(new Item(2L, "дрель универсальная", "Дрель без функции перфоратора", true, user1, null));


    }

    @Test
    void findByRequest_RequestId() {
        var createResult = itemRepository.findByRequest_RequestId(itemRequest1.getRequestId());

        assertNotNull(createResult);
        assertEquals(createResult.size(), 1);
        assertEquals(createResult.get(0).getId(), item1.getId());
        assertEquals(createResult.get(0).getName(), item1.getName());
        assertEquals(createResult.get(0).getRequest(), item1.getRequest());
    }

    @Test
    void findAllByOwnerIdOrderById() {
        var createResult = itemRepository.findAllByOwnerIdOrderById(user1.getId());

        assertNotNull(createResult);
        assertEquals(createResult.size(), 2);
        assertEquals(createResult.get(0).getId(), item1.getId());
        assertEquals(createResult.get(0).getName(), item1.getName());
        assertEquals(createResult.get(0).getRequest(), item1.getRequest());
        assertEquals(createResult.get(0).getOwner(), user1);

        assertEquals(createResult.get(1).getId(), item2.getId());
        assertEquals(createResult.get(1).getName(), item2.getName());
        assertEquals(createResult.get(1).getRequest(), item2.getRequest());
    }

    @Test
    void findByIdAndOwner_Id() {
        var createResult = itemRepository.findByIdAndOwner_Id(item1.getId(), user1.getId());

        assertNotNull(createResult);
        Set<Item> resultSet = Set.of(createResult.get());
        Assertions.assertThat(resultSet).contains(item1);
    }

    @Test
    void search() {
        var createResult = itemRepository.search("универсальная");

        assertNotNull(createResult);

        assertEquals(createResult.get(0).getId(), item2.getId());
        assertEquals(createResult.get(0).getName(), item2.getName());
        assertEquals(createResult.get(0).getRequest(), item2.getRequest());
    }

    @Test
    void findByRequestInOrderByIdDesc() {
        var createResult = itemRepository.findByRequestInOrderByIdDesc(List.of(itemRequest1, itemRequest2));

        assertNotNull(createResult);
        assertEquals(createResult.size(), 1);

        assertEquals(createResult.get(0).getId(), item1.getId());
        assertEquals(createResult.get(0).getName(), item1.getName());
        assertEquals(createResult.get(0).getRequest(), item1.getRequest());
    }
}