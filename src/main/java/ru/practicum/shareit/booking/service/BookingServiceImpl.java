package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.validation.ValidationService.validateStatusBooker;
import static ru.practicum.shareit.validation.ValidationService.validateStatusOwner;


@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final ValidationService validationService;
    private final BookingRepository repository;
    private final BookingMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public BookingForUser getById(long bookingId, int userId) {
        Booking booking = repository.findByIdByOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (userId == booking.getItem().getOwner().getId()
                || userId == booking.getBooker().getId()) {
            return mapper.toBookingForUser(booking);
        }
        throw new ValidationException("Вы не можете получить бронирование, недостаточно прав.");
    }

    @Transactional
    @Override
    public BookingForUser add(int bookerId, CreateBooking booking) {
        final User booker = validationService.validateUser(bookerId);
        final Item item = validationService.validateItem(booking.getItemId());
        validationService.validateAddBooking(booking, booker, item);
        Booking booking1 = mapper.toBooking(booker, item, booking, StatusBooking.WAITING);
        return mapper.toBookingForUser(repository.save(booking1));
    }

    @Transactional
    @Override
    public BookingForUser updateStatus(long bookingId, int userId, Boolean approved) {
        Booking update;
        final Booking booking = validationService.validateBooking(bookingId);

        if (userId == booking.getItem().getOwner().getId()) {
            validateStatusOwner(approved, booking);
            update = repository.save(booking);
            return mapper.toBookingForUser(update);
        }
        if (userId == booking.getBooker().getId()) {
            validateStatusBooker(approved, booking);
            update = repository.save(booking);
            return mapper.toBookingForUser(update);
        }
        throw new NotFoundException("Вы не можете изменить бронирование, недостаточно прав.");
    }


    @Transactional(readOnly = true)
    @Override
    public List<BookingForUser> getBookingsOwner(int ownerId, String stateParam) {
        User owner = validationService.validateUser(ownerId);
        StatusBooking status = StatusBooking.from(stateParam);
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = repository.findAllByItemOwner(ownerId);
                return mapper.toMapForUsers(bookings);

            case CURRENT:
                bookings = repository.findAllOwnerCurrent(ownerId);
                return mapper.toMapForUsers(bookings);

            case FUTURE:
                bookings = repository.findAllOwnerFuture(ownerId);
                return mapper.toMapForUsers(bookings);

            case PAST:
                bookings = repository.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(owner, LocalDateTime.now());
                System.out.println(LocalDateTime.now());
                return mapper.toMapForUsers(bookings);

            case REJECTED:
                bookings = repository.findAllByItemOwnerIdAndStatusEquals(ownerId, StatusBooking.REJECTED);
                return mapper.toMapForUsers(bookings);

            case WAITING:
                bookings = repository.findAllByItemOwnerIdAndStatusEquals(ownerId, StatusBooking.WAITING);
                return mapper.toMapForUsers(bookings);

            case APPROVED:
                bookings = repository.findAllByItemOwnerIdAndStatusEquals(ownerId, StatusBooking.APPROVED);
                return mapper.toMapForUsers(bookings);
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingForUser> getBookingsBooker(int bookerId, String stateParam) {
        StatusBooking status = StatusBooking.from(stateParam);
        validationService.validateUser(bookerId);
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = repository.findAllByBooker(bookerId);
                return mapper.toMapForUsers(bookings);

            case CURRENT:
                bookings = repository.findAllByCurrent(bookerId);
                return mapper.toMapForUsers(bookings);

            case FUTURE:
                bookings = repository.findAllByFuture(bookerId);
                return mapper.toMapForUsers(bookings);

            case PAST:
                bookings = repository.findAllByPast(bookerId);
                return mapper.toMapForUsers(bookings);

            case REJECTED:
                bookings = repository.findAllByBookerStatus(bookerId, StatusBooking.REJECTED);
                return mapper.toMapForUsers(bookings);

            case WAITING:
                bookings = repository.findAllByBookerStatus(bookerId, StatusBooking.WAITING);
                return mapper.toMapForUsers(bookings);

            case APPROVED:
                bookings = repository.findAllByBookerStatus(bookerId, StatusBooking.APPROVED);
                return mapper.toMapForUsers(bookings);
        }
        return new ArrayList<>();
    }
}
