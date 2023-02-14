package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.CreateBooking;
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
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class ValidationService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public Item validateItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("вещь не найдена"));
    }

    public Booking validateBooking(int bookerId, long itemId, LocalDateTime now) {
        return bookingRepository
                .findBookingByBookerAndItem(bookerId, itemId, now)
                .orElseThrow(() -> new ValidationException("Booking with itemId : " + itemId +
                        ", bookerId " + bookerId));
    }

    public Booking validateBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    public User validateUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist"));

    }


    public void validateAddBooking(CreateBooking booking, User booker, Item item) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Время начала бронирования после окончания");
        }
        if (Objects.equals(booker.getId(), item.getOwner().getId())) {
            throw new NotFoundException("Невозможно забронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
    }

    public static void validateStatusBooker(Boolean approved, Booking booking) {
        if (!approved && !booking.getStatus().equals(StatusBooking.CURRENT)
                && (booking.getStatus().equals(StatusBooking.WAITING)
                || booking.getStatus().equals(StatusBooking.APPROVED))) {
            booking.setStatus(StatusBooking.CANCELLED);
        }
        if (approved) {
            throw new NotFoundException("Бронирование может быть одобрено только владельцем");
        }
    }

    public static void validateStatusOwner(Boolean approved, Booking booking) {
        if (approved && booking.getStatus().equals(StatusBooking.APPROVED)) {
            throw new ValidationException("Бронирование уже было одобрено");
        }

        if (approved && booking.getStatus().equals(StatusBooking.WAITING)) {
            booking.setStatus(StatusBooking.APPROVED);
        }

        if (!approved && booking.getStatus().equals(StatusBooking.WAITING)) {
            booking.setStatus(StatusBooking.REJECTED);
        }
    }
}

