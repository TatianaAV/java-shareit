package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.dto.GetBookings;

import java.util.List;

public interface BookingService {

    BookingForUser getById(long bookingId, int ownerId);

    BookingForUser updateStatus(long bookingId, int userId, Boolean approved);

    List<BookingForUser> getBookingsBooker(GetBookings req);

    BookingForUser add(int userId, CreateBooking booking);

    List<BookingForUser> getBookingsOwner(GetBookings req);
}
