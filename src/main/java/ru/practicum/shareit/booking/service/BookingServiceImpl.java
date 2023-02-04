package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService{

    private final UserService userService;
    private final UserMapper userRepositoryMapper;
        private final BookingRepository repository;
        private final BookingMapper mapper;


     /*
        public Item getById(long id, int userId) {
            return repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Item with id: " + id + " does not exist"));
        }


        public void delete(long id, int userId) {
            repository.deleteItemByIdAndOwnerId(id, userId);
        }


        public Booking add(Booking booking) {
            return repository.save(booking);
        }


        public List<Item> search(String text) {
            return repository.search(text);
        }


        public List<Item> getAll(int userId) {
            log.info("item getAll user id {} ", userId);
            if (userId > 0) {
                return repository.findAllByOwnerId(userId);
            }
            return repository.findAll();
        }


    public Item update(long itemId, int userId, UpdateItemDto itemDto) {
        return null;
    }*/

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById() {
        return null;
    }

    @Transactional
    @Override
    public void delete() {
    }

    @Transactional
    @Override
    public BookingDto add() {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> search() {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsOwner() {
        return null;
    }

    @Transactional
    @Override
    public BookingDto update(Booking booking) {
        return mapper.toDto(repository.save(booking));
    }

    @Override
    public List<BookingDto> getBookingsOwner(int userId, StatusBooking state) {
        List<Booking> result;
        User booker = userRepositoryMapper.userDtoToUser(userService.getUserById(userId));
        switch (state) {
            case ALL: result = repository.findAllByBookerOrderByStartDesc(booker);
                break;
            case CURRENT: result = repository.findAll();
                break;
            case FUTURE:
                break;
            case PAST:
                break;
            case REJECTED:
                break;
            case WAITING:
                break;
            case APPROVED:
                break;
        }
        return null;
    }
}
