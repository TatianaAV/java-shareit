package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private int lastId = 0;
    protected final Map<Integer, User> users = new HashMap<>();

    private int getId() {
        return ++lastId;
    }

    @Override
    public List<String> getUsersEmail() {
        return users.values().stream().map(User::getEmail).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getMapUsers() {
        return new ArrayList<>(users.keySet());
    }

    @Override
    public List<User> getUsers() {
        log.info("Users count: {} ", users.values().size());
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(int userId) {
        User user = users.get(userId);
        log.info("User with id: {} find", user.getId());
        return Optional.of(user);
    }

    @Override
    public Optional<User> createUser(User user) {
        if (users.containsValue(user)) {
            return Optional.empty();
        }
        int id = getId();
        user.setId(id);
        users.put(id, user);
        log.info("User with id: {} created", user.getId());
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> updateUser(User updateUser, int id) {
        User user = users.get(id);
        String name = updateUser.getName();
        String email = updateUser.getEmail();

        if (name != null && !name.equals(user.getName())) {
            user.setName(name);
        }
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
        }
        users.put(id, user);
        log.info("User with id: {} updated", user.getId());
        return Optional.of(user);
    }

    @Override
    public void deleteById(int userId) {
        log.info("Users count : {} ", users.values().size());
        users.remove(userId);
        log.info("User with id: {} deleted", userId);
        log.info("Users count after deleted: {} ", users.values().size());
    }
}
