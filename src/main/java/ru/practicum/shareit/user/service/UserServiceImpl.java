package ru.practicum.shareit.user.service;

import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.AlreadyExistException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.validation.ValidationService;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ValidationService validationService;
    private final UserRepository userStorage;
    private final UserMapper mapper;


    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " does not exist"));
    }

    @Override
    public User createUser(CreatUserDto createUser) {
        User user = mapper.toUser(createUser);
        validationService.userValidateExistEmail(user);
        return userStorage.createUser(user)
                .orElseThrow(() -> new InternalException("User with id: " + user.getId() + " does not exist"));
    }

    @Override
    public User updateUser(UpdateUserDto user, int id) {
        User updateUser = mapper.toUser(user);
        validationService.userValidateExist(id);
        validationService.userValidateExistEmail(updateUser);
        if (updateUser.getEmail() == null && updateUser.getName() == null) {
            throw new AlreadyExistException("Нечего изменять");
        }
        return userStorage.updateUser(updateUser, id)
                .orElseThrow(() -> new NotFoundException("User with id: " + updateUser.getId() + " does not exist"));
    }

    @Override
    public void deleteUserById(int userId) {
        userStorage.deleteById(userId);
    }
}
