package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;


    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return mapper.mapToUserDto(users);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist"));
        return mapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto createUser(CreatUserDto createUser) {
        User user = mapper.toUser(createUser);
        return mapper.toUserDto(userRepository.save(user));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserDto updateUser(UpdateUserDto user, int userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist"));

        if (user.getName() != null && !user.getName().isBlank()) {
            foundUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            foundUser.setEmail(user.getEmail());
        }
        return mapper.toUserDto(foundUser);
    }

    @Transactional
    @Override
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }
}
