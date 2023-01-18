package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */

import lombok.Data;
import lombok.Value;

@Data
@Value
public class UpdateItemDto {
    String name;
    String description;
    Boolean available;
}