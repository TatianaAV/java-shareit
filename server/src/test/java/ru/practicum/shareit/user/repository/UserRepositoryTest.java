package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(0, "John Dawson", "john.doe@mail.com"));

        user2 = userRepository.save(new User(0, "John Doe", "john.doe@yandex.com"));
    }

    @Test
    void getById() {
        Optional<User> findUser = userRepository.findById(user1.getId());
        User user = findUser.get();

        assertNotNull(findUser);
        assertEquals(user1.getId(), user.getId());
        assertEquals(user1.getName(), user.getName());
        assertEquals(user1.getEmail(), user.getEmail());
    }

    @Test
    void getAll() {
        List<User> users = userRepository.findAll();

        assertNotNull(users);
        assertEquals(users.size(), 2);
        assertEquals(users.get(0).getId(), user1.getId());
        assertEquals(users.get(0).getName(), user1.getName());
        assertEquals(users.get(0).getEmail(), user1.getEmail());

        assertEquals(users.get(1).getId(), user2.getId());
        assertEquals(users.get(1).getName(), user2.getName());
        assertEquals(users.get(1).getEmail(), user2.getEmail());
    }

    @Test
    void updateUser() {
        Optional<User> findUser = userRepository.findById(user1.getId());
        User user = findUser.get();

        assertEquals(user1.getName(), user.getName());

        user.setName("Updated Name");

        Optional<User> updatedOptional = userRepository.findById(user1.getId());
        User updated = updatedOptional.get();

        assertEquals("Updated Name", updated.getName());
        assertEquals(user1.getEmail(), updated.getEmail());
    }

    @Test
    void createUser() {
        User user3 = userRepository.save(new User(0, "Ivanov Ivan", "iva@Ivanov.org"));

        assertNotNull(user3);
        assertEquals("Ivanov Ivan", user3.getName());
        assertEquals("iva@Ivanov.org", user3.getEmail());
    }

    @Test
    void deleteUserById() {
        userRepository.deleteById(user1.getId());

        List<User> users = userRepository.findAll();

        assertNotNull(users);
        assertEquals(users.size(), 1);

        assertEquals(users.get(0).getId(), user2.getId());
        assertEquals(users.get(0).getName(), user2.getName());
        assertEquals(users.get(0).getEmail(), user2.getEmail());
    }
}