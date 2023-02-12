package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.StatusBookingException;

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
    private final BookingService bookingService;

    @PostMapping
    public BookingForUser create(@RequestHeader(name = requestHeader) Integer booker, @Validated @RequestBody CreateBooking booking) {
        log.info("\n createBooking booker {}, itemId {}\n", booker, booking.getItemId());
        System.out.println(booking.getStart());
        return bookingService.add(booker, booking);
        /*
    Добавление нового запроса на бронирование.
    Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
     Эндпоинт — POST /bookings.
      После создания запрос находится в статусе WAITING — «ожидает подтверждения».*/
    }

    @GetMapping("/{bookingId}")
    public BookingForUser getById(@RequestHeader(name = requestHeader) Integer userId,
                                  @PathVariable(name = "bookingId") Long bookingId) {
        log.info("\n getById booker {}, bookingId {}\n", userId, bookingId);
        return bookingService.getById(bookingId, userId);
      /*  Получение данных о конкретном бронировании (включая его статус).
                Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.
                Эндпоинт — GET /bookings/{bookingId}.*/
    }

    @PatchMapping("/{bookingId}")
    public BookingForUser updateStatus(@RequestHeader(name = requestHeader) Integer userId,
                                       @RequestParam(name = "approved") Boolean approved,
                                       @PathVariable Long bookingId) {
        log.info("updateState");
        return bookingService.updateStatus(bookingId, userId, approved);
        /*  Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
    Затем статус бронирования становится либо APPROVED, либо REJECTED.
    Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать значения true или false.*/
    }

    @GetMapping
    public List<BookingForUser> getBookings(@RequestHeader(name = requestHeader) int booker,
                                            @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("getBookings booker {}, state {}", booker, stateParam);
        Optional<StatusBooking> status = StatusBooking.from(stateParam);
        StatusBooking statusBooking = status
                .orElseThrow(() -> new StatusBookingException("Unknown state: " + stateParam));
        //exeption здесь нельзя?
        return bookingService.getBookingsBooker(booker, statusBooking);
        /*
    Получение списка всех бронирований текущего пользователя.
    Эндпоинт — GET /bookings?state={state}. Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     Также он может принимать значения CURRENT (англ. «текущие»), **PAST** (англ. «завершённые»), FUTURE (англ. «будущие»),
      WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
       Бронирования должны возвращаться отсортированными по дате от более новых к более старым.*/
    }

    @GetMapping("/owner")
    public List<BookingForUser> getBookingsByOwnerStatus(@RequestHeader(name = requestHeader) Integer owner,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        Optional<StatusBooking> status = StatusBooking.from(stateParam);
        StatusBooking statusBooking = status
                .orElseThrow(() -> new StatusBookingException("Unknown state: " + stateParam));

        return bookingService.getBookingsOwner(owner, statusBooking);
        /*
    Получение списка бронирований для всех вещей текущего пользователя.
    Эндпоинт — GET /bookings/owner?state={state}. Этот запрос имеет смысл для владельца хотя бы одной вещи.
    Работа параметра state аналогична его работе в предыдущем сценарии.
*/
    }
}

