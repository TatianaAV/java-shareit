package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ValidationService;

import java.util.List;

import static org.mapstruct.ap.internal.util.Strings.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ValidationService validationService;
    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers() {
        List<User> users = repository.findAll();
        return mapper.mapToUserDto(users);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Integer userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist"));
        return mapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto createUser(CreatUserDto createUser) {
        User user = mapper.toUser(createUser);
        return mapper.toUserDto(repository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(UpdateUserDto user, int id) {
        User foundUser = validationService.validateUser(id);
        if (user == null) {
            return mapper.toUserDto(foundUser);
        }
        if (isNotEmpty(user.getName())) {
            foundUser.setName(user.getName());
        }
        if (isNotEmpty(user.getEmail())) {
            foundUser.setEmail(user.getEmail());
        }
        return mapper.toUserDto(repository.save(foundUser));
    }

    @Transactional
    @Override
    public void deleteUserById(int userId) {
        repository.deleteById(userId);
    }
}
