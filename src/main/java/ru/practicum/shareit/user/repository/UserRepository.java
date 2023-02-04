package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.UserShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<UserShort> findAllByEmailContainingIgnoreCase(String emailSearch);
}
