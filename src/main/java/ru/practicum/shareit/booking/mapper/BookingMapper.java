package ru.practicum.shareit.booking.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, ItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {

    @Mapping(target = "bookerId", source = "booker.id")
    @Mapping(target = "itemId", source = "item.id")
    BookingDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    Booking toBooking(CreateBooking booking, User booker, Item item);

    List<BookingForUser> toMapForUsers(List<Booking> bookings);

    @Mapping(target = "item.id", source = "item.id")
    @Mapping(target = "item.name", source = "item.name")
    @Mapping(target = "booker.id", source = "booker.id")
    @Mapping(target = "booker.name", source = "booker.name")
    BookingForUser toBookingForUser(Booking booking);

    List<Booking> mapToItemDto(Page<Booking> bookings);
}
