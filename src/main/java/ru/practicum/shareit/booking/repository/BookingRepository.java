package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select  b from Booking b " +
            "where b.id = ?1  " +
            "and (b.booker.id = ?2 or b.item.owner.id = ?2)")
    Optional<Booking> findByIdByOwnerId(long bookingId, int booker);

    //addComment PAST find booking item booker past
    @Query(nativeQuery = true, value = "SELECT * FROM bookings " +
            "WHERE booker_id = ?1  AND item_id= ?2 " +
            "AND status =  'APPROVED'   AND  finish < ?3 " +
            "LIMIT 1")
    Optional<Booking> findBookingByBookerAndItem(int bookerId, long itemId, LocalDateTime currentTime);

    // BOOKER ALL
    @Query(nativeQuery = true, value = "SELECT * FROM bookings " +
            "WHERE  booker_id = ? " +
            "AND status IN ('APPROVED', 'WAITING' ) ORDER BY start DESC")
    List<Booking> findAllByBooker(int bookerId);

    // BOOKER PAST
    @Query(nativeQuery = true, value = " SELECT * FROM bookings b " +
            "WHERE b.booker_id = ? " +
            "AND CURRENT_TIMESTAMP > b.finish AND b.status = 'APPROVED' " +
            "ORDER BY b.start")
    List<Booking> findAllByPast(int bookerId);

    // BOOKER CURRENT
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1  AND CURRENT_TIMESTAMP BETWEEN b.start AND  b.end  ")
    List<Booking> findAllByCurrent(int booker);

    // BOOKER FUTURE
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1  " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND (b.status = 'APPROVED' OR  b.status = 'WAITING')" +
            "ORDER BY b.start desc ")
    List<Booking> findAllByFuture(int bookerId);

    //BOOKER  APPROVE, WAITING, REJECTED
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1  AND b.status = ?2")
    List<Booking> findAllByBookerStatus(int booker, StatusBooking status);

    //OWNER ALL
    @Query(value = "SELECT b FROM Booking b " +
            " WHERE b.item.owner.id = ?1 AND (b.status = 'APPROVED' OR  b.status = 'WAITING') " +
            " ORDER BY b.start DESC")
    List<Booking> findAllByItemOwner(int ownerId);

    //OWNER PAST
    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now);

    //OWNER CURRENT
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findAllOwnerCurrent(int ownerId);

    //OWNER FUTURE
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 " +
            "AND b.start > CURRENT_TIMESTAMP AND (b.status = 'APPROVED' OR  b.status = 'WAITING') " +
            "ORDER BY b.start DESC")
    List<Booking> findAllOwnerFuture(int ownerId);

    //owner APPROVE, WAITING, REJECTED
    List<Booking> findAllByItemOwnerIdAndStatusEquals(int ownerId, StatusBooking status);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings b " +
            "WHERE b.item_id = ? " +
            "AND CURRENT_TIMESTAMP >= b.START AND b.status = 'APPROVED' " +
            "ORDER BY b.START DESC LIMIT 1")
    Booking findLast(long itemId);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings b  " +
            "WHERE b.item_id = ?1 " +
            "AND b.start > CURRENT_TIMESTAMP AND b.status = 'APPROVED' " +
            "ORDER BY b.start LIMIT 1")
    Booking findNext(long id);

    @Query(nativeQuery = true, value = "select distinct * from BOOKINGS b " +
            "where b.ITEM_ID in (?1) and ?2 >= b.START and b.status = 'APPROVED' order by b.START desc LIMIT 1")
    List<Booking> findListLast(List<Item> items, LocalDateTime end);

    @Query(nativeQuery = true, value = "select distinct * from BOOKINGS b " +
            "where b.ITEM_ID in (?1) and b.START > ?2  and b.STATUS = 'APPROVED' order by b.START LIMIT 1")
    List<Booking> findListNext(List<Item> itemByOwner, LocalDateTime start);
}
