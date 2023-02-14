package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exeption.StatusBookingException;

import java.util.Optional;

public enum StatusBooking {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    CANCELLED,
    WAITING,
    APPROVED;

    public static StatusBooking from(String stateParam) {
        for (StatusBooking value : StatusBooking.values()) {
            if (value.name().equals(stateParam)) {
                return Optional.of(value)
                        .orElseThrow(() -> new StatusBookingException("Unknown state: " + stateParam));
            }
        }
        throw new StatusBookingException("Unknown state: " + stateParam);
    }
}

