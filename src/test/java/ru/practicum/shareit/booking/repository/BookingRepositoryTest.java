package ru.practicum.shareit.booking.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
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
    Booking booking1;
    Booking booking2;
    Booking booking3;
    Booking booking4;
    ItemRequest itemRequest1;

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
        itemRequest1 = requestRepository.save(new ItemRequest(null, "Нужна мощная дрель по бетону", LocalDateTime.of(2022, 2, 25, 19, 55, 0), user2));

        booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setStatus(StatusBooking.WAITING);
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking1 = bookingRepository.save(booking1);


        booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        booking2.setStatus(StatusBooking.APPROVED);
        booking2.setItem(item1);
        booking2.setBooker(user2);
        booking2 = bookingRepository.save(booking2);

        booking3 = new Booking();
        booking3.setStart(LocalDateTime.now().minusDays(1));
        booking3.setEnd(LocalDateTime.now().minusHours(4));
        booking3.setStatus(StatusBooking.APPROVED);
        booking3.setItem(item2);
        booking3.setBooker(user2);

        booking4 = new Booking();
        booking4.setStart(LocalDateTime.now().minusDays(1));
        booking4.setEnd(LocalDateTime.now().plusHours(4));
        booking4.setStatus(StatusBooking.APPROVED);
        booking4.setItem(item2);
        booking4.setBooker(user2);
    }

    @Test
    void findByIdByOwnerId() {

        var createResult = bookingRepository.findByIdByOwnerId(booking1.getId(), user1.getId());

        assertNotNull(createResult);
        Set<Booking> resultSet = Set.of(createResult.get());
        Assertions.assertThat(resultSet).contains(booking1);
    }

    @Test
    void findBookingByBookerAndItem() {

    }

    @Test
    void findAllByBooker() {

        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllByBooker(user2.getId(), request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0).getId(), booking2.getId());
        assertEquals(bookingList.get(0).getBooker(), booking2.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking2.getStart());

        assertEquals(bookingList.get(1).getId(), booking1.getId());
        assertEquals(bookingList.get(1).getBooker(), booking1.getBooker());
        assertEquals(bookingList.get(1).getStart(), booking1.getStart());
    }

    @Test
    void findAllByPast() {
        booking3 = bookingRepository.save(booking3);
        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllByPast(user2.getId(), request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getId(), booking3.getId());
        assertEquals(bookingList.get(0).getBooker(), booking3.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking3.getStart());
    }

    @Test
    void findAllByCurrent() {
        booking4 = bookingRepository.save(booking4);
        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllByCurrent(user2.getId(), request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getId(), booking4.getId());
        assertEquals(bookingList.get(0).getBooker(), booking4.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking4.getStart());
    }

    @Test
    void findAllByFuture() {

        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllByFuture(user2.getId(), request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0).getId(), booking2.getId());
        assertEquals(bookingList.get(0).getBooker(), booking2.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking2.getStart());

        assertEquals(bookingList.get(1).getId(), booking1.getId());
        assertEquals(bookingList.get(1).getBooker(), booking1.getBooker());
        assertEquals(bookingList.get(1).getStart(), booking1.getStart());
    }

    @Test
    void findAllByBookerStatusREJECTED() {
        var booking = bookingRepository.findById(booking1.getId());
        booking.get().setStatus(StatusBooking.REJECTED);
        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllByBookerStatus(user2.getId(), StatusBooking.REJECTED, request);
        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getId(), booking1.getId());
        assertEquals(bookingList.get(0).getBooker(), booking1.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking1.getStart());
    }

    @Test
    void findAllByItemOwner() {

        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllByItemOwner(user1, request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0).getId(), booking2.getId());
        assertEquals(bookingList.get(0).getBooker(), booking2.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking2.getStart());
        assertEquals(bookingList.get(0).getItem().getOwner(), user1);

        assertEquals(bookingList.get(1).getId(), booking1.getId());
        assertEquals(bookingList.get(1).getBooker(), booking1.getBooker());
        assertEquals(bookingList.get(1).getStart(), booking1.getStart());
        assertEquals(bookingList.get(1).getItem().getOwner(), user1);
    }

    @Test
    void findBookingsByItemOwnerAndEndBefore() {
        booking3 = bookingRepository.save(booking3);
        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findBookingsByItemOwnerAndEndBefore(user1, LocalDateTime.now(), request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getId(), booking3.getId());
        assertEquals(bookingList.get(0).getBooker(), booking3.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking3.getStart());
        assertEquals(bookingList.get(0).getItem().getOwner(), user1);
    }

    @Test
    void findAllOwnerCurrent() {
        booking4 = bookingRepository.save(booking4);
        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllOwnerCurrent(user1.getId(), request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getId(), booking4.getId());
        assertEquals(bookingList.get(0).getBooker(), booking4.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking4.getStart());
        assertEquals(bookingList.get(0).getItem().getOwner(), user1);
    }

    @Test
    void findAllOwnerFuture() {

        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllOwnerFuture(user1.getId(), request);

        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 2);
        assertEquals(bookingList.get(0).getId(), booking2.getId());
        assertEquals(bookingList.get(0).getBooker(), booking2.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking2.getStart());
        assertEquals(bookingList.get(0).getItem().getOwner(), user1);

        assertEquals(bookingList.get(1).getId(), booking1.getId());
        assertEquals(bookingList.get(1).getBooker(), booking1.getBooker());
        assertEquals(bookingList.get(1).getStart(), booking1.getStart());
        assertEquals(bookingList.get(1).getItem().getOwner(), user1);
    }

    @Test
    void findAllByItemOwnerIdAndStatusEquals() {
        var booking = bookingRepository.findById(booking1.getId());
        booking.get().setStatus(StatusBooking.REJECTED);
        PageRequest request = PageRequest.of(0, 10, Sort.by("start").descending());
        var createResult = bookingRepository.findAllByItemOwnerIdAndStatusEquals(user1.getId(), StatusBooking.REJECTED, request);
        assertNotNull(createResult);
        List<Booking> bookingList = createResult.toList();
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getId(), booking1.getId());
        assertEquals(bookingList.get(0).getBooker(), booking1.getBooker());
        assertEquals(bookingList.get(0).getStart(), booking1.getStart());
        assertEquals(bookingList.get(0).getItem().getOwner(), user1);
    }
}