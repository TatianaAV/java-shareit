package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Queries;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.GetItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceTest implements Queries {
    @Autowired
    private ItemRequestService itemRequestService;

    private ItemRequestCreateDto itemRequestCreate;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {

        itemRequestCreate = new ItemRequestCreateDto("дрель по дереву", 1L, LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(1L, "дрель по дереву", LocalDateTime.now(), List.of());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenValidItemRequestCreateDto_whenCreatingItemRequest_thenItemRequestIsCreated() {
        var result = itemRequestService.add(new ItemRequestCreateDto("дрель по дереву", 1L, LocalDateTime.now()));
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("created")
                .isEqualTo(itemRequestDto);
    }

    @Test
    void givenNoUser_whenCreatingItemRequest_thenThrowException() {
          assertThatThrownBy(() -> itemRequestService.add(ItemRequestCreateDto.of(99L, itemRequestCreate)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void givenNoUser_whenFindingById_thenThrowException() {
        assertThatThrownBy(() -> itemRequestService.getById(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenNoRequest_whenFindingById_thenThrowException() {
        assertThatThrownBy(() -> itemRequestService.getById(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenSavedItemRequest_whenFindingById_thenRequestIsFound() {
        var createResult = itemRequestService.add(ItemRequestCreateDto.of(1L, itemRequestCreate));
        var findResult = itemRequestService.getById(1L, 1L);
        assertThat(findResult)
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(createResult);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenSavedItemRequest_whenFindingOwnRequests_thenRequestsAreFound() {
        var createResult = itemRequestService.add(new ItemRequestCreateDto("дрель по дереву", 1L, LocalDateTime.now()));
        var findResult = itemRequestService.getAll(1);
        assertThat(findResult).hasSize(1);
        assertThat(findResult.get(0))
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(createResult);
    }

    @Test
    void givenNoUser_whenFindingOwnRequests_thenThrowException() {
        assertThatThrownBy(() -> itemRequestService.getAll(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2})
    void givenSavedItemRequest_whenFindingAll_thenRequestsAreFound() {
        GetItemRequest request = GetItemRequest.of(1L, PageRequest.of(0, 10, Sort.by("created").descending()));

        var createResult = itemRequestService.add(ItemRequestCreateDto.of(2L, itemRequestCreate));
        var findResult = itemRequestService.searchRequests(request);
        assertThat(findResult).hasSize(1);
        assertThat(findResult.get(0))
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(createResult);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenOnlyOwnRequest_whenFindingAll_thenNoRequestsAreFound() {
        var findResult = itemRequestService.getAll(1);
        assertThat(findResult).hasSize(0);
    }

    @Test
    void givenNoUser_whenFindingAllRequests_thenThrowException() {
        assertThatThrownBy(() -> itemRequestService.getAll(1))
                .isInstanceOf(NotFoundException.class);
    }
}
