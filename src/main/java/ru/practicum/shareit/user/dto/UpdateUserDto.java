package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.dto.GetItemRequest;

import javax.validation.constraints.Email;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    private int id;

    private String name;

    @Email(message = "Email не соответствует формату")
    private  String email;

    public static UpdateUserDto of(UpdateUserDto user, int id) {
         user.setId(id);
        return user;
    }
}
