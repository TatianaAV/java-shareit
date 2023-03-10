package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private LocalDateTime created;

    private List<ItemDto> items = new ArrayList<>();

@Data
    public static class ItemDto {
        private long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
        private Long ownerId;
    }
}
