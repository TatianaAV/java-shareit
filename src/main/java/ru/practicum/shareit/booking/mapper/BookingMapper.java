package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface BookingMapper {
    BookingDto toDto(Booking booking);

    @Mapping(target = "booker.id", source = "bookerId")
    @Mapping(target = "item.id", source = "booking.item.id")
    Booking toBooking(Integer bookerId, Booking booking);
}
