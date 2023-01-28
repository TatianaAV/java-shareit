package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    private int id;

    @Email(message = "Email не соответствует формату")
    private String email;
    private String name;

}
