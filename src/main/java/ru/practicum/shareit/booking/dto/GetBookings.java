package ru.practicum.shareit.booking.dto;

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
public class GetBookings {
    private Integer userId;

    PageRequest pageRequest;

    private String stateParam;

    public static GetBookings of(Integer userId,
                                 PageRequest pageRequest, String stateParam) {
        GetBookings request = new GetBookings();

        request.setUserId(userId);
        request.setStateParam(stateParam);
        request.setPageRequest(pageRequest);

        return request;
    }
}
