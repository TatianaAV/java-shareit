package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime end;

    private StatusBooking status;

    private Integer bookerId;

    private Long itemId;
}

