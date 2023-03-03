package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookings;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.itemdto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
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


class BookingServiceTest {

    BookingServiceImpl bookingService;

    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingMapper mapper;

    BookingForUser bookingForUser;
    Booking bookingBooker2Item1;
    Booking bookingCurrent;
    Booking bookingFuture;
    Booking bookingPast;
    User user;
    CreateBookingDto createBookingDto;
    User user1;
    User user2;
    Item item1ByOwner1;
    Item item2ByOwner1;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    ItemDto itemDto1;


    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);

        mapper = new BookingMapperImpl(new UserMapperImpl());

        bookingService = spy(new BookingServiceImpl(userRepository, itemRepository, bookingRepository, mapper));

        user = new User(5, " user 5", "user5@user.ru");
        user1 = new User(1, " user 1", "user1@user.ru");
        user2 = new User(2, " user 2", "user2@user.ru");

        itemRequest = new ItemRequest(1L, "Запрос 1 дрели", LocalDateTime.now(), user2);

        item1ByOwner1 = new Item(1L, "дрель по дереву", "Дрель без функции перфоратора", true, user1, itemRequest);
        item2ByOwner1 = new Item(2L, "дрель по металлу", "вещь 2", true, user1, null);
        itemDto1 = new ItemDto(1L, "дрель по дереву", "Дрель без функции перфоратора", true, 1L);

        itemRequestDto = new ItemRequestDto(1L, "Запрос 1 дрели", LocalDateTime.now(), List.of(itemDto1));

        bookingForUser = new BookingForUser(1L, LocalDateTime.now().plusDays(1), LocalDateTime.MAX,
                StatusBooking.WAITING,
                new BookingForUser.Booker(1, "John Dow"),
                new BookingForUser.Item(1L, "Дрель по дереву"));

        bookingBooker2Item1 = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.MAX, StatusBooking.WAITING, user2, item1ByOwner1);
        bookingCurrent = new Booking(2L, LocalDateTime.now().minusSeconds(5), LocalDateTime.MAX, StatusBooking.APPROVED, user2, item1ByOwner1);
        bookingFuture =
                new Booking(3L, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(10), StatusBooking.APPROVED, user2, item1ByOwner1);
        bookingPast =
                new Booking(4L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5), StatusBooking.APPROVED, user2, item1ByOwner1);
        createBookingDto = new CreateBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.MAX, 1L);
    }

    @Test
    void add() {

        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1ByOwner1.getId())).thenReturn(Optional.of(item1ByOwner1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingBooker2Item1);

        BookingForUser actualBooking = bookingService.add(userId, createBookingDto);

        assertEquals(bookingBooker2Item1.getId(), actualBooking.getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBooking.getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBooking.getItem().getName());
        assertEquals(bookingBooker2Item1.getStatus(), actualBooking.getStatus());

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void addUserNotFoundException() {

        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.add(userId, createBookingDto));

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(0))
                .save(any(Booking.class));
    }

    @Test
    void addItemNotFoundException() {

        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1ByOwner1.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.add(userId, createBookingDto));

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(0))
                .save(any(Booking.class));
    }

    @Test
    void addItemByBookerException() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(item1ByOwner1.getId())).thenReturn(Optional.of(item1ByOwner1));

        assertThrows(NotFoundException.class, () -> bookingService.add(userId, new CreateBookingDto()));

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(0))
                .save(any(Booking.class));
    }

    @Test
    void addValidationExceptionStartEquelsEnd() {

        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1ByOwner1.getId())).thenReturn(Optional.of(item1ByOwner1));

        assertThrows(ValidationException.class, () -> bookingService.add(userId, new CreateBookingDto(LocalDateTime.of(2022, 3, 17, 19, 55, 0), LocalDateTime.of(2022, 3, 17, 19, 55, 0), 1L)));

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(0))
                .save(any(Booking.class));
    }

    @Test
    void addValidationExceptionEndBeforeStart() {

        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1ByOwner1.getId())).thenReturn(Optional.of(item1ByOwner1));

        assertThrows(ValidationException.class, () -> bookingService.add(userId, new CreateBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now(), 1L)));

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(0))
                .save(any(Booking.class));
    }

    @Test
    void addValidationExceptionItemNotAvailable() {

        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        item1ByOwner1.setAvailable(false);
        when(itemRepository.findById(item1ByOwner1.getId())).thenReturn(Optional.of(item1ByOwner1));

        assertThrows(ValidationException.class, () -> bookingService.add(userId, new CreateBookingDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L)));

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(0))
                .save(any(Booking.class));
    }

    @Test
    void addValidationExceptionItemByBooker() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        when(itemRepository.findById(item1ByOwner1.getId())).thenReturn(Optional.of(item1ByOwner1));

        assertThrows(NotFoundException.class, () -> bookingService.add(userId, new CreateBookingDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L)));

        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBookingDto.class));

        verify(bookingRepository, times(0))
                .save(any(Booking.class));
    }

    @Test
    void getById() {
        int userId = 2;
        int bookingId = 1;

        when(bookingRepository.findByIdByOwnerId(anyLong(), anyInt())).thenReturn(Optional.of(bookingBooker2Item1));

        BookingForUser actualItemRequest = bookingService.getById(userId, bookingId);

        assertEquals(bookingBooker2Item1.getId(), actualItemRequest.getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualItemRequest.getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualItemRequest.getItem().getName());
        assertEquals(bookingBooker2Item1.getStatus(), actualItemRequest.getStatus());

        verify(bookingService, times(1))
                .getById(anyLong(), anyInt());

        verify(bookingRepository, times(1))
                .findByIdByOwnerId(anyLong(), anyInt());
    }

    @Test
    void getByIdNotFoundException() {
        int userId = 2;
        int bookingId = 1;

        when(bookingRepository.findByIdByOwnerId(anyLong(), anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(bookingId, userId));

        verify(bookingService, times(1))
                .getById(anyLong(), anyInt());

        verify(bookingRepository, times(1))
                .findByIdByOwnerId(anyLong(), anyInt());
    }

    @Test
    void updateStatusBookerRejection() {
        int userId = 2;
        long bookingId = 1;

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingBooker2Item1));

        BookingForUser actualItemRequest = bookingService.updateStatus(bookingId, userId, Boolean.FALSE);

        assertEquals(bookingBooker2Item1.getId(), actualItemRequest.getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualItemRequest.getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualItemRequest.getItem().getName());
        assertEquals(StatusBooking.CANCELLED, actualItemRequest.getStatus());

        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void updateStatusBookerRejectionAfterApproved() {
        int userId = 2;
        long bookingId = 1;
        bookingBooker2Item1.setStatus(StatusBooking.APPROVED);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingBooker2Item1));

        BookingForUser actualItemRequest = bookingService.updateStatus(bookingId, userId, Boolean.FALSE);

        assertEquals(bookingBooker2Item1.getId(), actualItemRequest.getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualItemRequest.getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualItemRequest.getItem().getName());
        assertEquals(StatusBooking.CANCELLED, actualItemRequest.getStatus());


        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void updateStatusBookerApproved() {
        int userId = 2;
        long bookingId = 1;

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingBooker2Item1));

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(bookingId, userId, Boolean.TRUE));

        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void updateStatusUserIdApproved() {
        int userId = 5;
        long bookingId = 1;

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingBooker2Item1));

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(bookingId, userId, Boolean.TRUE));

        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void updateStatusOwner() {
        int userId = 1;
        long bookingId = 1;

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingBooker2Item1));

        BookingForUser actualItemRequest = bookingService.updateStatus(bookingId, userId, Boolean.TRUE);

        assertEquals(bookingBooker2Item1.getId(), actualItemRequest.getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualItemRequest.getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualItemRequest.getItem().getName());
        assertEquals(StatusBooking.APPROVED, actualItemRequest.getStatus());


        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void updateStatusOwnerRejection() {
        int userId = 1;
        long bookingId = 1;

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingBooker2Item1));

        BookingForUser actualItemRequest = bookingService.updateStatus(bookingId, userId, Boolean.FALSE);

        assertEquals(bookingBooker2Item1.getId(), actualItemRequest.getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualItemRequest.getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualItemRequest.getItem().getName());
        assertEquals(bookingBooker2Item1.getBooker().getId(), actualItemRequest.getBooker().getId());
        assertEquals(StatusBooking.REJECTED, actualItemRequest.getStatus());

        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void updateStatusOwnerExceptionDoubleApproved() {
        int userId = 1;
        long bookingId = 1;

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingBooker2Item1));
        bookingBooker2Item1.setStatus(StatusBooking.APPROVED);

        assertThrows(ValidationException.class, () -> bookingService.updateStatus(bookingId, userId, Boolean.TRUE));

        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void updateStatusBookingNotFoundException() {
        int userId = 1;
        long bookingId = 1;

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(bookingId, userId, Boolean.TRUE));

        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());
    }

    @Test
    void getBookingsOwnerExceptionStateParam() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()), "All");

        assertThrows(IllegalStateException.class, () -> bookingService.getBookingsOwner(request));

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
    }


    @Test
    void getBookingsBookerExceptionStateParam() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()), "All");

        assertThrows(IllegalStateException.class, () -> bookingService.getBookingsBooker(request));

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
    }

    @Test
    void getBookingsOwnerException() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()), "ALL");

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsOwner(request));

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
    }


    @Test
    void getBookingsBookerException() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()), "ALL");

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsBooker(request));

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
    }

    @Test
    void getBookingsOwnerStateParamALL() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("created").descending()), "ALL");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByItemOwner(user1, request.getPageRequest())).thenReturn(new PageImpl<>(List.of(bookingBooker2Item1)));

        List<BookingForUser> actualBookings = bookingService.getBookingsOwner(request);

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingBooker2Item1.getBooker().getId(), actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.WAITING, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByItemOwner(any(User.class), any(PageRequest.class));
    }


    @Test
    void getBookingsBookerStateParamALL() {
        int userId = 2;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "ALL");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBooker(userId, request.getPageRequest())).thenReturn(new PageImpl<>(List.of(bookingBooker2Item1, bookingCurrent)));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(2, actualBookings.size());
        assertEquals(1, actualBookings.get(0).getId());
        assertEquals(2, actualBookings.get(1).getId());

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(userId, actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.WAITING, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByBooker(anyInt(), any(PageRequest.class));
    }

    @Test
    void getBookingsOwnerStateParamCURRENT() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "CURRENT");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllOwnerCurrent(request.getUserId(), request.getPageRequest())).thenReturn(new PageImpl<>(List.of(bookingCurrent)));

        List<BookingForUser> actualBookings = bookingService.getBookingsOwner(request);

        assertEquals(bookingCurrent.getId(), actualBookings.get(0).getId());
        assertEquals(bookingCurrent.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingCurrent.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingCurrent.getBooker().getId(), actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllOwnerCurrent(anyInt(), any(PageRequest.class));
    }


    @Test
    void getBookingsBookerStateParamCurrent() {
        int userId = 2;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "CURRENT");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByCurrent(userId, request.getPageRequest())).thenReturn(new PageImpl<>(List.of(bookingCurrent)));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(bookingCurrent.getId(), actualBookings.get(0).getId());
        assertEquals(bookingCurrent.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingCurrent.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingCurrent.getStart(), actualBookings.get(0).getStart());
        assertEquals(userId, actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByCurrent(anyInt(), any(PageRequest.class));
    }

    @Test
    void getBookingsOwnerStateParamFUTURE() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "FUTURE");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        when(bookingRepository.findAllOwnerFuture(request.getUserId(), request.getPageRequest())).thenReturn(new PageImpl<>(List.of(bookingFuture)));

        List<BookingForUser> actualBookings = bookingService.getBookingsOwner(request);

        assertEquals(bookingFuture.getId(), actualBookings.get(0).getId());
        assertEquals(bookingFuture.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingFuture.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingFuture.getBooker().getId(), actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllOwnerFuture(anyInt(), any(PageRequest.class));
    }


    @Test
    void getBookingsBookerStateParamFUTURE() {
        int userId = 2;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "FUTURE");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByFuture(userId, request.getPageRequest())).thenReturn(new PageImpl<>(List.of(bookingFuture)));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(bookingFuture.getId(), actualBookings.get(0).getId());
        assertEquals(bookingFuture.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingFuture.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingFuture.getStart(), actualBookings.get(0).getStart());
        assertEquals(userId, actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByFuture(anyInt(), any(PageRequest.class));
    }

    @Test
    void getBookingsOwnerStateParamPAST() {
        int userId = 1;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "PAST");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        when(bookingRepository.findBookingsByItemOwnerAndEndBefore(any(User.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(bookingPast)));

        List<BookingForUser> actualBookings = bookingService.getBookingsOwner(request);

        assertEquals(bookingPast.getId(), actualBookings.get(0).getId());
        assertEquals(bookingPast.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingPast.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingPast.getBooker().getId(), actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findBookingsByItemOwnerAndEndBefore(any(User.class), any(LocalDateTime.class), any(PageRequest.class));
    }


    @Test
    void getBookingsBookerStateParamPAST() {
        int userId = 2;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "PAST");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByPast(anyInt(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(bookingPast)));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(bookingPast.getId(), actualBookings.get(0).getId());
        assertEquals(bookingPast.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingPast.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingPast.getStart(), actualBookings.get(0).getStart());
        assertEquals(userId, actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByPast(anyInt(), any(PageRequest.class));
    }

    @Test
    void getBookingsOwnerStateParamREJECTED() {
        int userId = 1;
        bookingBooker2Item1.setStatus(StatusBooking.REJECTED);
        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "REJECTED");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(anyInt(), any(StatusBooking.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(bookingBooker2Item1)));

        List<BookingForUser> actualBookings = bookingService.getBookingsOwner(request);

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingBooker2Item1.getBooker().getId(), actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.REJECTED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusEquals(anyInt(), any(StatusBooking.class), any(PageRequest.class));
    }


    @Test
    void getBookingsBookerStateParamREJECTED() {
        int userId = 2;
        bookingBooker2Item1.setStatus(StatusBooking.REJECTED);
        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "REJECTED");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(bookingBooker2Item1)));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingBooker2Item1.getStart(), actualBookings.get(0).getStart());
        assertEquals(userId, actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.REJECTED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class));
    }

    @Test
    void getBookingsOwnerStateParamWAITING() {
        int userId = 1;
        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "WAITING");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(anyInt(), any(StatusBooking.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(bookingBooker2Item1)));

        List<BookingForUser> actualBookings = bookingService.getBookingsOwner(request);

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingBooker2Item1.getBooker().getId(), actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.WAITING, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusEquals(anyInt(), any(StatusBooking.class), any(PageRequest.class));
    }


    @Test
    void getBookingsBookerStateParamWAITING() {
        int userId = 2;
        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "WAITING");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(bookingBooker2Item1)));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingBooker2Item1.getStart(), actualBookings.get(0).getStart());
        assertEquals(userId, actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.WAITING, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class));
    }

    @Test
    void getBookingsOwnerStateParamAPPROVED() {
        int userId = 1;
        bookingBooker2Item1.setStatus(StatusBooking.APPROVED);
        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "APPROVED");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        when(bookingRepository.findAllByItemOwnerIdAndStatusEquals(anyInt(), any(StatusBooking.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(bookingBooker2Item1)));

        List<BookingForUser> actualBookings = bookingService.getBookingsOwner(request);

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingBooker2Item1.getBooker().getId(), actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusEquals(anyInt(), any(StatusBooking.class), any(PageRequest.class));
    }


    @Test
    void getBookingsBookerStateParamAPPROVED() {
        int userId = 2;
        bookingBooker2Item1.setStatus(StatusBooking.APPROVED);
        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "APPROVED");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(bookingBooker2Item1)));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(bookingBooker2Item1.getId(), actualBookings.get(0).getId());
        assertEquals(bookingBooker2Item1.getItem().getId(), actualBookings.get(0).getItem().getId());
        assertEquals(bookingBooker2Item1.getItem().getName(), actualBookings.get(0).getItem().getName());
        assertEquals(bookingBooker2Item1.getStart(), actualBookings.get(0).getStart());
        assertEquals(userId, actualBookings.get(0).getBooker().getId());
        assertEquals(StatusBooking.APPROVED, actualBookings.get(0).getStatus());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class));
    }

    @Test
    void getBookingsBookerStateParamAPPROVEDEmptyList() {
        int userId = 2;

        GetBookings request = GetBookings.of(userId, PageRequest.of(0, 10, Sort.by("start").descending()), "APPROVED");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        when(bookingRepository.findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

        List<BookingForUser> actualBookings = bookingService.getBookingsBooker(request);

        assertEquals(0, actualBookings.size());

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
        verify(bookingRepository, times(1))
                .findAllByBookerStatus(anyInt(), any(StatusBooking.class), any(PageRequest.class));
    }
}