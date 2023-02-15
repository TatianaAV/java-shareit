package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "start")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime start;

    @Column(name = "finish")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private StatusBooking status;

    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;

    @PrePersist
    public void setStatus() {
        if (this.status == null) {
            this.status = StatusBooking.WAITING;
           // return;
        }
   /*     if (this.status.equals(StatusBooking.APPROVED)) {
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
        }*/
    }
}
