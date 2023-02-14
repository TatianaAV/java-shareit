package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;


@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    private int id;

    private String name;

    @Email(groups = {CreatUserDto.class})
    private  String email;
}
