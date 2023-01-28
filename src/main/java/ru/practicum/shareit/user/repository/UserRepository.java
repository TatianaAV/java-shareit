package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<String> getUsersEmail();

    List<Integer> getMapUsers();

    List<User> getUsers();

    Optional<User> getUserById(int userId);

    Optional<User> updateUser(User updateUser, int id);

    void deleteById(int userId);

    Optional<User> createUser(User user);
}