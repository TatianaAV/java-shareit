package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.dto.itemdto.CreateItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemForOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    Item createItemDtoToItem(User user, CreateItemDto dto);

    ItemDto toItemDto(Item item);

    List<ItemForOwnerDto> mapItemForBookerDto(List<Item> items);

    @Mapping(target = "id", source = "item.id")
    ItemForOwnerDto toItemForOwnerDto(Item item, List<CommentDto> comments, BookingDto lastBooking, BookingDto nextBooking);

    @Mapping(target = "id", source = "item.id")
    ItemForOwnerDto toItemForBookerDto(Item item, List<CommentDto> comments);

    List<ItemDto> mapItemDto(List<Item> items);
}
