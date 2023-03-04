package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class GetItemRequest {
    private Integer userId;

    PageRequest pageRequest;

    public static GetItemRequest of(Integer userId,
                                    PageRequest pageRequest) {
        GetItemRequest request = new GetItemRequest();

        request.setUserId(userId);
        request.setPageRequest(pageRequest);
        return request;
    }
}
