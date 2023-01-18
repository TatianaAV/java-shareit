package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    private int id;

    @Email(message = "Email не соответствует формату")
    private String email;
    private String name;

}
