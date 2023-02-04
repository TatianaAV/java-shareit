package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select  item from Item item " +
            "where  item.available = true " +
            "and item.name like ?1 " +
            "and item.description like ?1 ")
    List<ItemDto> search(String text);

    void deleteItemByIdAndOwnerId(long id, int userId);

    List<ItemDto> findAllByOwnerId(int userId);
}

