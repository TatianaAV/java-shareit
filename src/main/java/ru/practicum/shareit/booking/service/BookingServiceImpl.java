package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.validation.ValidationService.validateStatusBooker;
import static ru.practicum.shareit.validation.ValidationService.validateStatusOwner;


@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public BookingForUser getById(long bookingId, int userId) {
        Booking booking = bookingRepository.findByIdByOwnerId(bookingId, userId)
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
        final User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + bookerId + " does not exist"));
        final Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("вещь не найдена"));
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Время начала бронирования после окончания");
        }
        if (Objects.equals(booker.getId(), item.getOwner().getId())) {
            throw new NotFoundException("Невозможно забронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        Booking booking1 = mapper.toBooking(booker, item, booking, StatusBooking.WAITING);
        return mapper.toBookingForUser(bookingRepository.save(booking1));
    }

    @Transactional
    @Modifying
    @Override
    public BookingForUser updateStatus(long bookingId, int userId, Boolean approved) {
        Booking update;
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (userId == booking.getItem().getOwner().getId()) {
            validateStatusOwner(approved, booking);
            update = bookingRepository.save(booking);
            return mapper.toBookingForUser(update);
        }
        if (userId == booking.getBooker().getId()) {
            validateStatusBooker(approved, booking);
            update = bookingRepository.save(booking);
            return mapper.toBookingForUser(update);
        }
        throw new NotFoundException("Вы не можете изменить бронирование, недостаточно прав.");
    }


    @Transactional(readOnly = true)
    @Override
    public List<BookingForUser> getBookingsOwner(int ownerId, String stateParam) {
        final User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + ownerId + " does not exist"));
        StatusBooking status = StatusBooking.from(stateParam);
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(ownerId);
                break;

            case CURRENT:
                bookings = bookingRepository.findAllOwnerCurrent(ownerId);
                break;

            case FUTURE:
                bookings = bookingRepository.findAllOwnerFuture(ownerId);
                break;

            case PAST:
                bookings = bookingRepository.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(owner, LocalDateTime.now());
                System.out.println(LocalDateTime.now());
                break;

            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(ownerId, StatusBooking.REJECTED);
                break;

            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(ownerId, StatusBooking.WAITING);
                break;

            case APPROVED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(ownerId, StatusBooking.APPROVED);
               break;
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
        return mapper.toMapForUsers(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingForUser> getBookingsBooker(int bookerId, String stateParam) {
        StatusBooking status = StatusBooking.from(stateParam);
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + bookerId + " does not exist"));
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(bookerId);
                break;

            case CURRENT:
                bookings = bookingRepository.findAllByCurrent(bookerId);
                break;

            case FUTURE:
                bookings = bookingRepository.findAllByFuture(bookerId);
                break;

            case PAST:
                bookings = bookingRepository.findAllByPast(bookerId);
                break;

            case REJECTED:
                bookings = bookingRepository.findAllByBookerStatus(bookerId, StatusBooking.REJECTED);
                break;

            case WAITING:
                bookings = bookingRepository.findAllByBookerStatus(bookerId, StatusBooking.WAITING);
                break;

            case APPROVED:
                bookings = bookingRepository.findAllByBookerStatus(bookerId, StatusBooking.APPROVED);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
        return mapper.toMapForUsers(bookings);
    }
}
