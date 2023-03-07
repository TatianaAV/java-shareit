package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.handler.ValidationException;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemDto {

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private Long ownerId;

    public static CreateItemDto of(Long userId, CreateItemDto item) {
        if (item == null) {
            throw new ValidationException("Недостаточно данных для создания вещи");
        }
        CreateItemDto itemNew = new CreateItemDto();
        itemNew.setName(item.getName());
        itemNew.setDescription(item.getDescription());
        itemNew.setAvailable(item.getAvailable());
        itemNew.setOwnerId(userId);
        if (item.getRequestId() != null) {
            itemNew.setRequestId(item.getRequestId());
        }
        return itemNew;
    }
}