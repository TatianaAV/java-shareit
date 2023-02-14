package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mapstruct.ap.internal.util.Strings.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    @Override
    public ItemForOwnerDto getById(long itemId, int ownerId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " does not exist"));
        List<CommentDto> comments = commentMapper.mapCommentDto(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
        if (item.getOwner().getId() == ownerId) {
            BookingDto lastBooking = bookingMapper.toDto(bookingRepository.findLast(itemId));
            BookingDto nextBooking = bookingMapper.toDto(bookingRepository.findNext(itemId));
            return itemMapper.toItemForOwnerDto(item, comments, lastBooking, nextBooking);
        } else {
            return itemMapper.toItemForBookerDto(item, comments);
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
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist"));
        Item newItem = itemMapper.createItemDtoToItem(owner, item);
        return itemMapper.toItemDto(repository.save(newItem));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text, Integer userId) {
        log.info("\n item search user id {}, search text {} \n ", userId, text);
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.mapItemDto(repository.search(text.trim().toUpperCase()));
    }

    @Transactional
    @Override
    public CommentDto addComment(int bookerId, CommentCreate comment, long itemId) {
        Booking booking = bookingRepository
                .findBookingByBookerAndItem(bookerId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Booking with itemId : " + itemId +
                        ", bookerId " + bookerId));
        if (booking.getBooker().getId() == bookerId) {
            Comment commentCreate = commentMapper.toComment(booking.getBooker(), booking.getItem(), comment, LocalDateTime.now());
            return commentMapper.toCommentDto(commentRepository.save(commentCreate));
        } else {
            throw new ValidationException("Вы не можете оставить комментарий, у вас не было бронирований");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemForOwnerDto> getAll(int userId) {
        log.info("item getAll user id {} ", userId);
        List<Item> items = repository.findAllByOwnerIdOrderById(userId);
        List<ItemForOwnerDto> itemList = new ArrayList<>();
        for (Item item : items) {
            long id = item.getId();
            List<CommentDto> comments = commentMapper.mapCommentDto(commentRepository.findAllByItemIdOrderByCreatedDesc(id));
            BookingDto lastBooking = bookingMapper.toDto(bookingRepository.findLast(id));
            BookingDto nextBooking = bookingMapper.toDto(bookingRepository.findNext(id));
            itemList.add(itemMapper.toItemForOwnerDto(item, comments, lastBooking, nextBooking));
        }
        return itemList;
    }

    @Transactional
    @Override
    public ItemDto update(long itemId, int ownerId, UpdateItemDto itemUpdate) {
        final Item updateItem = repository.findByIdAndOwner_Id(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + itemId + " does not exist"));
        if (itemUpdate == null) {
            return itemMapper.toItemDto(updateItem);
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
        return itemMapper.toItemDto(repository.save(updateItem));
    }
}
