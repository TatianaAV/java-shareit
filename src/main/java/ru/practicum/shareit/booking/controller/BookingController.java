package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final String requestHeader = "X-Sharer-User-Id";
    private final BookingService itemService;

    @GetMapping
    public List<BookingDto> getBookings(@RequestHeader(name = requestHeader) int userId,
                                        @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("getBookings");
        Optional<StatusBooking> state = StatusBooking.from(stateParam);
        StatusBooking statusBooking = state.orElseThrow(() -> new IllegalArgumentException("Unknow state: " + stateParam));
        //exeption здесь нельзя?
        return itemService.getBookingsOwner(userId, statusBooking);
    }

   /* @GetMapping("/search")
    public List<Booking> search(@RequestParam(required = false) String text) {
        return itemService.search();
    }

    @GetMapping("/{id}")
    public Booking getById(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        return itemService.getById();
    }

    @PostMapping
    public Booking create(@RequestHeader(name = requestHeader) Integer userId, @Valid @RequestBody Booking booking) {
        return itemService.add();
    }

    @PatchMapping("/{id}")
    public Booking update(@RequestHeader(name = requestHeader) int userId, @PathVariable("id") long itemId,
                          @Valid @RequestBody UpdateItemDto item) {
        return itemService.update();
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(name = requestHeader) int userId, @PathVariable long id) {
        itemService.delete();
    }*/
}

