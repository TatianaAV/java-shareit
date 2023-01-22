package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@Mapper(componentModel = "spring", uses = User.class)
public interface ItemMapper {

    @Mapping(target = "owner.id", source = "user.id")
    @Mapping(target = "owner.name", source = "user.name")
    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "name", source = "dto.name")
    Item createItemDtoToItem(User user, CreateItemDto dto);

    @Mapping(target = "id", source = "itemId")
    Item toItem(long itemId, UpdateItemDto itemDto);
}
