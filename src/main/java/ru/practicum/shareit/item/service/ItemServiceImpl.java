package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

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

    @Override
    public List<ItemSearchDto> search(String text, Integer userId) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.mapItemForSearch(repository.search(text.trim().toUpperCase()));
    }

    @Transactional
    @Override
    public CommentDto addComment(int bookerId, CommentCreate comment, long itemId) {
        Booking booking = bookingRepository
                .findBookingByBookerAndItem(bookerId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Booking with itemId : " + itemId +
                        ", bookerId " + bookerId));
        if (booking.getBooker().getId() == bookerId) {
            Comment commentCreate = commentMapper.toComment(booking.getBooker(), booking.getItem(), comment);
            return commentMapper.toCommentDto(commentRepository.save(commentCreate));
        } else {
            throw new ValidationException("Вы не можете оставить комментарий, у вас не было бронирований");
        }
    }

    @Override
    public List<ItemForOwnerDto> getAll(int userId) {
        List<Item> itemByOwner = repository.findAllByOwnerIdOrderById(userId);

        Map<Long, Booking> itemsLast = bookingRepository.findListLast(itemByOwner, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));

        Map<Long, Booking> itemsNext = bookingRepository.findListNext(itemByOwner, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Long, List<Comment>> itemsCommits = commentRepository

                .findByItemIn(itemByOwner, Sort.by(DESC, "created"))
                .stream().collect(groupingBy(comment -> comment.getItem().getId(), toList()));

        List<ItemForOwnerDto> itemForOwnerDto = mapItemForOwnerDto(itemByOwner, itemsLast, itemsNext, itemsCommits);
        return itemForOwnerDto;
    }

    private List<ItemForOwnerDto> mapItemForOwnerDto(List<Item> itemByOwner,
                                                     Map<Long, Booking> itemsLast,
                                                     Map<Long, Booking> itemsNext,
                                                     Map<Long, List<Comment>> itemsCommits) {

        List<ItemForOwnerDto> items = itemMapper.mapItemForBookerDto(itemByOwner);

        return items.stream().peek(item -> {
            item.setComments(commentMapper.mapCommentDto(itemsCommits.get(item.getId())));
            item.setLastBooking(bookingMapper.toDto(itemsLast.get(item.getId())));
            item.setNextBooking(bookingMapper.toDto(itemsNext.get(item.getId())));
        }).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public ItemDto update(long itemId, int ownerId, UpdateItemDto itemUpdate) {
        final Item updateItem = repository.findByIdAndOwner_Id(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("User with id: " + itemId + " does not exist"));

        if (itemUpdate.getName() != null && !itemUpdate.getName().isBlank()) {
            updateItem.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null && !itemUpdate.getDescription().isBlank()) {
            updateItem.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            updateItem.setAvailable(itemUpdate.getAvailable());
        }
        return itemMapper.toItemDto(updateItem);
    }
}
