package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.itemdto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private ItemRequestCreateDto newItemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {

        newItemRequest = new ItemRequestCreateDto("Нужна мощная дрель по дереву просверлить дубовую столешницу", null, null);

        itemRequestDto = new ItemRequestDto(
                1L,
                "Нужна мощная дрель по дереву просверлить дубовую столешницу",
                LocalDateTime.of(2022, 2, 17, 19, 55, 0),
                List.of(
                        new ItemDto(1L, "дрель по дереву", "Дрель без функции перфоратора", true, 1L),
                        new ItemDto(2L, "дрель универсальная", "Дрель без функции перфоратора", true, 1L)));
    }

    @Test
    void create() throws Exception {

        when(itemRequestService.add(any(ItemRequestCreateDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)

                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));


        verify(itemRequestService, times(1))
                .add(any(ItemRequestCreateDto.class));
    }

    @Test
    void getAll() throws Exception {
        List<ItemRequestDto> requestDtos = List.of(itemRequestDto);
        when(itemRequestService.getAll(anyInt()))
                .thenReturn(requestDtos);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items.size()", is(2)))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));

        verify(itemRequestService, times(1))
                .getAll(anyInt());
    }

    @Test
    void searchRequests() throws Exception {
        String from = null;
        String size = null;

        List<ItemRequestDto> requestDtos = List.of(itemRequestDto);
        when(itemRequestService.searchRequests(any(GetItemRequest.class)))
                .thenReturn(requestDtos);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", from)
                        .queryParam("size", size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items.size()", is(2)))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));


        verify(itemRequestService, times(1))
                .searchRequests(any(GetItemRequest.class));
    }

    @Test
    void searchRequestsExceptionFrom() throws Exception {
        String from = "-1";
        String size = "20";

        when(itemRequestService.searchRequests(any(GetItemRequest.class)))
                .thenThrow(new ValidationException(""));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", from)
                        .queryParam("size", size))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, times(0))
                .searchRequests(any(GetItemRequest.class));
    }

    @Test
    void searchRequestsExceptionSize() throws Exception {
        String from = "0";
        String size = "0";

        when(itemRequestService.searchRequests(any(GetItemRequest.class)))
                .thenThrow(new ValidationException(""));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("from", from)
                        .queryParam("size", size))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, times(0))
                .searchRequests(any(GetItemRequest.class));
    }


    @Test
    void getById() throws Exception {


        when(itemRequestService.getById(anyInt(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items.size()", is(2)))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }
}
