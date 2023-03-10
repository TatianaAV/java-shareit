package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.GetBookings;

import java.util.List;

public interface BookingService {

    BookingForUser getById(long bookingId, long ownerId);

    BookingForUser updateStatus(long bookingId, long userId, Boolean approved);

    List<BookingForUser> getBookingsBooker(GetBookings req);

    BookingForUser add(long userId, BookingDto booking);

    List<BookingForUser> getBookingsOwner(GetBookings req);
}
