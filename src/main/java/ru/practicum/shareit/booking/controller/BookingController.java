package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingForUser create(@RequestHeader(name = requestHeader) int booker, @Valid @RequestBody CreateBooking booking) {
        System.out.println(booking.getStart());
        return bookingService.add(booker, booking);
    }

    @GetMapping("/{bookingId}")
    public BookingForUser getById(@RequestHeader(name = requestHeader) int userId,
                                  @PathVariable(name = "bookingId") Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingForUser updateStatus(@RequestHeader(name = requestHeader) int userId,
                                       @RequestParam(name = "approved") Boolean approved,
                                       @PathVariable long bookingId) {
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingForUser> getBookings(@RequestHeader(name = requestHeader) int booker,
                                            @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        return bookingService.getBookingsBooker(booker, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingForUser> getBookingsByOwnerStatus(@RequestHeader(name = requestHeader) int ownerId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        return bookingService.getBookingsOwner(ownerId, stateParam);
    }
}

