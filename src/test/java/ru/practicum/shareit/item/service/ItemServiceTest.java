package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.commentdto.CommentCreate;
import ru.practicum.shareit.item.dto.itemdto.CreateItemDto;
import ru.practicum.shareit.item.dto.itemdto.UpdateItemDto;
import ru.practicum.shareit.Queries;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class ItemServiceTest implements Queries {
    @Autowired
    private ItemService itemService;
    private CreateItemDto item1;
    private CreateItemDto item2;
    private UpdateItemDto updateItem;

    @BeforeEach
    void setUp() {
        item1 = new CreateItemDto();
        item1.setName("Item1");
        item1.setDescription("Test item1");
        item1.setAvailable(Boolean.TRUE);

        item2 = new CreateItemDto();
        item2.setName("Item2");
        item2.setDescription("Test item2");
        item2.setAvailable(Boolean.TRUE);

        updateItem = new UpdateItemDto();
        updateItem.setAvailable(Boolean.FALSE);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenSavedItem_whenFindingItem_thenItemIsFound() {
        var createResult = itemService.add(CreateItemDto.of(1, item1));
        var findResult = itemService.getById(createResult.getId(), 1);
        assertThat(findResult).usingRecursiveComparison()
                .ignoringFields("comments", "nextBooking", "lastBooking")
                .isEqualTo(createResult);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2})
    void getByIdByNotOwner() {

        var createResult = itemService.add(CreateItemDto.of(1, item1));
        var findResult = itemService.getById(createResult.getId(), 2);
        assertThat(findResult).usingRecursiveComparison()
                .ignoringFields("comments", "nextBooking", "lastBooking")
                .isEqualTo(createResult);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void getByIdNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.getById(1, 1));
    }

    @Test
    void givenNoOwner_whenCreatingItem_thenThrowException() {
        assertThatThrownBy(() -> itemService.add(CreateItemDto.of(99, item1)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2})
    void addValidationExceptionRequest() {
        assertThrows(ValidationException.class, () -> itemService.add(new CreateItemDto("дрель по дереву", "Дрель без функции перфоратора", true, 2L, 1)));
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenTwoSavedItems_whenFindingAllItems_thenAllItemsAreFound() {
        itemService.add(CreateItemDto.of(1, item1));
        itemService.add(CreateItemDto.of(1, item2));
        var result = itemService.getAll(1);
        assertThat(result).hasSize(2);
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                .isEqualTo(item1);
        assertThat(result.get(1))
                .usingRecursiveComparison()
                .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                .isEqualTo(item2);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenValidItemDto_whenCreatingItem_thenItemIsCreated() {
        var result = itemService.add(CreateItemDto.of(1, item1));
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(item1);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenValidUpdateDto_whenUpdatingItem_thenItemIsUpdated() {
        var createResult = itemService.add(CreateItemDto.of(1, item1));
        var updateResult = itemService.update(1L, 1, updateItem);
        assertThat(updateResult)
                .usingRecursiveComparison()
                .ignoringFields("available")
                .isEqualTo(createResult);
        assertThat(updateResult.getAvailable()).isEqualTo(updateItem.getAvailable());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenValidUpdateDto_whenUpdatingItem_thenItemIsUpdatedName() {
        var createResult = itemService.add(CreateItemDto.of(1, item1));
        updateItem.setName("updated item");
        //  updateItem.setDescription("updated description");
        var updateResult = itemService.update(1L, 1, updateItem);
        assertThat(updateResult)
                .usingRecursiveComparison()
                .ignoringFields("available", "name")
                .isEqualTo(createResult);
        assertThat(updateResult.getAvailable()).isEqualTo(updateItem.getAvailable());
        assertThat(updateResult.getName()).isEqualTo(updateItem.getName());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenValidUpdateDto_whenUpdatingItem_thenItemIsUpdatedDescription() {
        var createResult = itemService.add(CreateItemDto.of(1, item1));
        updateItem.setDescription("updated description");
        var updateResult = itemService.update(1L, 1, updateItem);
        assertThat(updateResult)
                .usingRecursiveComparison()
                .ignoringFields("available", "description")
                .isEqualTo(createResult);
        assertThat(updateResult.getAvailable()).isEqualTo(updateItem.getAvailable());
        assertThat(updateResult.getDescription()).isEqualTo(updateItem.getDescription());
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenValidUpdateDtoButWrongOwner_whenUpdatingItem_thenThrowException() {
        itemService.add(CreateItemDto.of(1, item1));
        assertThatThrownBy(() -> itemService.update(1L, 11, updateItem))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenValidUpdateDtoButItemId_whenUpdatingItem_thenThrowException() {
        itemService.add(CreateItemDto.of(1, item1));
        assertThatThrownBy(() -> itemService.update(99L, 1, updateItem))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenSearchRequest_whenSearching_thenFindResultByNamen() {
        itemService.add(CreateItemDto.of(1, item1));
        itemService.add(CreateItemDto.of(1, item2));
        var result = itemService.search("iTeM", 1);
        assertThat(result).hasSize(2);
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                .isEqualTo(item1);
        assertThat(result.get(1))
                .usingRecursiveComparison()
                .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                .isEqualTo(item2);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER})
    void givenSearchRequest_whenSearching_thenFindResultByDescription() {
        itemService.add(CreateItemDto.of(1, item1));
        itemService.add(CreateItemDto.of(1, item2));
        var result = itemService.search("TeSt", 1);
        assertThat(result).hasSize(2);
        assertThat(result.get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                .isEqualTo(item1);
        assertThat(result.get(1))
                .usingRecursiveComparison()
                .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                .isEqualTo(item2);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM, ADD_BOOKING_ITEM1_USER2})
    void givenSavedItemWithPreviousBookingByUser2_whenCommentingItem_thenCommentIsCreated() {
        var result = itemService.addComment(2, new CommentCreate("Comment"), 1L);
        assertThat(result.getText()).isEqualTo("Comment");
        var commentResult = itemService.getById(1L, 1);
        assertThat(commentResult.getComments().contains(result));
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM, ADD_BOOKING_ITEM1_USER2})
    void givenSavedItemWithPreviousBookingByUser2_whenCommentingItemWithWrongUser_thenThrowException() {
        assertThatThrownBy(() -> itemService.addComment(99, new CommentCreate("Comment"), 1L)).isInstanceOf(ValidationException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_ITEM, ADD_BOOKING_ITEM1_USER2})
    void addCommentEmptyTestMapping() {
        assertThatThrownBy(() -> itemService.addComment(2, new CommentCreate(" "), 1L)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2, ADD_iTEM_REQUEST})
    void addWithRequest() {
        item1.setRequestId(1L);
        var createResult = itemService.add(CreateItemDto.of(1, item1));
        var findResult = itemService.getById(createResult.getId(), 2);
        assertThat(findResult).usingRecursiveComparison()
                .ignoringFields("comments", "nextBooking", "lastBooking")
                .isEqualTo(createResult);
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2})
    void searchEmptyItemsList() {
        var actualItemList = itemService.search("ДреЛь", 2);
        assertThat(actualItemList.size()).isZero();
    }

    @Test
    @Sql(statements = {RESET_IDS, ADD_USER, ADD_USER_2})
    void getAllEmptyItemsList() {
        var actualItemList = itemService.getAll(2);
        assertThat(actualItemList.size()).isZero();
    }
}
