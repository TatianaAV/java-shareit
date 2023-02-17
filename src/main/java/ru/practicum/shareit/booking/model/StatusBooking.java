package ru.practicum.shareit.booking.model;

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
                return value;
            }
        }
        throw new IllegalStateException("Unknown state: " + stateParam);
    }
}

