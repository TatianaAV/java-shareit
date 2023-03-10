package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private long id;

    private String name;

    @Email(message = "Email не соответствует формату")
    private String email;
}
