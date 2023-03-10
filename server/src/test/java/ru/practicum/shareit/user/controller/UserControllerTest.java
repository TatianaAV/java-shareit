package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper mapper;
    private UserDto user1;
    private UserDto user2;
    private UserDto updatedUserDto;

    private UserDto updateUserDto;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {

        user1 = new UserDto(0L,
                "john.doe@mail.com",
                "John Doe");

        user2 = new UserDto(
                1,
                "John Doe",
                "john.doe@mail.com");

        updateUserDto = new UserDto(0L,
                "Update Doe",
                "update.doe@mail.com");

        updatedUserDto = new UserDto(1L,
                "Update Doe",
                "update.doe@mail.com");
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(user2);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user2.getName())))
                .andExpect(jsonPath("$.email", is(user2.getEmail())));

        verify(userService, times(1))
                .createUser(any(UserDto.class));
    }

    @Test
    void getUsers() throws Exception {

        when(userService.getUsers())
                .thenReturn(List.of());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1))
                .getUsers();
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user2);

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user2.getName())))
                .andExpect(jsonPath("$.email", is(user2.getEmail())));

        verify(userService, times(1))
                .getUserById(anyLong());
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenReturn(updatedUserDto);

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(updateUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updateUserDto.getEmail())));

        verify(userService, times(1))
                .updateUser(any(UserDto.class), anyLong());
    }

    @Test
    void deleteUserById() throws Exception {

        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());


        verify(userService, times(1))
                .deleteUserById(anyLong());
    }
}
