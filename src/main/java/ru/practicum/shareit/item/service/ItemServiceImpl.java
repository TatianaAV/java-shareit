package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mapstruct.ap.internal.util.Strings.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    final BookingRepository repositoryBooking;
    final ItemRepository repository;
    final UserRepository userRepository;
    final ItemMapper mapper;
    final BookingMapper bookingMapper;
    final UserService userService;
    final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public ItemForOwnerDto getById(long itemId, int ownerId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " does not exist"));
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId);
        ;
        if (item.getOwner().getId().equals(ownerId)) {
            BookingDto lastBooking = bookingMapper.toDto(repositoryBooking.findLast(itemId));
            BookingDto nextBooking = bookingMapper.toDto(repositoryBooking.findNext(itemId));
            return mapper.toItemForOwnerDto(item, comments, lastBooking, nextBooking);
        } else {
            return mapper.toItemForOwnerDto(item, comments, null, null);
        }
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

    @Transactional
    @Override
    public Comment addComment(int bookerId, CommentCreate comment, long itemId) {

        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + bookerId + " does not exist"));
        repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " does not exist"));
        Booking booking =
                repositoryBooking.findBookingByBookerAndItem(bookerId, itemId)
                        .orElseThrow(() -> new ValidationException("Booking with itemId : " + itemId +
                                ", bookerId " + bookerId));

        // if (booking.getStatus().equals(StatusBooking.PAST)) {
        Comment commentCreate = mapper.toComment(booking.getBooker(), booking.getItem(), comment);
        commentCreate.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        return commentRepository.save(commentCreate);
        //  } else throw new ValidationException("Бронирование не завершено, статус ", booking.getStatus().toString());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemForOwnerDto> getAll(int userId) {
        log.info("item getAll user id {} ", userId);
        List<Item> items = repository.findAllByOwnerIdOrderById(userId);
        List<ItemForOwnerDto> itemList = new ArrayList<>();
        for (Item item : items) {
            long id = item.getId();
            List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(id);
            BookingDto lastBooking = bookingMapper.toDto(repositoryBooking.findLast(id));
            BookingDto nextBooking = bookingMapper.toDto(repositoryBooking.findNext(id));
            itemList.add(mapper.toItemForOwnerDto(item, comments, lastBooking, nextBooking));
        }
        return itemList;
    }

    @Transactional
    @Override
    public ItemDto update(long itemId, int userId, UpdateItemDto itemUpdate) {
        final UserDto owner = userService.getUserById(userId);
        final Item updateItem = repository.findByIdAndOwner_Id(itemId, owner.getId())
                .orElseThrow(() -> new NotFoundException("User with id: " + itemId + " does not exist"));

        if (itemUpdate == null) {
            return mapper.toItemDto(updateItem);
        }
        if (isNotEmpty(itemUpdate.getName())) {
            updateItem.setName(itemUpdate.getName());
        }

        if (isNotEmpty(itemUpdate.getDescription())) {
            updateItem.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            updateItem.setAvailable(itemUpdate.getAvailable());
        }
        return mapper.toItemDto(repository.save(updateItem));
    }
}
