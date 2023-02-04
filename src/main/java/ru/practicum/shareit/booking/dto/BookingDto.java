package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@StartBeforeEnd
public class BookingDto {

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private  LocalDateTime finish;
}
