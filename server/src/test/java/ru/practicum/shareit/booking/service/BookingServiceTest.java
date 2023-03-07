package ru.practicum.shareit.booking.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Queries;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookings;
import ru.practicum.shareit.booking.dto.StatusBooking;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.handler.ValidationException;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class BookingServiceTest implements Queries {
    @Autowired
    private BookingService bookingService;
    private BookingDto bookingCreateDto;


    @BeforeEach
    void setUp() {
        bookingCreateDto
                = new BookingDto(0L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), StatusBooking.WAITING, null,1L);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM, PAST_BOOKING})
    void givenBookerId_whenFindingBooking_resultIsFound() {
        var result = bookingService.getById(1L, 2);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getItem().getName()).isEqualTo("Item1");
        assertThat(result.getStatus()).isEqualTo(StatusBooking.APPROVED);
        assertThat(result.getStart().getMonth()).isEqualTo(Month.SEPTEMBER);
        assertThat(result.getEnd().getMonth()).isEqualTo(Month.OCTOBER);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenNoBooking_whenFindingBooking_ThrowException() {
        assertThatThrownBy(() -> bookingService.getById(1L, 2))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM, ADD_BOOKING_ITEM1_USER2})
    void givenNoBooking_whenFindingBooking_ThrowExceptionUser() {
        assertThatThrownBy(() -> bookingService.getById(1L, 3))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenValidItemCreateDto_whenCreatingItem_getEqualResultBack() {
        var result = bookingService.add(2, bookingCreateDto);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getItem().getName()).isEqualTo("Item1");
        assertThat(result.getStatus()).isEqualTo(StatusBooking.WAITING);
        assertThat(result.getStart().getHour()).isEqualTo(LocalDateTime.now().plusHours(1).getHour());
        assertThat(result.getEnd().getHour()).isEqualTo(LocalDateTime.now().plusHours(2).getHour());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenEmptyCreateDto_whenCreatingItem_getEqualResultBack() {
        assertThatThrownBy(() -> bookingService.add(2, new BookingDto()))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2})
    void givenNoItem_whenCreatingBooking_thenThrowException() {
        assertThatThrownBy(() -> bookingService.add(2, bookingCreateDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenNoBooker_whenCreatingBooking_thenThrowException() {
        assertThatThrownBy(() -> bookingService.add(99, bookingCreateDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM_NOT_AVAILABLE})
    void givenItemNotAvailable_whenCreatingBooking_thenThrowException() {
        assertThatThrownBy(() -> bookingService.add(2, bookingCreateDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenItem_whenCreatingBookingForOwnItem_thenThrowException() {
        assertThatThrownBy(() -> bookingService.add(1, bookingCreateDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenItem_whenCreatingBookingForOwnItem_thenThrowExceptionEndBeforeStart() {
        assertThatThrownBy(() -> bookingService.add(1, new BookingDto(0L, LocalDateTime.now().plusDays(1), LocalDateTime.now(),StatusBooking.WAITING, null,1L)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenItem_whenCreatingBookingForOwnItem_thenThrowExceptionEndEqualsStart() {
        assertThatThrownBy(() -> bookingService.add(2, new BookingDto(0L, LocalDateTime.of(2024, 3, 17, 19, 55, 0), LocalDateTime.of(2024, 3, 17, 19, 55, 0), StatusBooking.WAITING, null,1L)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenItem_whenCreatingBookingForOwnItem_thenThrowExceptionItemByOwner() {
        assertThatThrownBy(() -> bookingService.add(1, new BookingDto(0L, LocalDateTime.of(2024, 3, 17, 19, 55, 0), LocalDateTime.of(2024, 3, 18, 19, 55, 0), StatusBooking.WAITING, null, 1L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenSavedBooking_whenConfirmingBooking_thenBookingIsConfirmed() {
        bookingService.add(2, bookingCreateDto);
        var result = bookingService.updateStatus(1L, 1, Boolean.TRUE);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getItem().getName()).isEqualTo("Item1");
        assertThat(result.getStatus()).isEqualTo(StatusBooking.APPROVED);
        assertThat(result.getStart().getHour()).isEqualTo(LocalDateTime.now().plusHours(1).getHour());
        assertThat(result.getEnd().getHour()).isEqualTo(LocalDateTime.now().plusHours(2).getHour());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenSavedBooking_whenCanceledBookingBooker_thenBookingIsConfirmed() {
        bookingService.add(2, bookingCreateDto);
        var result = bookingService.updateStatus(1L, 2, Boolean.FALSE);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getItem().getName()).isEqualTo("Item1");
        assertThat(result.getStatus()).isEqualTo(StatusBooking.CANCELLED);
        assertThat(result.getStart().getHour()).isEqualTo(LocalDateTime.now().plusHours(1).getHour());
        assertThat(result.getEnd().getHour()).isEqualTo(LocalDateTime.now().plusHours(2).getHour());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenSavedBooking_whenConfirmingBooking_thenBookingIsRejected() {
        bookingService.add(2, bookingCreateDto);
        var result = bookingService.updateStatus(1L, 1, Boolean.FALSE);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getItem().getName()).isEqualTo("Item1");
        assertThat(result.getStatus()).isEqualTo(StatusBooking.REJECTED);
        assertThat(result.getStart().getHour()).isEqualTo(LocalDateTime.now().plusHours(1).getHour());
        assertThat(result.getEnd().getHour()).isEqualTo(LocalDateTime.now().plusHours(2).getHour());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenSavedBooking_whenConfirmingBooking_thenBookingIsCanceled() {
        bookingService.add(2, bookingCreateDto);
        bookingService.updateStatus(1L, 1, Boolean.TRUE);
        var result = bookingService.updateStatus(1L, 2, Boolean.FALSE);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
        assertThat(result.getItem().getName()).isEqualTo("Item1");
        assertThat(result.getStatus()).isEqualTo(StatusBooking.CANCELLED);
        assertThat(result.getStart().getHour()).isEqualTo(LocalDateTime.now().plusHours(1).getHour());
        assertThat(result.getEnd().getHour()).isEqualTo(LocalDateTime.now().plusHours(2).getHour());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenBooking_whenApprovingAlreadyApprovedBooking_thenThrowException() {
        bookingService.add(2, bookingCreateDto);
        bookingService.updateStatus(1L, 1, Boolean.TRUE);
        assertThatThrownBy(() -> bookingService.updateStatus(1L, 1, Boolean.TRUE))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenBooking_whenApprovingUserNotExistApprovedBooking_thenThrowException() {
        bookingService.add(2, bookingCreateDto);
        assertThatThrownBy(() -> bookingService.updateStatus(1L, 3, Boolean.TRUE))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM})
    void givenBooking_whenApprovingNotOwnBooking_thenThrowException() {
        bookingService.add(2, bookingCreateDto);
        assertThatThrownBy(() -> bookingService.updateStatus(1L, 2, Boolean.TRUE))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenBookerSearchersForPastBookings_thenFinds2() {
        GetBookings request = GetBookings.of(2L, PageRequest.of(0, 10, Sort.by("start").descending()), "PAST");
        var result = bookingService.getBookingsBooker(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenBookerSearchersForFutureBookings_thenFinds1() {
        GetBookings request = GetBookings.of(2L, PageRequest.of(0, 10, Sort.by("start").descending()), "FUTURE");

        var result = bookingService.getBookingsBooker(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenBookerSearchersForAllBookings_thenFinds4() {
        GetBookings request = GetBookings.of(2L, PageRequest.of(0, 10, Sort.by("start").descending()), "ALL");

        var result = bookingService.getBookingsBooker(request
        );
        assertThat(result).hasSize(4);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenBookerSearchersForCurrentBookings_thenFinds1() {
        GetBookings request = GetBookings.of(2L, PageRequest.of(0, 10, Sort.by("start").descending()), "CURRENT");

        var result = bookingService.getBookingsBooker(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenBookerSearchersForWaitingBookings_thenFinds1() {
        GetBookings request = GetBookings.of(2L, PageRequest.of(0, 10, Sort.by("start").descending()), "WAITING");
        var result = bookingService.getBookingsBooker(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenBookerSearchersForRejectedBookings_thenFinds1() {
        GetBookings request = GetBookings.of(2L, PageRequest.of(0, 10, Sort.by("start").descending()), "REJECTED");

        var result = bookingService.getBookingsBooker(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenNoBooker_thenThrowException() {
        GetBookings request = GetBookings.of(99L, PageRequest.of(0, 10, Sort.by("start").descending()), "REJECTED");

        assertThatThrownBy(() -> bookingService.getBookingsBooker(request)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenOwnerSearchersForCurrentBookings_thenFinds1() {
        GetBookings request = GetBookings.of(1L, PageRequest.of(0, 10, Sort.by("start").descending()), "CURRENT");

        var result = bookingService.getBookingsOwner(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenOwnerSearchersForPastBookings_thenFinds2() {
        GetBookings request = GetBookings.of(1L, PageRequest.of(0, 10, Sort.by("start").descending()), "PAST");
        var result = bookingService.getBookingsOwner(request);
        assertThat(result).hasSize(2);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenOwnerSearchersForFutureBookings_thenFinds1() {
        GetBookings request = GetBookings.of(1L, PageRequest.of(0, 10, Sort.by("start").descending()), "FUTURE");

        var result = bookingService.getBookingsOwner(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenOwnerSearchersForAllBookings_thenFinds4() {
        GetBookings request = GetBookings.of(1L, PageRequest.of(0, 10, Sort.by("start").descending()), "ALL");

        var result = bookingService.getBookingsOwner(request);
        assertThat(result).hasSize(4);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenOwnerSearchersForWaitingBookings_thenFinds1() {
        GetBookings request = GetBookings.of(1L, PageRequest.of(0, 10, Sort.by("start").descending()), "WAITING");

        var result = bookingService.getBookingsOwner(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenOwnerSearchersForRejectedBookings_thenFinds1() {
        GetBookings request = GetBookings.of(1L, PageRequest.of(0, 10, Sort.by("start").descending()), "REJECTED");
        var result = bookingService.getBookingsOwner(request);
        assertThat(result).hasSize(1);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenOwnerSearchersForApprovedBookings_thenFinds1() {
        GetBookings request = GetBookings.of(1L, PageRequest.of(0, 10, Sort.by("start").descending()), "APPROVED");

        var result = bookingService.getBookingsOwner(request);
        assertThat(result).hasSize(2);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenBookerSearchersForApprovedBookings_thenFinds1() {
        GetBookings request = GetBookings.of(2L, PageRequest.of(0, 10, Sort.by("start").descending()), "APPROVED");

        var result = bookingService.getBookingsBooker(request);
        assertThat(result).hasSize(2);
    }

    @Test
    @Sql(statements = {
            RESET_IDS,
            ADD_USER,
            ADD_USER_2,
            ADD_ITEM,
            PAST_BOOKING,
            CURRENT_BOOKING,
            REJECTED_BOOKING,
            WAITING_BOOKING
    })
    void given4Bookings_whenNotOwner_thenThrowException() {
        GetBookings request = GetBookings.of(99L, PageRequest.of(0, 10, Sort.by("created").descending()), "REJECTED");

        assertThatThrownBy(() -> bookingService.getBookingsOwner(request)).isInstanceOf(NotFoundException.class);
    }
}