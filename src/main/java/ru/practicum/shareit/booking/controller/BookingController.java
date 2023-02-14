package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingForUser create(@RequestHeader(name = requestHeader) int booker, @Validated @RequestBody CreateBooking booking) {
        log.info("\n createBooking booker {}, itemId {}\n", booker, booking.getItemId());
        System.out.println(booking.getStart());
        return bookingService.add(booker, booking);
    }

    @GetMapping("/{bookingId}")
    public BookingForUser getById(@RequestHeader(name = requestHeader) int userId,
                                  @PathVariable(name = "bookingId") Long bookingId) {
        log.info("\n getById booker {}, bookingId {}\n", userId, bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingForUser updateStatus(@RequestHeader(name = requestHeader) int userId,
                                       @RequestParam(name = "approved") Boolean approved,
                                       @PathVariable long bookingId) {
        log.info("updateState");
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingForUser> getBookings(@RequestHeader(name = requestHeader) int booker,
                                            @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("getBookings booker {}, state {}", booker, stateParam);
          return bookingService.getBookingsBooker(booker, stateParam);
       }

    @GetMapping("/owner")
    public List<BookingForUser> getBookingsByOwnerStatus(@RequestHeader(name = requestHeader) int ownerId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("getBookings owner {}, state {}", ownerId, stateParam);
        return bookingService.getBookingsOwner(ownerId, stateParam);
    }
}

