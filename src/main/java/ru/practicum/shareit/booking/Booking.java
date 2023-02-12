package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {

    /*   @Id
       @SequenceGenerator(name = "pk_sequence", schema = "public", sequenceName = "bookings_id_seq", allocationSize = 1)
       @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "start")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone ="Europe/Moscow")
    private LocalDateTime start;

    @Column(name = "finish")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone ="Europe/Moscow")
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private StatusBooking status;

    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

}
