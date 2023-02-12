package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    @Mapping(target = "booker", source = "booker.id")
    @Mapping(target = "item", source = "item.id")
    BookingDto toDto(Booking booking);

    @Mapping(target = "booker", source = "booking.booker")
  //  @Mapping(target = "booker", source = "booking.booker")
  //  @Mapping(target = "booker", source = "booking.booker")
    @Mapping(target = "item.id", source = "booking.item.id")
    @Mapping(target = "item.name", source = "booking.item.name")
    @Mapping(target = "item.owner", source = "booking.item.owner")
    BookingForUser toBookingForUser(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker.id", source = "booker.id")
    @Mapping(target = "booker.name", source = "booker.name")
    @Mapping(target = "booker.email", source = "booker.email")
    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "item.name", source = "item.name")
    @Mapping(target = "item.owner", source = "item.owner")
    Booking toBooking(User booker, Item item, CreateBooking booking);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "booker", source = "booking.booker")
    @Mapping(target = "item", source = "item1")
    @Mapping(target = "item.owner", source = "owner")
    Booking toBooking(User owner, Item item1, Booking booking);

    List<BookingDto> toMapDto(List<Booking> bookings);
    List<BookingForUser> toMapForUsers(List<Booking> bookings);
}
