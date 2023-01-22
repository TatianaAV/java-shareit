package ru.practicum.shareit.item.dto;

/*
 * TODO Sprint add-controllers.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Null;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDto {
    private String name;
    private String description;
    private Boolean available;

    @Null
    private User owner;
}