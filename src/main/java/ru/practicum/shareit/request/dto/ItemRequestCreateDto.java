package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreateDto {

    @NotBlank(message = "Заполните описание зпапроса.")
    private String description;
    private Integer requestorId;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime created;

    public static ItemRequestCreateDto of(Integer userId, ItemRequestCreateDto item) {
        ItemRequestCreateDto request = new ItemRequestCreateDto();

        request.setDescription(item.getDescription());
        request.setRequestorId(userId);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}
