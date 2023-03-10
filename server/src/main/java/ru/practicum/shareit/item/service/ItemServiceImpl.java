package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemForOwnerDto getById(long itemId, long ownerId) {
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
    public ItemDto add(CreateItemDto item) {
        User owner = userRepository.findById(item.getOwnerId())
                .orElseThrow(() -> new NotFoundException("User with id: " + item.getOwnerId() + " does not exist"));

        ItemRequest itemRequest = null;
        if (item.getRequestId() != null) {
            itemRequest = requestRepository
                    .findById(item.getRequestId())
                    .orElseThrow(() -> new ValidationException("???????????? ???? ????????????."));
        }

        Item newItem = itemMapper.toItem(item, owner, itemRequest);

        Item itemSaved = repository.save(newItem);
        return itemMapper.toItemDto(itemSaved);
    }

    @Override
    public List<ItemDto> search(String text, Long userId) {
        return itemMapper.mapItemDto(repository.search(text.trim().toUpperCase()));
    }

    @Transactional
    @Override
    public CommentDto addComment(long bookerId, CommentCreate comment, long itemId) {
        log.info("LocalDateTime.now() {}", LocalDateTime.now());
        Booking booking = bookingRepository
                .findBookingByBookerAndItem(bookerId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("???? ???? ???????????? ???????????????? ??????????????????????, ?? ?????? ???? ???????? ????????????????????????"));
        Comment commentCreate = commentMapper.toComment(booking.getBooker(), booking.getItem(), comment);
        return commentMapper.toCommentDto(commentRepository.save(commentCreate));
    }

    @Override
    public List<ItemForOwnerDto> getAll(long userId) {
        List<Item> itemByOwner = repository.findAllByOwnerIdOrderById(userId);
        if (itemByOwner.isEmpty()) {
            return List.of();
        }

        Map<Long, Booking> itemsLast = bookingRepository.findListLast(itemByOwner, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));

        Map<Long, Booking> itemsNext = bookingRepository.findListNext(itemByOwner, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
        Map<Long, List<Comment>> itemsCommits = commentRepository

                .findByItemIn(itemByOwner, Sort.by(DESC, "created"))
                .stream().collect(groupingBy(comment -> comment.getItem().getId(), toList()));

        return mapItemForOwnerDto(itemByOwner, itemsLast, itemsNext, itemsCommits);
    }

    private List<ItemForOwnerDto> mapItemForOwnerDto(List<Item> itemByOwner,
                                                     Map<Long, Booking> itemsLast,
                                                     Map<Long, Booking> itemsNext,
                                                     Map<Long, List<Comment>> itemsCommits) {

        List<ItemForOwnerDto> items = itemMapper.mapItemForBookerDto(itemByOwner);

        return items.stream().peek(item -> {
            item.setComments(commentMapper.mapCommentDto(itemsCommits.getOrDefault(item.getId(), null)));
            item.setLastBooking(bookingMapper.toDto(itemsLast.getOrDefault(item.getId(), null)));
            item.setNextBooking(bookingMapper.toDto(itemsNext.getOrDefault(item.getId(), null)));
        }).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public ItemDto update(long itemId, long ownerId, UpdateItemDto itemUpdate) {
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
