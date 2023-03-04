package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUserById(Integer userId);

    UserDto createUser(CreatUserDto user);

    UserDto updateUser(UpdateUserDto user, int userId);

    void deleteUserById(int userId);
}
