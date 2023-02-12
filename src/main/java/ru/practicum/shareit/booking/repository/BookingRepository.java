package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select  b from Booking b " +
            "where b.id = ?1  and (b.booker.id = ?2 or b.item.owner.id = ?2)")
    Optional<Booking> findByIdByOwnerId(Long bookingId, Integer booker);

    /* @Query("select  item from Item item " +
            "where  item.available = true " +
            "and item.name like ?1 " +
            "and item.description like ?1 ")
    List<ItemDto> search(String text);*/

    @Query("select  b from Booking b " +
            "where  b.status = 'WAITING' " +
            "and b.start > ?1 and " +
            " b.end < ?2 ")
    List<Booking> searchBooking(LocalDateTime start, LocalDateTime finish);

    List<Booking> findBookingByItem_OwnerId(int ownerId);


    //addComment PAST
    @Query(nativeQuery = true, value = "SELECT * FROM bookings WHERE  item_id= ?2 AND booker_id = ?1 AND finish < CURRENT_TIMESTAMP ORDER BY finish DESC LIMIT 1")
    Optional<Booking> findBookingByBookerAndItem(int bookerId, long itemId);

    //getBooking for booker
    @Query(nativeQuery = true, value = "SELECT * FROM bookings WHERE  booker_id = ? AND status IN ('APPROVED', 'WAITING' ) ORDER BY start DESC")
    List<Booking> findAllByBooker(int bookerId);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Integer booker_id, Timestamp end);

    @Query("SELECT b FROM Booking b WHERE  b.booker.id = ?1 and b.end <  CURRENT_TIMESTAMP")
    List<Booking> findAllByPast(int bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1  AND CURRENT_TIMESTAMP BETWEEN b.start AND  b.end  ")
    List<Booking> findAllByCurrent(int booker);

    // or b.status = 'WAITING')
    @Query("SELECT b FROM Booking b WHERE b.booker = ?1  " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND (b.status = 'APPROVED' OR  b.status = 'WAITING')" +
            "ORDER BY b.start desc ")
    List<Booking> findAllByFuture(User booker);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1  AND b.status = ?2")
    List<Booking> findAllByBookerStatus(int booker, StatusBooking status);

    //getItem whith Booking
    @Query(nativeQuery = true, value = "SELECT * FROM bookings" +
            "             WHERE item_id = 2 AND finish < CURRENT_TIMESTAMP" +
            "             ORDER BY finish LIMIT 1")
    Booking findLast(long id);

    @Query(nativeQuery = true, value = "SELECT * FROM bookings" +
            " WHERE item_id = ?1 AND start > CURRENT_TIMESTAMP " +
            " ORDER BY start DESC LIMIT 1")
    Booking findNext(long id);

    //get booking owner
    @Query(value = "SELECT b FROM Booking b " +
            " WHERE b.item.owner.id = ?1 AND (b.status = 'APPROVED' OR  b.status = 'WAITING') " +
            " ORDER BY b.start DESC")
    List<Booking> findAllByItemOwner(int ownerId);

    //Past owner
    @Query("SELECT b FROM Booking b WHERE" +
            " b.end < CURRENT_TIMESTAMP AND b.status = 'APPROVED' AND b.item.owner.id = ?1 " +
            " ORDER BY b.start DESC")
    List<Booking> findAllOwnerPast(int owner);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findAllOwnerCurrent(int ownerId);

    //rejected
    // @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1  AND b.status = ?2")
    List<Booking> findAllByItemOwnerIdAndStatusEquals(int booker, StatusBooking status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 " +
            " AND b.start > CURRENT_TIMESTAMP AND (b.status = 'APPROVED' OR  b.status = 'WAITING') " +
            " ORDER BY b.start DESC")
    List<Booking> findAllOwnerFuture(Integer ownerId);
}
