package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserShort;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUserById(Integer userId);


    UserDto createUser(CreatUserDto user);

    UserDto updateUser(UpdateUserDto user, int id);

    void deleteUserById(int userId);

    List<UserShort> findAllByEmailContainingIgnoreCase(String emailSearch);
}
