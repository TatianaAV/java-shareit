package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ValidationService validationService;
    private final ItemRepository repository;
    private final ItemMapper mapper;
    private final UserService userService;

    @Override
    public Item getById(long id, int userId) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id: " + id + " does not exist"));
    }

    @Override
    public void delete(long id, int userId) {
        repository.delete(id);
    }

    @Override
    public Item add(int userId, CreateItemDto item) {
        validationService.userValidateExist(userId);
        Item newItem = mapper.createItemDtoToItem(userService.getUserById(userId),item);
        validationService.validateCreate(userId, newItem);
        return repository.create(newItem)
                .orElseThrow(() -> new NotFoundException("Item with id: " + userId + " does not exist"));
    }

    @Override
    public List<Item> search(String text) {
        return repository.search(text);
    }

    @Override
    public List<Item> getAll(int userId) {
        log.info("item getAll user id {} ", userId);
        if (userId > 0) {
            return repository.getAllByOwnerId(userId);
        }
        return repository.findAll();
    }

    @Override
    public Item update(long itemId, int userId, UpdateItemDto itemDto) {
        Item item = mapper.toItem(itemId, itemDto);
        validationService.userValidateExist(userId);
        log.info("item update user id {} exist", userId);
        validationService.validateUpdate(userId, item);
        log.info("item update user id {} exist item id {}", userId, item.getId());
        return repository.update(itemId, item)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " does not exist"));
    }
}
