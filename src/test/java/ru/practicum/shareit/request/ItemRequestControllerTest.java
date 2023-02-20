package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController requestController;

    private ItemRequestDto itemRequest;
    private UserDto user1;
    private UserDto user2;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();


        user1 = new UserDto(
                1,
                "john.doe@mail.com",
                "John Doe");

        user2 = new UserDto(
                1,
                "john.doe@mail.com",
                "John Doe");

        itemRequest = new ItemRequestDto(
                1L,
                "Нужна мощная дрель по дереву просверлить дубовую столешницу",
                LocalDateTime.of(2022, 02, 17, 19, 55, 00),
                List.of(
                        new ItemRequestDto.Item(1L, "дрель по дереву", 2),
                        new ItemRequestDto.Item(2L, "дрель универсальная", 2)));
    }

    @Test
    void getAll() throws Exception {
        when(itemRequestService.getAll(user1.getId()))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequest.getCreated()), LocalDateTime.class))
                .andExpect(jsonPath("$[0].items", is(itemRequest.getItems())));
    }

    @Test
    void searchRequests() {
    }

    @Test
    void getById() {
    }

    @Test
    void create() {
    }
}