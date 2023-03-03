package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookings;
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

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;

    @Override
    public BookingForUser getById(long bookingId, int userId) {
        Booking booking = bookingRepository.findByIdByOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено или вы не можете получить бронирование, недостаточно прав."));
        return mapper.toBookingForUser(booking);
    }

    @Transactional
    @Override
    public BookingForUser add(int bookerId, CreateBookingDto booking) {
        final User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + bookerId + " does not exist"));
        final Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("вещь не найдена"));

        if (booking.getEnd().equals(booking.getStart())) {
            throw new ValidationException("Время конца бронирования не может быть временем начала");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Время начала бронирования после окончания");
        }
        if (Objects.equals(booker.getId(), item.getOwner().getId())) {
            throw new NotFoundException("Невозможно забронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        Booking booking1 = mapper.toBooking(booking, booker, item);
        booking1.setStatus(StatusBooking.WAITING);
        return mapper.toBookingForUser(bookingRepository.save(booking1));
    }

    @Transactional
    @Override
    public BookingForUser updateStatus(long bookingId, int userId, Boolean approved) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (userId == booking.getItem().getOwner().getId()) {
            if (approved) {
                if (booking.getStatus() == StatusBooking.WAITING) {
                    booking.setStatus(StatusBooking.APPROVED);
                } else {
                    throw new ValidationException("Бронирование уже было одобрено");
                }
            } else if (booking.getStatus() == StatusBooking.WAITING) {
                booking.setStatus(StatusBooking.REJECTED);
            }
            return mapper.toBookingForUser(booking);
        }

        if (userId == booking.getBooker().getId()) {
            if (!approved && (booking.getStatus().equals(StatusBooking.WAITING)
                    || booking.getStatus().equals(StatusBooking.APPROVED))) {
                    booking.setStatus(StatusBooking.CANCELLED);
            } else {
                throw new NotFoundException("Бронирование может быть одобрено только владельцем");
            }
            return mapper.toBookingForUser(booking);
        }
        throw new NotFoundException("Вы не можете изменить бронирование, недостаточно прав.");
    }

    @Override
    public List<BookingForUser> getBookingsOwner(GetBookings req) {
        StatusBooking status = StatusBooking.from(req.getStateParam());
        final User owner = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id: " + req.getUserId() + " does not exist"));


        PageRequest pageRequest = req.getPageRequest();

        log.info("pageRequest FROM {}, SIZE {}", pageRequest.getPageNumber(), pageRequest.getPageSize());

        Page<Booking> bookings = null;
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(owner, pageRequest);
                log.info("Page<Bookings> OWNER ALL  {}", bookings.getSize());
                break;

            case CURRENT:
                bookings = bookingRepository.findAllOwnerCurrent(req.getUserId(), pageRequest);
                log.info("Page<Bookings> OWNER CURRENT  {}", bookings.getSize());
                break;

            case FUTURE:
                bookings = bookingRepository.findAllOwnerFuture(req.getUserId(), pageRequest);
                log.info("Page<Bookings> OWNER FUTURE  {}", bookings.getSize());
                break;

            case PAST:
                bookings = bookingRepository.findBookingsByItemOwnerAndEndBefore(owner, LocalDateTime.now(), pageRequest);
                log.info("Page<Bookings> OWNER PAST  {}",bookings.getSize());
                break;

            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(req.getUserId(), StatusBooking.REJECTED, pageRequest);
                log.info("Page<Bookings> OWNER REJECTED  {}", bookings.getSize());
                break;

            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(req.getUserId(), StatusBooking.WAITING, pageRequest);
                log.info("Page<Bookings> OWNER WAITING  {}", bookings.getSize());
                break;

            case APPROVED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusEquals(req.getUserId(), StatusBooking.APPROVED, pageRequest);
                log.info("Page<Bookings> OWNER APPROVED  {}", bookings.getSize());
                break;

        }
        log.info("Page<Bookings> booker size {}", bookings);
        List<Booking> bookingsForUsers = mapper.mapToItemDto(bookings);
        log.info("List<Booking> booker size {}", bookingsForUsers);

        return mapper.toMapForUsers(bookingsForUsers);
    }

    @Override
    public List<BookingForUser> getBookingsBooker(GetBookings req) {
        StatusBooking status = StatusBooking.from(req.getStateParam());
        final User booker = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id: " + req.getUserId() + " does not exist"));

        PageRequest pageRequest = req.getPageRequest();

        log.info("установлено page {}, size {}", pageRequest.getPageNumber(), pageRequest.getPageSize());
        Page<Booking> bookings = null;
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(booker.getId(), pageRequest);
                log.info("Page<Bookings> BOOKER ALL  {}", bookings);
                log.info("PageRequest BOOKER ALL  {}", pageRequest);
                break;

            case CURRENT:
                bookings = bookingRepository.findAllByCurrent(req.getUserId(), pageRequest);
                break;

            case FUTURE:
                bookings = bookingRepository.findAllByFuture(req.getUserId(), pageRequest);
                break;

            case PAST:
                bookings = bookingRepository.findAllByPast(req.getUserId(), pageRequest);
                break;

            case REJECTED:
                bookings = bookingRepository.findAllByBookerStatus(req.getUserId(), StatusBooking.REJECTED, pageRequest);
                break;

            case WAITING:
                bookings = bookingRepository.findAllByBookerStatus(req.getUserId(), StatusBooking.WAITING, pageRequest);
                break;

            case APPROVED:
                bookings = bookingRepository.findAllByBookerStatus(req.getUserId(), StatusBooking.APPROVED, pageRequest);
                break;
 }
        log.info("Page<Bookings> booker size {}", bookings);
        List<Booking> bookingsForUsers = mapper.mapToItemDto(bookings);
        log.info("List<Booking> booker size {}", bookingsForUsers);

        return mapper.toMapForUsers(bookingsForUsers);
    }
}
