package ru.practicum.shareit.item.dto.itemdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.exeption.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Available is required")
    private Boolean available;

    private Long requestId;

    private Integer ownerId;

    public static CreateItemDto of(Integer userId, CreateItemDto item) {
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