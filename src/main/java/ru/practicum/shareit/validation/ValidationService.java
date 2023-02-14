package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;

@RequiredArgsConstructor
@Slf4j
@Service
public class ValidationService {

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

