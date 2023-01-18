package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Valid
@Data
@AllArgsConstructor
public class ItemDto {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private Boolean available;

    private int owner;

    private long request;
}
