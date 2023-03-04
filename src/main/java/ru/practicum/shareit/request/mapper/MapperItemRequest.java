package ru.practicum.shareit.request.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MapperItemRequest {

    List<ItemRequestDto> toMapItemRequestDto(List<ItemRequest> requests);

    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "requestor", source = "requestor")
    ItemRequest toItemRequest(User requestor, ItemRequestCreateDto requestDto);

    @Mapping(target = "id", source = "requestId")
    ItemRequestDto toItemRequestDto(ItemRequest request);

    @Mapping(target = "id", source = "request.requestId")
    ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items);

    List<ItemRequest> mapToItem(Page<ItemRequest> all);
}
