package ru.bsu.cosmos.service;

import ru.bsu.cosmos.model.ObjectType;
import ru.bsu.cosmos.model.SpaceObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Хранилище космических объектов в оперативной памяти.
 * Предоставляет CRUD-операции (создание, чтение, обновление, удаление)
 * и базовый доступ к коллекции.
 */
public class SpaceObjectRepository {

    private final Map<Long, SpaceObject> storage = new LinkedHashMap<>();

    /** Добавляет объект. Возвращает добавленный объект. */
    public SpaceObject add(SpaceObject object) {
        Objects.requireNonNull(object, "Объект не может быть null");
        storage.put(object.getId(), object);
        return object;
    }

    /** Обновляет объект (поиск по id). Возвращает true, если найден. */
    public boolean update(SpaceObject object) {
        if (!storage.containsKey(object.getId())) {
            return false;
        }
        storage.put(object.getId(), object);
        return true;
    }

    /** Удаляет объект по id. */
    public boolean deleteById(long id) {
        return storage.remove(id) != null;
    }

    public Optional<SpaceObject> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    /** Возвращает копию списка всех объектов. */
    public List<SpaceObject> findAll() {
        return new ArrayList<>(storage.values());
    }

    /** Возвращает все объекты заданного типа. */
    public List<SpaceObject> findByType(ObjectType type) {
        return storage.values().stream()
            .filter(o -> o.getType() == type)
            .collect(Collectors.toList());
    }

    public int size() {
        return storage.size();
    }

    public void clear() {
        storage.clear();
    }

    /** Загружает несколько объектов разом (например, из JSON). */
    public void addAll(Collection<? extends SpaceObject> objects) {
        for (SpaceObject o : objects) {
            add(o);
        }
    }
}
