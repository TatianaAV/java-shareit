package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;

import java.util.List;

public interface BookingService{


    BookingForUser getById(Long bookingId, Integer ownerId);

    BookingForUser updateStatus(Long bookingId, Integer userId, Boolean approved);

    List<BookingForUser> getBookingsBooker(int userId, StatusBooking state);

    BookingForUser add(Integer userId, CreateBooking booking);

    List<BookingForUser> getBookingsOwner(int owner, StatusBooking status);
}
