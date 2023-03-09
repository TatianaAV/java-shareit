package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetItemRequest {
    private Long userId;

    PageRequest pageRequest;

    public static GetItemRequest of(Long userId,
                                    PageRequest pageRequest) {
        GetItemRequest request = new GetItemRequest();

        request.setUserId(userId);
        request.setPageRequest(pageRequest);
        return request;
    }
}
