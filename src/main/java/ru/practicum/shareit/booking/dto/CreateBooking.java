package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Validated
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBooking {

    @FutureOrPresent(message = "Время начала не может быть раньше текущего времени")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    @NotNull
    private LocalDateTime start;

    @Future(message = "Время окончания не может быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}

