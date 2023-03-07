package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Valid
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatUserDto {

    @Email(message = "Email не соответствует формату")
    @NotBlank(message = "Email не может быть пустым")
    @Size(max = 50, message = "Email не может быть длиннее 50 символов")
    private String email;

    @Size(max = 50, message = "Имя не может быть длиннее 50 символов")
    @NotBlank(message = "Имя отсутствует")
    private String name;
}

