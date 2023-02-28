package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.dto.itemdto.CreateItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemDto;
import ru.practicum.shareit.item.dto.itemdto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.itemdto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    ItemServiceImpl itemService;

    CommentRepository commentRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    ItemRequestRepository itemRequestRepository;

    ItemMapper itemMapper;
    CommentMapper commentMapper;
    BookingMapper bookingMapper;
    BookingForUser bookingForUser;

    User user1;
    User user2;
    Item item1ByOwner1;
    ItemRequest itemRequest1;
    ItemDto itemDto1;
    ItemForOwnerDto itemForOwnerDto;
    Comment comment1ByItem1Booker2;
    Booking lastBooking1ByBooker2Item1;
    Booking nextBooking;
    Booking booking;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);


        itemMapper = new ItemMapperImpl(new UserMapperImpl());
        commentMapper = new CommentMapperImpl(new UserMapperImpl());
        bookingMapper = new BookingMapperImpl(new UserMapperImpl());


        itemService = spy(new ItemServiceImpl(userRepository, itemRepository, commentRepository, bookingRepository, itemRequestRepository, itemMapper, commentMapper, bookingMapper));

        user1 = new User(1, " user 1", "user1@user.ru");
        user2 = new User(2, " user 2", "user2@user.ru");
        item1ByOwner1 = new Item(1L, "дрель по дереву", "Дрель без функции перфоратора", true, user1, null);
        itemRequest1 = new ItemRequest(1L, "Запрос 1 дрели", LocalDateTime.now().minusDays(1), user2);
        itemDto1 = new ItemDto(1L, "дрель универсальная", "Дрель без функции перфоратора", true, 1L);

        bookingForUser = new BookingForUser(1L, LocalDateTime.now().plusDays(1), LocalDateTime.MAX,
                StatusBooking.WAITING,
                new BookingForUser.Booker(1, "John Dow"),
                new BookingForUser.Item(1L, "Дрель по дереву"));

        comment1ByItem1Booker2 = new Comment(1L, LocalDateTime.now().minusMinutes(5), "Комментарий для вещи 1 от пользователя 2", item1ByOwner1, user2);

        lastBooking1ByBooker2Item1 = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1), StatusBooking.APPROVED, user2, item1ByOwner1);
        nextBooking = new Booking();
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1), StatusBooking.APPROVED, user2, item1ByOwner1);
        itemForOwnerDto = new ItemForOwnerDto(1L, "Вещь 1", "Описание вещи 1", true, null, null, List.of());
    }

    @Test
    void getByIdByOwner() {

        long itemId = 1;
        int userId = 1;//owner

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1ByOwner1));

        when(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId)).thenReturn(List.of(comment1ByItem1Booker2));
        when(bookingRepository.findLast(itemId)).thenReturn(lastBooking1ByBooker2Item1);
        when(bookingRepository.findNext(itemId)).thenReturn(null);

        ItemForOwnerDto actualItem = itemService.getById(itemId, userId);

        assertEquals(item1ByOwner1.getId(), actualItem.getId());
        assertEquals(item1ByOwner1.getName(), actualItem.getName());
        assertEquals(item1ByOwner1.getDescription(), actualItem.getDescription());

        assertEquals(comment1ByItem1Booker2.getText(), actualItem.getComments().get(0).getText());
        assertEquals(lastBooking1ByBooker2Item1.getId(), actualItem.getLastBooking().getId());
        assertNull(actualItem.getNextBooking());
    }

    @Test
    void getByIdByBooker() {

        long itemId = 1;
        int userId = 2;//booker

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1ByOwner1));

        when(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId)).thenReturn(List.of(comment1ByItem1Booker2));

        ItemForOwnerDto actualItem = itemService.getById(itemId, userId);

        assertEquals(item1ByOwner1.getId(), actualItem.getId());
        assertEquals(item1ByOwner1.getName(), actualItem.getName());
        assertEquals(item1ByOwner1.getDescription(), actualItem.getDescription());

        assertEquals(comment1ByItem1Booker2.getText(), actualItem.getComments().get(0).getText());
        assertNull(actualItem.getLastBooking());
        assertNull(actualItem.getNextBooking());

        verify(itemService, times(1))
                .getById(anyLong(), anyInt());

        verify(itemRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getByIdNotFoundException() {

        long itemId = 1;
        int userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));

        verify(itemService, times(1))
                .getById(anyLong(), anyInt());

        verify(itemRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void add() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRepository.save(any(Item.class))).thenReturn(item1ByOwner1);

        ItemDto actualItem = itemService.add(new CreateItemDto("дрель по дереву", "Дрель без функции перфоратора", true, null, userId));

        assertEquals(item1ByOwner1.getId(), actualItem.getId());
        assertEquals(item1ByOwner1.getName(), actualItem.getName());
        assertEquals(item1ByOwner1.getDescription(), actualItem.getDescription());

        verify(itemService, times(1))
                .add(any(CreateItemDto.class));

        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void addWithRequest() {

        int userId = 1;
        long requestId = 1;
        Item itemWithRequest =
                new Item(1L, "дрель по дереву",
                        "Дрель без функции перфоратора",
                        true, user1, itemRequest1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.save(any(Item.class))).thenReturn(itemWithRequest);

        ItemDto actualItem = itemService.add(new CreateItemDto("дрель по дереву", "Дрель без функции перфоратора", true, requestId, userId));

        assertEquals(itemWithRequest.getId(), actualItem.getId());
        assertEquals(itemWithRequest.getName(), actualItem.getName());
        assertEquals(itemWithRequest.getDescription(), actualItem.getDescription());
        assertEquals(itemWithRequest.getRequest().getRequestId(), actualItem.getRequestId());

        verify(itemService, times(1))
                .add(any(CreateItemDto.class));

        verify(itemRepository, times(1))
                .save(any(Item.class));
    }

    @Test
    void addValidationExceptionRequest() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService.add(new CreateItemDto("дрель по дереву", "Дрель без функции перфоратора", true, 2L, userId)));

        verify(itemService, times(1))
                .add(any(CreateItemDto.class));

        verify(itemRepository, times(0))
                .save(any(Item.class));
    }

    @Test
    void addValidationExceptionUser() {

        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.add(new CreateItemDto("дрель по дереву", "Дрель без функции перфоратора", true, 2L, userId)));

        verify(itemService, times(1))
                .add(any(CreateItemDto.class));

        verify(itemRepository, times(0))
                .save(any(Item.class));
    }

    @Test
    void search() {

        when(itemRepository.search(anyString())).thenReturn(List.of(item1ByOwner1));

        List<ItemDto> actualItemList = itemService.search("ДреЛь", 2);

        assertEquals(item1ByOwner1.getId(), actualItemList.get(0).getId());
        assertEquals(item1ByOwner1.getName(), actualItemList.get(0).getName());
        assertEquals(item1ByOwner1.getDescription(), actualItemList.get(0).getDescription());

        verify(itemService, times(1))
                .search(anyString(), anyInt());

        verify(itemRepository, times(1))
                .search(anyString());
    }

    @Test
    void searchEmptyItemsList() {

        when(itemRepository.search(anyString())).thenReturn(List.of());

        List<ItemDto> actualItemList = itemService.search("ДреЛь", 2);

        assertEquals(0, actualItemList.size());

        verify(itemService, times(1))
                .search(anyString(), anyInt());

        verify(itemRepository, times(1))
                .search(anyString());
    }

    @Test
    void addComment() {

        when(bookingRepository
                .findBookingByBookerAndItem(anyInt(), anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1ByItem1Booker2);

        CommentDto actualComment = itemService.addComment(booking.getBooker().getId(), new CommentCreate("Комментарий для вещи 1 от пользователя 2"), booking.getItem().getId());

        assertEquals(comment1ByItem1Booker2.getId(), actualComment.getId());
        assertEquals(comment1ByItem1Booker2.getText(), actualComment.getText());
        assertEquals(comment1ByItem1Booker2.getCreated(), actualComment.getCreated());
        assertEquals(comment1ByItem1Booker2.getAuthor().getName(), actualComment.getAuthorName());

        verify(itemService, times(1))
                .addComment(anyInt(), any(CommentCreate.class), anyLong());

        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }

    @Test
    void addCommentEmptyTestMapping() {

        when(bookingRepository
                .findBookingByBookerAndItem(anyInt(), anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());

        CommentDto actualComment = itemService.addComment(booking.getBooker().getId(), new CommentCreate("Комментарий для вещи 1 от пользователя 2"), booking.getItem().getId());

        assertNull(actualComment.getText());
    }

    @Test
    void addCommentException() {

        long itemId = 1;
        int userId = 1;

        when(bookingRepository
                .findBookingByBookerAndItem(anyInt(), anyLong(), any(LocalDateTime.class))).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, new CommentCreate(), itemId));

        verify(itemService, times(1))
                .addComment(anyInt(), any(CommentCreate.class), anyLong());

        verify(commentRepository, times(0))
                .save(any(Comment.class));
    }

    @Test
    void getAll() {
        //список вещей пользователя
        when(itemRepository.findAllByOwnerIdOrderById(anyInt())).thenReturn(List.of(item1ByOwner1));
        //последнее бронирование
        when(bookingRepository.findListLast(anyList(), any(LocalDateTime.class))).thenReturn(List.of(lastBooking1ByBooker2Item1));
        //следующее бронирование
        when(bookingRepository.findListNext(anyList(), any(LocalDateTime.class))).thenReturn(List.of());
        when(commentRepository.findByItemIn(anyList(), any(Sort.class))).thenReturn(List.of(comment1ByItem1Booker2));

        List<ItemForOwnerDto> actualItemList = itemService.getAll(item1ByOwner1.getOwner().getId());

        assertEquals(item1ByOwner1.getId(), actualItemList.get(0).getId());
        assertEquals(item1ByOwner1.getName(), actualItemList.get(0).getName());
        assertEquals(item1ByOwner1.getDescription(), actualItemList.get(0).getDescription());

        assertEquals(comment1ByItem1Booker2.getText(), actualItemList.get(0).getComments().get(0).getText());
        assertEquals(lastBooking1ByBooker2Item1.getId(), actualItemList.get(0).getLastBooking().getId());
        assertNull(actualItemList.get(0).getNextBooking());
    }

    @Test
    void getAllEmptyComment() {
        //список вещей пользователя
        when(itemRepository.findAllByOwnerIdOrderById(anyInt())).thenReturn(List.of(item1ByOwner1));
        //последнее бронирование
        when(bookingRepository.findListLast(anyList(), any(LocalDateTime.class))).thenReturn(List.of());
        //следующее бронирование
        when(bookingRepository.findListNext(anyList(), any(LocalDateTime.class))).thenReturn(List.of());
        when(commentRepository.findByItemIn(anyList(), any(Sort.class))).thenReturn(List.of());

        List<ItemForOwnerDto> actualItemList = itemService.getAll(item1ByOwner1.getOwner().getId());

        assertEquals(item1ByOwner1.getId(), actualItemList.get(0).getId());
        assertEquals(item1ByOwner1.getName(), actualItemList.get(0).getName());
        assertEquals(item1ByOwner1.getDescription(), actualItemList.get(0).getDescription());

        assertEquals(1, actualItemList.size());
        assertNull(actualItemList.get(0).getLastBooking());
        assertNull(actualItemList.get(0).getNextBooking());
    }

    @Test
    void getAllEmptyList() {
        //список вещей пользователя
        when(itemRepository.findAllByOwnerIdOrderById(anyInt())).thenReturn(List.of());
        //последнее бронирование
        when(bookingRepository.findListLast(anyList(), any(LocalDateTime.class))).thenReturn(List.of());
        //следующее бронирование
        when(bookingRepository.findListNext(anyList(), any(LocalDateTime.class))).thenReturn(List.of());
        when(commentRepository.findByItemIn(anyList(), any(Sort.class))).thenReturn(List.of());

        List<ItemForOwnerDto> actualItemList = itemService.getAll(item1ByOwner1.getOwner().getId());

        assertEquals(0, actualItemList.size());
    }

    @Test
    void update() {

        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyInt())).thenReturn(Optional.of(item1ByOwner1));

        ItemDto actualItemList = itemService.update(item1ByOwner1.getId(), item1ByOwner1.getOwner().getId(), new UpdateItemDto("Обновленное название", "Обновленное описание", true));

        assertEquals(item1ByOwner1.getId(), actualItemList.getId());
        assertEquals(item1ByOwner1.getName(), "Обновленное название");
        assertEquals(item1ByOwner1.getDescription(), "Обновленное описание");

        verify(itemService, times(1))
                .update(anyLong(), anyInt(), any(UpdateItemDto.class));

        verify(itemRepository, times(1))
                .findByIdAndOwner_Id(anyLong(), anyInt());
    }

    @Test
    void updateOnlyName() {

        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyInt())).thenReturn(Optional.of(item1ByOwner1));

        ItemDto actualItemList = itemService.update(
                item1ByOwner1.getId(),
                item1ByOwner1.getOwner().getId(),
                new UpdateItemDto("Обновленное название", null, true));

        assertEquals(item1ByOwner1.getId(), actualItemList.getId());
        assertEquals(item1ByOwner1.getName(), "Обновленное название");
        assertEquals(item1ByOwner1.getDescription(), "Дрель без функции перфоратора");

        verify(itemService, times(1))
                .update(anyLong(), anyInt(), any(UpdateItemDto.class));

        verify(itemRepository, times(1))
                .findByIdAndOwner_Id(anyLong(), anyInt());
    }

    @Test
    void updateOnlyNameDescriptionEmpty() {

        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyInt())).thenReturn(Optional.of(item1ByOwner1));

        ItemDto actualItemList = itemService.update(
                item1ByOwner1.getId(),
                item1ByOwner1.getOwner().getId(),
                new UpdateItemDto("Обновленное название", "  ", true));

        assertEquals(item1ByOwner1.getId(), actualItemList.getId());
        assertEquals(item1ByOwner1.getName(), "Обновленное название");
        assertEquals(item1ByOwner1.getDescription(), "Дрель без функции перфоратора");

        verify(itemService, times(1))
                .update(anyLong(), anyInt(), any(UpdateItemDto.class));

        verify(itemRepository, times(1))
                .findByIdAndOwner_Id(anyLong(), anyInt());
    }

    @Test
    void updateOnlyAvailable() {

        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyInt())).thenReturn(Optional.of(item1ByOwner1));

        ItemDto actualItem = itemService.update(
                item1ByOwner1.getId(),
                item1ByOwner1.getOwner().getId(),
                new UpdateItemDto(null, null, false));

        assertEquals(item1ByOwner1.getId(), actualItem.getId());
        assertEquals(item1ByOwner1.getName(), actualItem.getName());
        assertEquals(item1ByOwner1.getDescription(), actualItem.getDescription());
        assertEquals(item1ByOwner1.getAvailable(), false);

        verify(itemService, times(1))
                .update(anyLong(), anyInt(), any(UpdateItemDto.class));

        verify(itemRepository, times(1))
                .findByIdAndOwner_Id(anyLong(), anyInt());
    }

    @Test
    void updateOnlyAvailableNull() {

        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyInt())).thenReturn(Optional.of(item1ByOwner1));

        ItemDto actualItem = itemService.update(
                item1ByOwner1.getId(),
                item1ByOwner1.getOwner().getId(),
                new UpdateItemDto(" ", " ", null));

        assertEquals(item1ByOwner1.getId(), actualItem.getId());
        assertEquals(item1ByOwner1.getName(), actualItem.getName());
        assertEquals(item1ByOwner1.getDescription(), actualItem.getDescription());
        assertEquals(item1ByOwner1.getAvailable(), actualItem.getAvailable());

        verify(itemService, times(1))
                .update(anyLong(), anyInt(), any(UpdateItemDto.class));

        verify(itemRepository, times(1))
                .findByIdAndOwner_Id(anyLong(), anyInt());
    }

    @Test
    void updateOnlyAvailableDescriptionEmpty() {

        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyInt())).thenReturn(Optional.of(item1ByOwner1));

        ItemDto actualItem = itemService.update(
                item1ByOwner1.getId(),
                item1ByOwner1.getOwner().getId(),
                new UpdateItemDto(" ", " ", false));

        assertEquals(item1ByOwner1.getId(), actualItem.getId());
        assertEquals(item1ByOwner1.getName(), actualItem.getName());
        assertEquals(item1ByOwner1.getDescription(), actualItem.getDescription());
        assertEquals(item1ByOwner1.getAvailable(), false);

        verify(itemService, times(1))
                .update(anyLong(), anyInt(), any(UpdateItemDto.class));

        verify(itemRepository, times(1))
                .findByIdAndOwner_Id(anyLong(), anyInt());
    }

    @Test
    void updateException() {

        when(itemRepository.findByIdAndOwner_Id(anyLong(), anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(
                item1ByOwner1.getId(),
                item1ByOwner1.getOwner().getId(),
                new UpdateItemDto()));

        verify(itemService, times(1))
                .update(anyLong(), anyInt(), any(UpdateItemDto.class));

        verify(itemRepository, times(1))
                .findByIdAndOwner_Id(anyLong(), anyInt());
    }
}