package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.AlreadyExistException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class ValidationService {
    private final UserRepository userRepository;

    public void userValidateExist(int id) {
        if (!userRepository.getMapUsers().contains(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public void userValidateExistEmail(User user) {
        if (userRepository.getUsersEmail().contains(user.getEmail())) {
            throw new AlreadyExistException("Email уже используется");
        }
    }

    public void validateCreate(int userId, Item item) {
        if (!InMemoryItemRepository.checkUserOwnsItem(userId, item.getId())) {
            throw new NotFoundException("Item not found");
        }
    }

    public void validateUpdate(int userId, Item item) {
        if (InMemoryItemRepository.checkUserOwnsItem(userId, item.getId())) {
            throw new NotFoundException("Item not found");
        }
    }
}
