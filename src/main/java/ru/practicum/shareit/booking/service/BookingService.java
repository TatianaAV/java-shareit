package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService{


    BookingDto getById();

    void delete();

    List<BookingDto> search();

    List<BookingDto> getBookingsOwner();

    BookingDto update(Booking booking);

    List<BookingDto> getBookingsOwner(int userId, StatusBooking state);

    BookingDto add(Integer userId, Booking booking);
}
