package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.ap.internal.util.Strings.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    final ItemRepository repository;
    final ItemMapper mapper;
    final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id: " + id + " does not exist"));
        return mapper.toItemDto(item);
    }

    @Transactional
    @Override
    public void delete(long id, int userId) {
        repository.deleteItemByIdAndOwnerId(id, userId);
    }

    @Transactional
    @Override
    public ItemDto add(int userId, CreateItemDto item) {
        Item newItem = mapper.createItemDtoToItem(userService.getUserById(userId), item);
        return mapper.toItemDto(repository.save(newItem));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text, Integer userId) {
        log.info("\n item search user id {}, search text {} \n ", userId, text);
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return mapper.mapItemDto(repository.search(text.trim().toUpperCase()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAll(int userId) {
        log.info("item getAll user id {} ", userId);
        final UserDto owner = userService.getUserById(userId);
         List<Item> items = repository.findAll().stream()
                 .filter(item -> item.getOwner().getId() == owner.getId())
                 .collect(Collectors.toList());
            return mapper.mapItemDto(items);
}

    @Transactional
    @Override
    public ItemDto update(long itemId, int userId, UpdateItemDto itemUpdate) {
        final UserDto owner = userService.getUserById(userId);
        final Item updateItem = repository.findByIdAndOwner_Id(itemId, owner.getId())
                .orElseThrow(() -> new NotFoundException("User with id: " + itemId + " does not exist"));

        if ( itemUpdate == null ) {
            return mapper.toItemDto(updateItem);
        }

        if ( isNotEmpty( itemUpdate.getName() ) ) {
            updateItem.setName( itemUpdate.getName() );
        }

        if ( isNotEmpty( itemUpdate.getDescription() ) ) {
            updateItem.setDescription( itemUpdate.getDescription() );
        }
        if (itemUpdate.getAvailable() != null) {
            updateItem.setAvailable( itemUpdate.getAvailable() );
        }
        return mapper.toItemDto(repository.save(updateItem));
    }
}
