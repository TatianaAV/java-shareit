package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static java.time.LocalTime.now;


@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository repository;
    private final BookingMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public BookingForUser getById(Long bookingId, Integer userId) {
        //User owner = userRepository.findById(booker).orElseThrow(() -> new NotFoundException("Владелец не найден"));
        Booking booking = repository.findByIdByOwnerId(bookingId, userId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (userId.equals(booking.getItem().getOwner().getId())
                || userId.equals(booking.getBooker().getId())) {
            return mapper.toBookingForUser(booking);
        }
        throw new ValidationException("Вы не можете получить бронирование, недостаточно прав.");
    }

    @Transactional
    @Override
    public BookingForUser add(Integer bookerId, CreateBooking booking) {
        final User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Ошибка пользователя, не найден"));
        final Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        System.out.println(booking.getStart());
        // if (booking.getStart().before(Timestamp.valueOf(LocalDateTime.now()))) {
        //       throw new ValidationException("Время начала бронирования раньше текущего времени");//404
        //    }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Время начала бронирования после окончания");
        }
        if (Objects.equals(booker.getId(), item.getOwner().getId())) {
            throw new NotFoundException("Невозможно забронировать свою вещь");//404
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        Booking booking1 = mapper.toBooking(booker, item, booking, StatusBooking.WAITING);
        BookingForUser bookingCreate = mapper.toBookingForUser(repository.save(booking1));
        System.out.println(bookingCreate.getStart());
        return bookingCreate;
    }

    @Transactional
    @Override
    public BookingForUser updateStatus(Long bookingId, Integer userId, Boolean approved) {
        Booking update;
        final Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (userId.equals(booking.getItem().getOwner().getId())) {

            if (approved && booking.getStatus().equals(StatusBooking.APPROVED)) {
                throw new ValidationException("Бронирование уже было одобрено");
            }

            if (approved && booking.getStatus().equals(StatusBooking.WAITING)) {
                booking.setStatus(StatusBooking.APPROVED);
            }

            if (!approved && booking.getStatus().equals(StatusBooking.WAITING)) {
                booking.setStatus(StatusBooking.REJECTED);
            }

            update = repository.save(booking);
            return mapper.toBookingForUser(update);

        }
        if (userId.equals(booking.getBooker().getId())) {

            if (!approved && !booking.getStatus().equals(StatusBooking.CURRENT) && (
                    booking.getStatus().equals(StatusBooking.WAITING)
                            || booking.getStatus().equals(StatusBooking.APPROVED))) {
                booking.setStatus(StatusBooking.CANCELLED);

                update = repository.save(booking);
                return mapper.toBookingForUser(update);
            }
        }
        throw new NotFoundException("Вы не можете изменить бронирование, недостаточно прав.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingForUser> getBookingsOwner(int ownerId, StatusBooking status) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Ошибка, пользователь не найден"));
        final List<Item> itemsUser = itemRepository.findAllByOwner(owner);
        if (itemsUser.isEmpty()) {
            throw new NotFoundException("У вас нет ни одной вещи для аренды");
        }
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
    public List<BookingForUser> getBookingsBooker(int bookerId, StatusBooking state) {
        final User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Ошибка пользователя, не найден"));
        List<Booking> bookings;
        switch (state) {
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
                bookings = repository.findAllByPast(booker.getId());
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
