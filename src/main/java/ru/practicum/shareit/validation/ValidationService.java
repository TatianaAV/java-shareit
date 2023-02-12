package ru.practicum.shareit.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.AlreadyExistException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ValidationService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public void userValidateExist(int id) {
        if (!userRepository.findAll().contains(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public void userValidateExistEmail(User user1) {
        List<User> userList = userRepository.findAll()
                .stream().filter(user -> user.getEmail().equals(user1.getEmail()))
                .collect(Collectors.toList());
        if (!userList.isEmpty()) {
            throw new AlreadyExistException("Email уже используется");
        }}

        public void validateCreateItem ( int userId, Item item){
            List<Item> items = itemRepository.findAllByOwnerId(userId);
            if (!items.isEmpty()) {
                throw new AlreadyExistException("Вещь уже добавлена");
            }
        }

        public void validateUpdateItem ( int userId, Item item){
            List<Item> items = itemRepository.findAllByOwnerId(userId);

            if (items.isEmpty()) {
                throw new NotFoundException("вещь не найдена");
            }
        }
    }

