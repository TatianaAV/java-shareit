package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.annotation.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@StartBeforeEnd
public class BookingDto {

    private Long id;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp start;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp end;

    private StatusBooking status;

    private Integer booker;

    private Long item;
}
