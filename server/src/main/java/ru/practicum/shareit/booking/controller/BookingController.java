package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.GetBookings;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingForUser create(@RequestHeader(name = requestHeader) long booker,
                                 @RequestBody BookingDto booking) {
        log.info(" create BOOKING userId {}, booking.getItemId {}", booker, booking.getItemId());
        return bookingService.add(booker, booking);
    }

    @GetMapping("/{bookingId}")
    public BookingForUser getById(@RequestHeader(name = requestHeader) long userId,
                                  @PathVariable(name = "bookingId") Long bookingId) {
        log.info(" getById USER {}, bookingId {}", userId, bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingForUser updateStatus(@RequestHeader(name = requestHeader) long userId,
                                       @RequestParam(name = "approved") Boolean approved,
                                       @PathVariable long bookingId) {
        log.info(" UPDATE STATUS USER {}, approved {}, bookingId {}", userId, approved, bookingId);
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingForUser> getBookings(@RequestHeader(name = requestHeader) long booker,
                                            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info(" get bookings booker from {}, size {}, stateParam {}", from, size, stateParam);
        return bookingService.getBookingsBooker(GetBookings.of(booker, PageRequest.of(from / size, size, Sort.by("start").descending()), stateParam));
    }


    @GetMapping("/owner")
    public List<BookingForUser> getBookingsByOwnerStatus(@RequestHeader(name = requestHeader) long ownerId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info(" get bookings owner from {}, size {}, stateParam {}", from, size, stateParam);
        return bookingService.getBookingsOwner(GetBookings.of(ownerId, PageRequest.of(from / size, size, Sort.by("start").descending()), stateParam));
    }
}

