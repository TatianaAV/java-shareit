package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByRequest_RequestId(@Param("requestId") Long requestId);

    List<Item> findAllByOwnerIdOrderById(Long userId);

    Optional<Item> findByIdAndOwner_Id(Long id, Long owner);

    @Query(value = " select i from Item i  where " +
            " i.available = true and " +
            "  (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or  upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);

    List<Item> findByRequestInOrderByIdDesc(List<ItemRequest> requests);
}

