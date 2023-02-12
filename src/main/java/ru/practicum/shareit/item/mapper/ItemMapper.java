package ru.practicum.shareit.item.mapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.List;


@Mapper(componentModel = "spring", uses = {UserMapper.class, BookingMapper.class})
public interface ItemMapper {

    @Mapping(target = "owner.id", source = "user.id")
    @Mapping(target = "owner.name", source = "user.name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    Item createItemDtoToItem(UserDto user, CreateItemDto dto);

    @Mapping(target = "id", source = "itemId")
    Item toItem(long itemId, UpdateItemDto itemDto);

    ItemDto toItemDto(Item item);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "owner.id", source = "owner1.id")
    Item toItem(UserDto owner1, Item item);

    List<ItemDto> mapItemDto(List<Item> items);

    @Mapping(target = "id", ignore = true)
    Item toItem(@MappingTarget Item item, UpdateItemDto itemUpdate);

    @Mapping(target = "id", source = "item.id")
    ItemForOwnerDto toItemForOwnerDto(Item item, List<Comment> comments, BookingDto lastBooking, BookingDto nextBooking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "author1")
    @Mapping(target = "item", source = "item1")
    @Mapping(target = "created", ignore = true)
    Comment toComment(User author1, Item item1, CommentCreate comment);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "authorName", source = "comment.author.name")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "created", source = "comment.created")
    CommentDto toCommentDto(Comment comment);
}
