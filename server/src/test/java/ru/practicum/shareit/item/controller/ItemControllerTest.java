package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.commentdto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import ru.practicum.shareit.handler.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private CreateItemDto createItemDto;
    private CreateItemDto createItemDtoWithRequest;
    private ItemDto itemDto;
    private ItemDto itemDto2Request1;
    private ItemForOwnerDto itemForOwnerDto;
    private UpdateItemDto updateItemDto;
    private ItemDto updatedItemDto;


    @BeforeEach
    void setUp() {

        createItemDto = new CreateItemDto("?????????? ???? ????????????", "?????????? ?????? ?????????????? ??????????????????????", true, null, null);
        createItemDtoWithRequest = new CreateItemDto("Item with request ", "Description item ", true, 1L, null);


        itemDto = new ItemDto(1L, "?????????? ???? ????????????", "?????????? ?????? ?????????????? ??????????????????????", true, null);

        itemDto2Request1 = new ItemDto(2L, "?????????? ??????????????????????????", "?????????? ?????? ?????????????? ??????????????????????", true, 1L);

        itemForOwnerDto = new ItemForOwnerDto(1L, "?????????? ???? ????????????", "?????????? ?????? ?????????????? ??????????????????????", true, null, null, null);

        updateItemDto = new UpdateItemDto("?????????? ?????????? ???? ????????????", "?????????????? ??????????", true);
        updatedItemDto = new ItemDto(1L, "?????????? ?????????? ???? ????????????", "?????????? ?????? ?????????????? ??????????????????????", true, 1L);
    }

    @Test
    void create() throws Exception {

        when(itemService.add(any(CreateItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(createItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1))
                .add(any(CreateItemDto.class));
    }

    @Test
    void createRequest() throws Exception {

        when(itemService.add(any(CreateItemDto.class)))
                .thenReturn(itemDto2Request1);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(createItemDtoWithRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto2Request1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto2Request1.getName())))
                .andExpect(jsonPath("$.requestId", is(itemDto2Request1.getRequestId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto2Request1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto2Request1.getAvailable())));

        verify(itemService, times(1))
                .add(any(CreateItemDto.class));
    }

    @Test
    void createWithoutUserId() throws Exception {

        when(itemService.add(any(CreateItemDto.class)))
                .thenThrow(new ValidationException("Available is required"));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", IsNull.nullValue()))
                .andExpect(status().isBadRequest());

        verify(itemService, times(0))
                .add(any(CreateItemDto.class));
    }

    @Test
    void getAll() throws Exception {

        List<ItemForOwnerDto> items = List.of(itemForOwnerDto);
        when(itemService.getAll(anyLong()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemForOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemForOwnerDto.getDescription())))
                .andExpect(jsonPath("$[0].comments", is(IsNull.nullValue())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1))
                .getAll(anyLong());
    }

    @Test
    void search() throws Exception {

        when(itemService.search(anyString(), anyLong()))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("text", "??????????"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService, times(1))
                .search(anyString(), anyLong());
    }

    @Test
    void searchEmpty() throws Exception {
        String text = " ";
        when(itemService.search(anyString(), anyLong()))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("text", text))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService, times(0))
                .search(anyString(), anyLong());
    }

    @Test
    void getById() throws Exception {

        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemForOwnerDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemForOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemForOwnerDto.getDescription())))
                .andExpect(jsonPath("$.comments", is(IsNull.nullValue())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), any(CommentCreate.class), anyLong()))
                .thenReturn(new CommentDto(1L, "?????????? ??????????????????????", "John How",
                        LocalDateTime.of(2022, 2, 17, 19, 55, 0)));

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new CommentCreate("?????????? ??????????????????????")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.text", is("?????????? ??????????????????????")))
                .andExpect(jsonPath("$.authorName", is("John How")));

        verify(itemService, times(1))
                .addComment(anyLong(), any(CommentCreate.class), anyLong());
    }

    @Test
    void update() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(updatedItemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(UpdateItemDto.class));
    }

    @Test
    void updateNameDescriptionEmpty() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(new ItemDto(1L, "?????????? ?????????? ???? ????????????", "?????????? ?????? ?????????????? ??????????????????????", true, 1L));

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new UpdateItemDto(" ", null, true)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("?????????? ?????????? ???? ????????????")))
                .andExpect(jsonPath("$.description", is("?????????? ?????? ?????????????? ??????????????????????")))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class))
                .andExpect(jsonPath("$.available", is(true)));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(UpdateItemDto.class));
    }

    @Test
    void updateDescriptionEmpty() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(new ItemDto(1L, "?????????????????????? ????????????????", "?????????? ?????? ?????????????? ??????????????????????", true, 1L));

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new UpdateItemDto("?????????????????????? ????????????????", null, true)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("?????????????????????? ????????????????")))
                .andExpect(jsonPath("$.description", is("?????????? ?????? ?????????????? ??????????????????????")))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class))
                .andExpect(jsonPath("$.available", is(true)));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(UpdateItemDto.class));
    }

    @Test
    void updateNameEmpty() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(UpdateItemDto.class)))
                .thenReturn(new ItemDto(1L, "?????????? ?????????? ???? ????????????", "?????????????????????? ????????????????", true, 1L));

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(new UpdateItemDto(" ", "?????????????????????? ????????????????", true)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("?????????? ?????????? ???? ????????????")))
                .andExpect(jsonPath("$.description", is("?????????????????????? ????????????????")))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class))
                .andExpect(jsonPath("$.available", is(true)));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(UpdateItemDto.class));
    }
}