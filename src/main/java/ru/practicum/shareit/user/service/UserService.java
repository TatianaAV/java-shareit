package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getUserById(Integer userId);

    User createUser(CreatUserDto user);

    User updateUser(UpdateUserDto user, int id);

    void deleteUserById(int userId);

    void userValidateExist(int userId);
}
