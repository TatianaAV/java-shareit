package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;




@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "owner", source = "userId")
    Item createItemDtoToItem(int userId, CreateItemDto dto);

    @Mapping(target = "id", source = "itemId")
    Item toItem(long itemId, UpdateItemDto itemDto);
}
