package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;
import ru.practicum.shareit.booking.dto.BookingForUser;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.dto.GetBookings;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.model.StatusBooking.WAITING;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private BookingForUser bookingForUser;
    private CreateBooking createBooking;

    @BeforeEach
    void setUp() {

        bookingForUser = new BookingForUser(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.MAX,
                WAITING,
                new BookingForUser.Booker(1, "John Dow"),
                new BookingForUser.Item(1L, "Дрель по дереву"));

        createBooking = new CreateBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.MAX, 1L);
    }

    @Test
    void create() throws Exception {

        when(bookingService.add(anyInt(), any(CreateBooking.class)))
                .thenReturn(bookingForUser);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(createBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingForUser.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.start", is(bookingForUser.getStart()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.end", is(bookingForUser.getEnd()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.item.id", is(bookingForUser.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingForUser.getBooker().getId()), Integer.class));


        verify(bookingService, times(1))
                .add(anyInt(), any(CreateBooking.class));
    }

    @Test
    void createExceptionStart() throws Exception {

        when(bookingService.add(anyInt(), any(CreateBooking.class)))
                .thenThrow(new ValidationException("Время начала не может быть раньше текущего времени"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new CreateBooking(
                                LocalDateTime.now(),
                                LocalDateTime.MAX, 1L
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, times(0))
                .add(anyInt(), any(CreateBooking.class));
    }

    @Test
    void createExceptionFinish() throws Exception {


        when(bookingService.add(anyInt(), any(CreateBooking.class)))
                .thenThrow(new ValidationException(""));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new CreateBooking(
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now(), 1L
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, times(0))
                .add(anyInt(), any(CreateBooking.class));
    }

    @Test
    void createExceptionFailItemId() throws Exception {


        when(bookingService.add(anyInt(), any(CreateBooking.class)))
                .thenThrow(new ValidationException(""));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new CreateBooking(
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(2), null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, times(0))
                .add(anyInt(), any(CreateBooking.class));
    }

    @Test
    void getByIdException() throws Exception {

        when(bookingService.getById(anyLong(), anyInt()))
                .thenThrow(new NotFoundException(""));

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1))
                .getById(1, 2);
    }

    @Test
    void getByIdExceptionXSharerUserId() throws Exception {

        when(bookingService.getById(anyLong(), anyInt()))
                .thenThrow(HttpServerErrorException.InternalServerError.class);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", IsNull.nullValue()))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0))
                .add(anyInt(), any(CreateBooking.class));
    }

    @Test
    void getById() throws Exception {

        when(bookingService.getById(anyLong(), anyInt()))
                .thenReturn(bookingForUser);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingForUser.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingForUser.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingForUser.getItem().getId()), Long.class));

        verify(bookingService, times(1))
                .getById(1, 2);
    }

    @Test
    void updateStatus() throws Exception {
        Long bookingId = 1L;
        Integer userId = 1;
        String approved = "true";
        bookingForUser.setStatus(StatusBooking.APPROVED);

        when(bookingService.updateStatus(anyLong(), anyInt(), anyBoolean()))
                .thenReturn(bookingForUser);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .queryParam("approved", approved)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingForUser.getId()), Long.class))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.item.id", is(bookingForUser.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingForUser.getBooker().getId()), Integer.class));


        verify(bookingService, times(1))
                .updateStatus(anyLong(), anyInt(), anyBoolean());

    }

    @Test
    void getBookings() throws Exception {
        String from = null;
        String size = null;
        String stateParam = null;
        when(bookingService.getBookingsBooker(any(GetBookings.class)))
                .thenReturn(List.of());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .queryParam("status", stateParam))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .getBookingsBooker(any(GetBookings.class));
    }

    @Test
    void getBookingsByOwnerStatus() throws Exception {
        String from = null;
        String size = null;
        String stateParam = null;
        when(bookingService.getBookingsOwner(any(GetBookings.class)))
                .thenReturn(List.of());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .queryParam("status", stateParam))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .getBookingsOwner(any(GetBookings.class));
    }

    @Test
    void getBookingsExceptionFrom() throws Exception {
        String from = "-1";
        String size = "20";
        String stateParam = "ALL";
        when(bookingService.getBookingsBooker(any(GetBookings.class)))
                .thenThrow(new ValidationException(""));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .queryParam("status", stateParam))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0))
                .getBookingsBooker(any(GetBookings.class));
    }

    @Test
    void getBookingsByOwnerExceptionSize() throws Exception {
        String from = "0";
        String size = "0";
        String stateParam = null;
        when(bookingService.getBookingsOwner(any(GetBookings.class)))
                .thenThrow(new ValidationException(""));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .queryParam("status", stateParam))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(0))
                .getBookingsOwner(any(GetBookings.class));
    }
}