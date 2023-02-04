package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

   /* @Query("select  item from Item item " +
            "where  item.available = true " +
            "and item.name like ?1 " +
            "and item.description like ?1 ")
    List<ItemDto> search(String text);

    @Query("select  b from Booking b " +
            "where  b.state = 'WAITING' " +
            "and b.start > ?1 and " +
            " b.finish < ?2 ")

    List<Booking> searchBooking(LocalDateTime start, LocalDateTime finish);
*/
 //   void deleteItemByIdAndOwnerId(long id, int userId);

   // List<Booking> findAllByOwnerId(int userId);
   List<Booking> findAllByBookerOrderByStartDesc(User booker);

    //List<Booking> findCurrentByBooker(User booker, Timestamp.valueOf(LocalDateTime.now()));
}

