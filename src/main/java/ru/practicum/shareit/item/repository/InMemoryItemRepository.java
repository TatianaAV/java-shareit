package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    protected final Map<Long, Item> items = new HashMap<>();
    long lastId = 0;

    private long getId() {
        return ++lastId;
    }

    @Override
    public List<Item> findAll() {
        log.info("FIND ALL all items count: {}", items.keySet().size());
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByOwnerId(int ownerId) {
        log.info(" FIND ALL find all items by owner id: {} count: {}", ownerId, items.keySet().size());
        List<Item> allOwnerItems = items.values()
                .stream()
                .filter(item -> item.getOwner() == ownerId)
                .collect(Collectors.toList());
        log.info(" FIND ALL  items: {}", allOwnerItems);
        return allOwnerItems;
    }


    @Override
    public Optional<Item> findById(long itemId) {
        Item item = items.get(itemId);
        log.info("find items id: {}", itemId);
        return Optional.ofNullable(item);

    }

    @Override
    public Optional<Item> create(Item item) {

        long id = getId();
        item.setId(id);
        items.put(id, item);
        log.info("items create id : {}, count items {}", item.getId(), items.values().size());
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void delete(long itemId) {
        log.info("delete items count before: {}", items.keySet().size());
        items.remove(itemId);
        log.info("delete items count after: {}", items.keySet().size());
    }

    @Override
    public Optional<Item> update(long itemId, Item item) {
        Item updateItem = items.get(item.getId());
        if (item.getName() != null && !item.getName().isEmpty()) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        items.put(updateItem.getId(), updateItem);
        log.info("update items id : {}", updateItem.getId());
        return Optional.of(items.get(updateItem.getId()));
    }

    @Override
    public List<Item> search(String keyword) {
        List<Item> result = new ArrayList<>();
        if (!keyword.isEmpty()) {
            result.addAll(items.values().stream()
                    .filter(
                            item -> item.getAvailable() && (
                                    item.getName().toLowerCase().contains(keyword.toLowerCase())
                                            || item.getDescription().toLowerCase().contains(keyword.toLowerCase())
                            )
                    ).collect(Collectors.toList()));
        }
        log.info("search items {} , count: {}", keyword, result.size());
        return result;
    }

    @Override
    public boolean checkUserOwnsItem(int ownerId, long itemId) {
        List<Item> items1 = items.values()
                .stream()
                .filter(item -> item.getOwner() == ownerId).filter(item -> item.getId() == itemId)
                .collect(Collectors.toList());
        return items1.isEmpty();
    }
}
