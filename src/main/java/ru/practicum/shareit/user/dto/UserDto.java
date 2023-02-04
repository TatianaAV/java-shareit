package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private Integer id;

    @NotBlank(groups = {CreatUserDto.class})
    private String name;

    @Email(groups = {UpdateUserDto.class, CreatUserDto.class})
    @NotNull(groups = {CreatUserDto.class})
    private  String email;
}
