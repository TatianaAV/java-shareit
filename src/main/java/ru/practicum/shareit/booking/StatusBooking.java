package ru.practicum.shareit.booking;

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

    public static Optional<StatusBooking> from(String stateParam) {
        for (StatusBooking value : StatusBooking.values()){
            if (value.name().equals(stateParam)){
                return Optional.of(value);
            }
        } return Optional.empty();
    }
}

