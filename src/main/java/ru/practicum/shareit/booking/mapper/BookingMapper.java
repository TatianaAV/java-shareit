package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface BookingMapper {
    BookingDto toDto(Booking booking);
}
