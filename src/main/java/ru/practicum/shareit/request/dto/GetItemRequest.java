package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.exeption.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class GetItemRequest {
    private Integer userId;
    private Sort sort;


    private Integer from;

    private Integer size;

    public static GetItemRequest of(Integer userId,
                                    String sort,
                                    Integer from,
                                    Integer size) {
        GetItemRequest request = new GetItemRequest();

        request.setUserId(userId);
        request.setSort(Sort.valueOf(sort.toUpperCase()));
        if (from != null && from > -1) {
            request.setFrom(from);
        } else {
            throw new ValidationException("Ошибка пагинации");
        }
        if (size != null && size > 0) {
            request.setSize(size);
        } else {
            throw new ValidationException("Ошибка пагинации");
        }

        return request;
    }

    public enum Sort {DESC, ASC}
}
