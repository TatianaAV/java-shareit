package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    UserRepository userRepository;
    UserServiceImpl userService;
    UserMapper mapper;

    User user1;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mapper = Mappers.getMapper(UserMapper.class);
        userService = spy(new UserServiceImpl(userRepository, mapper));
        user1 = new User(1, " user 1", "user1@user.ru");

    }

    @Test
    void getUsers() {
        User user = new User(1, " user 1", "user@user.ru");
        final List<User> users = List.of(user);

        when(userRepository.findAll())
                .thenReturn(users);

        final List<UserDto> userDtos = userService.getUsers();

        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
    }

    @Test
    void getUsersEmptyList() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        final List<UserDto> userDtos = userService.getUsers();

        assertEquals(0, userDtos.size());
    }

    @Test
    void getUserById() {
        int userId = 1;
        User expectedUser = new User(1, " user 1", "user@user.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userService.getUserById(userId);

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void getUserById_WhenUserNotFound() {
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void createUser() {
        CreatUserDto createUser = new CreatUserDto("user1@user.ru", " user 1");

        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto actualUser = userService.createUser(createUser);

        assertEquals(user1.getId(), actualUser.getId());
        assertEquals(user1.getName(), actualUser.getName());
        assertEquals(user1.getEmail(), actualUser.getEmail());

        verify(userRepository, times(1))
                .save(ArgumentMatchers.any(User.class));
    }

    @Test
    void updateUser() {

        User expectedUser = new User(user1.getId(), " Updated 1", "user@user.ru");

        UpdateUserDto updateUserDto = new UpdateUserDto(0, " Updated 1", "user@user.ru");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        UserDto updateUser = userService.updateUser(updateUserDto, user1.getId());

        assertEquals(expectedUser.getName(), updateUser.getName());
        assertEquals(expectedUser.getEmail(), updateUser.getEmail());

        verify(userService, times(1))
                .updateUser(any(UpdateUserDto.class), anyInt());
    }

    @Test
    void updateUserException() {

        UpdateUserDto updateUserDto = new UpdateUserDto(0, " Updated 1", "user@user.ru");

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(updateUserDto, user1.getId()));

        verify(userService, times(1))
                .updateUser(any(UpdateUserDto.class), anyInt());
    }

    @Test
    void updateUserName() {

        UpdateUserDto updateUserDto = new UpdateUserDto(0, " Updated 1", null);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        UserDto expectedUser = userService.updateUser(updateUserDto, user1.getId());

        assertEquals(user1.getId(), expectedUser.getId());
        assertEquals(user1.getName(), updateUserDto.getName());
        assertEquals(user1.getEmail(), expectedUser.getEmail());

        verify(userService, times(1))
                .updateUser(any(UpdateUserDto.class), anyInt());
    }

    @Test
    void updateUserNameEmptyEmailNull() {

        UpdateUserDto updateUserDto = new UpdateUserDto(0, " ", null);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        UserDto expectedUser = userService.updateUser(updateUserDto, user1.getId());

        assertEquals(user1.getId(), expectedUser.getId());
        assertEquals(user1.getName(), expectedUser.getName());
        assertEquals(user1.getEmail(), expectedUser.getEmail());

        verify(userService, times(1))
                .updateUser(any(UpdateUserDto.class), anyInt());
    }

    @Test
    void updateUserEmail() {

        UpdateUserDto updateUserDto = new UpdateUserDto(0, null, "updated@email.ru");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        UserDto expectedUser = userService.updateUser(updateUserDto, user1.getId());

        assertEquals(user1.getId(), expectedUser.getId());
        assertEquals(user1.getName(), expectedUser.getName());
        assertEquals(user1.getEmail(), updateUserDto.getEmail());

        verify(userService, times(1))
                .updateUser(any(UpdateUserDto.class), anyInt());
    }

    @Test
    void updateUserEmailEmpty() {

        UpdateUserDto updateUserDto = new UpdateUserDto(0, null, " ");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        UserDto expectedUser = userService.updateUser(updateUserDto, user1.getId());

        assertEquals(user1.getId(), expectedUser.getId());
        assertEquals(user1.getName(), expectedUser.getName());
        assertEquals(user1.getEmail(), expectedUser.getEmail());

        verify(userService, times(1))
                .updateUser(any(UpdateUserDto.class), anyInt());
    }

    @Test
    void deleteUserById() {

        userService.deleteUserById(user1.getId());

        verify(userService, times(1))
                .deleteUserById(anyInt());

        verify(userRepository, times(1))
                .deleteById(anyInt());
    }
}