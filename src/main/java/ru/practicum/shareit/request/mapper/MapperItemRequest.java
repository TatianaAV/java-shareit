package ru.practicum.shareit.request.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.AddItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface MapperItemRequest {


    List<ItemRequestDto> toMapItemRequestDto(List<ItemRequest> requests);

    ItemRequest toItemRequest(ItemRequestDto requestDto);

    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "requestor", source = "requestor")
    ItemRequest toItemRequest(User requestor, AddItemRequest requestDto);

    @Mapping(target = "id", source = "requestId")
    ItemRequestDto toItemRequestDto(ItemRequest request);

    @Mapping(target = "id", source = "request.requestId")
    ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items);

    List<ItemRequestDto.Item> toItemsRequest(List<Item> items);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemRequestDto.Item toItem(Item Item);

    List<ItemRequestDto> mapToItemDto(Page<ItemRequest> items);
}
