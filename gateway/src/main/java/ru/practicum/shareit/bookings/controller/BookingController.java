package ru.practicum.shareit.bookings.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.bookings.client.BookingClient;
import ru.practicum.shareit.bookings.dto.CreateBookingDto;
import ru.practicum.shareit.bookings.dto.StatusBooking;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final BookingClient bookingController;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = requestHeader) long booker,
                                         @Valid @RequestBody CreateBookingDto booking) {
        log.info(" create BOOKING userId {}, booking.getItemId {}", booker, booking.getItemId());
        return bookingController.createBooking(booker, booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(name = requestHeader) long userId,
                                          @PathVariable(name = "bookingId") Long bookingId) {
        log.info(" getById USER {}, bookingId {}", userId, bookingId);
        return bookingController.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(name = requestHeader) long userId,
                                               @RequestParam(name = "approved") Boolean approved,
                                               @PathVariable long bookingId) {
        log.info(" UPDATE STATUS USER {}, approved {}, bookingId {}", userId, approved, bookingId);
        return bookingController.approveBooking(bookingId, userId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(name = requestHeader) long booker,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info(" get bookings booker from {}, size {}, stateParam {}", from, size, stateParam);
        return bookingController.getBookings(booker, StatusBooking.from(stateParam), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerStatus(@RequestHeader(name = requestHeader) long ownerId,
                                                           @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info(" get bookings owner from {}, size {}, stateParam {}", from, size, stateParam);
        return bookingController.getOwnerBookings(ownerId, StatusBooking.from(stateParam), from, size);
    }
}

