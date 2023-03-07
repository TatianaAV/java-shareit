package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user, long userId);

    void deleteUserById(long userId);
}
