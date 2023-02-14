package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.PrePersist;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingForUser {

    private Long id;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime start;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime end;

    private StatusBooking status;

    private User booker;

    private Item item;

    @PrePersist
    private void setStatus() {
        if (this.status.equals(StatusBooking.APPROVED)) {
            if (this.end != null && this.end.isBefore(LocalDateTime.now())) {
                this.status = StatusBooking.PAST;
            }
            if (this.start != null && this.start.isAfter(LocalDateTime.now())) {
                this.status = StatusBooking.FUTURE;
            } else {
                if (this.start != null && this.start.isBefore(LocalDateTime.now())
                        && this.end.isAfter(LocalDateTime.now())) {
                    this.status = StatusBooking.CURRENT;
                }
            }
        }
    }
}
