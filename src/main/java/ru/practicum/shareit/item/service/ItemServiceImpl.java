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
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    final ItemRepository repository;
    final ItemMapper mapper;
    final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(long id, int userId) {
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
    public List<ItemDto> search(String text) {
        return repository.search(text);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAll(int userId) {
        log.info("item getAll user id {} ", userId);
        if (userId > 0) {
            return repository.findAllByOwnerId(userId);
        }
        return mapper.mapItemDto(repository.findAll());
    }

    @Transactional
    @Override
    public ItemDto update(long itemId, int userId, UpdateItemDto itemUpdate) {
        final Item updateItem = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("User with id: " + itemId + " does not exist"));
        Item item = mapper.toItem(itemId, itemUpdate);
        if (item.getName() != null && !item.getName().isEmpty()) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        return mapper.toItemDto(repository.save(item));
    }
}
