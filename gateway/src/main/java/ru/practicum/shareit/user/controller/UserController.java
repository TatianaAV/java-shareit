package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;

import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public  ResponseEntity<Object> createUser(@Valid @RequestBody CreatUserDto user) {
        return userClient.createUser(user);
    }

    @GetMapping
    public  ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Object> getUserById(@PathVariable long id) {
        return userClient.getUserById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateUserDto user, @PathVariable long id) {
        return userClient.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        userClient.deleteUser(id);
    }
}
