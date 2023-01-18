package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Value
public class CreateItemDto {
    @NotBlank(message = "Name is required")
    String name;

    @NotBlank(message = "Description is required")
    String description;

    @NotNull(message = "Available is required")
    Boolean available;
}