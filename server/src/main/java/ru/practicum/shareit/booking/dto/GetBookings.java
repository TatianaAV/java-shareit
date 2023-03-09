package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBookings {
    private Long userId;

    PageRequest pageRequest;

    private String stateParam;

    public static GetBookings of(Long userId,
                                 PageRequest pageRequest, String stateParam) {
        GetBookings request = new GetBookings();

        request.setUserId(userId);
        request.setStateParam(stateParam);
        request.setPageRequest(pageRequest);

        return request;
    }
}
