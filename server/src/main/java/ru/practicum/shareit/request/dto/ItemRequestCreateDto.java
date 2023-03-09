package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreateDto {

    private String description;
    private Long requestorId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime created;

    public static ItemRequestCreateDto of(Long userId, ItemRequestCreateDto item) {
        ItemRequestCreateDto request = new ItemRequestCreateDto();

        request.setDescription(item.getDescription());
        request.setRequestorId(userId);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}
