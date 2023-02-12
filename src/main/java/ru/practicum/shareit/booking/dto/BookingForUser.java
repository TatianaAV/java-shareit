package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.annotation.StartBeforeEnd;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.PrePersist;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@StartBeforeEnd
public class BookingForUser {

    private Long id;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp start;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp end;

    private StatusBooking status;

    private User booker;

    private Item item;
    @PrePersist
    //@PreUpdate
    private void setStatus() {
        if (this.status.equals(StatusBooking.APPROVED)) {
            if (this.end != null && this.end.before(Timestamp.valueOf(LocalDateTime.now()))) {
                this.status = StatusBooking.PAST;
            }
            if (this.start != null && this.start.after(Timestamp.valueOf(LocalDateTime.now()))) {
                this.status = StatusBooking.FUTURE;
            } else {
                if (this.start != null && this.start.before(Timestamp.valueOf(LocalDateTime.now()))
                        && this.end.after(Timestamp.valueOf(LocalDateTime.now()))) {
                    this.status = StatusBooking.CURRENT;
                }
            }
        }
    }}
