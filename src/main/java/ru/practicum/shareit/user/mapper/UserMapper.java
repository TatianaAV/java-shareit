package ru.practicum.shareit.user.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.user.dto.CreatUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<UserDto> mapToUserDto(List<User> users);

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(CreatUserDto user);


    @Mapping(target = "id", ignore = true)
    User toUser(@MappingTarget User updateUser, UpdateUserDto user);

    @Condition
    default boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
