package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserShort;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("userController getUsers");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.info("userController getUserById");
        return userService.getUserById(id);
    }

    @GetMapping("/search")
    public List<UserShort> searchEmail(@RequestParam(required = false) String text) {
        log.info("userController searchEmail");
        return userService.findAllByEmailContainingIgnoreCase(text);
    }

    @PostMapping
    public UserDto createUser(@Validated @RequestBody CreatUserDto user) {
        log.info("userController createUser");
        System.out.println(user);
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UpdateUserDto user, @PathVariable int id) {
        log.info("userController updateUser");
        return userService.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        log.info("userController deleteUser");
        userService.deleteUserById(id);
    }
}
