package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select  b from Booking b " +
            "where b.id = ?1  " +
            "and (b.booker.id = ?2 or b.item.owner.id = ?2)")
    Optional<Booking> findByIdByOwnerId(long bookingId, long booker);

    //addComment PAST find booking item booker past
    @Query(nativeQuery = true, value = "SELECT * FROM bookings " +
            "WHERE booker_id = ?1  AND item_id= ?2 " +
            "AND status =  'APPROVED'   AND  finish < ?3 " +
            "LIMIT 1")
    Optional<Booking> findBookingByBookerAndItem(long bookerId, long itemId, LocalDateTime currentTime);

    // BOOKER ALL
    @Query(value = "SELECT b FROM Booking b " +
            " WHERE b.booker.id = ?1")
    Page<Booking> findAllByBooker(Long booker, PageRequest request);

    // BOOKER PAST
    @Query(nativeQuery = true, value = " SELECT * FROM bookings b " +
            "WHERE b.booker_id = ? " +
            "AND CURRENT_TIMESTAMP > b.finish AND b.status = 'APPROVED' ")
    Page<Booking> findAllByPast(long bookerId, PageRequest request);

    // BOOKER CURRENT
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1  AND CURRENT_TIMESTAMP BETWEEN b.start AND  b.end  ")
    Page<Booking> findAllByCurrent(long booker, PageRequest request);

    // BOOKER FUTURE
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1  " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND (b.status = 'APPROVED' OR  b.status = 'WAITING')")
    Page<Booking> findAllByFuture(long bookerId, PageRequest request);

    //BOOKER  APPROVE, WAITING, REJECTED
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1  AND b.status = ?2")
    Page<Booking> findAllByBookerStatus(long booker, StatusBooking status, PageRequest request);

    //OWNER ALL
   @Query(value = "SELECT b FROM Booking b " +
            " WHERE b.item.owner = ?1")
    Page<Booking> findAllByItemOwner(User owner, PageRequest request);
  //  Page<Booking> findByItem_OwnerOrderByStartDesc(User owner, PageRequest request);

    //OWNER PAST
  Page<Booking> findBookingsByItemOwnerAndEndBefore(User owner, LocalDateTime now, PageRequest reg);

    //OWNER CURRENT
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    Page<Booking> findAllOwnerCurrent(long ownerId, PageRequest reg);

    //OWNER FUTURE
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 " +
            "AND b.start > CURRENT_TIMESTAMP AND (b.status = 'APPROVED' OR  b.status = 'WAITING')")
    Page<Booking> findAllOwnerFuture(long ownerId, PageRequest reg);

    //owner APPROVE, WAITING, REJECTED
    Page<Booking> findAllByItemOwnerIdAndStatusEquals(long ownerId, StatusBooking status, PageRequest reg);

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
