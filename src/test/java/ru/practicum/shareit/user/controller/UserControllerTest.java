package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper mapper;
    private CreatUserDto user1;
    private UserDto user2;
    private UserDto updatedUserDto;

    private UpdateUserDto updateUserDto;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {

        user1 = new CreatUserDto(
                "john.doe@mail.com",
                "John Doe");

        user2 = new UserDto(
                1,
                "John Doe",
                "john.doe@mail.com");

        updateUserDto = new UpdateUserDto(0,
                "Update Doe",
                "update.doe@mail.com");

        updatedUserDto = new UserDto(1,
                "Update Doe",
                "update.doe@mail.com");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(CreatUserDto.class)))
                .thenReturn(user2);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user2.getName())))
                .andExpect(jsonPath("$.email", is(user2.getEmail())));
    }

    @Test
    void getUsers() throws Exception {
        when(userService.getUsers())
                .thenReturn(List.of(user2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user2.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(user2.getName())))
                .andExpect(jsonPath("$[0].email", is(user2.getEmail())));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyInt()))
                .thenReturn(user2);

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user2.getName())))
                .andExpect(jsonPath("$.email", is(user2.getEmail())));
    }


    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(any(UpdateUserDto.class), anyInt()))
                .thenReturn(updatedUserDto);

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(updateUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user2.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updateUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updateUserDto.getEmail())));
    }

    @Test
    void deleteUserById() {

    }

}