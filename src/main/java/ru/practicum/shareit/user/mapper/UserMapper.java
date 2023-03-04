package ru.practicum.shareit.user.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<UserDto> mapToUserDto(List<User> users);

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(CreatUserDto user);

    @Condition
    default boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    @Condition
    default boolean isNotEmpty(Integer value) {
        return value != null;
    }
}
