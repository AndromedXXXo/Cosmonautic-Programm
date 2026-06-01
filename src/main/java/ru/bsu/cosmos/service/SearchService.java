package ru.bsu.cosmos.service;

import ru.bsu.cosmos.model.ObjectType;
import ru.bsu.cosmos.model.SpaceObject;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Сервис поиска, фильтрации и сортировки космических объектов.
 * Использует функциональный подход с Predicate и Comparator.
 */
public class SearchService {

    private final SpaceObjectRepository repository;

    public SearchService(SpaceObjectRepository repository) {
        this.repository = repository;
    }

    /** Поиск по подстроке в названии (без учёта регистра). */
    public List<SpaceObject> searchByName(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        return repository.findAll().stream()
            .filter(o -> q.isEmpty() || o.getName().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }

    /** Универсальная фильтрация с заданным предикатом. */
    public List<SpaceObject> filter(Predicate<SpaceObject> predicate) {
        return repository.findAll().stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }

    /** Фильтрация по нескольким параметрам сразу. */
    public List<SpaceObject> filter(ObjectType type,
                                    String nameQuery,
                                    Double maxDistanceLy,
                                    Double minMassKg) {
        String q = nameQuery == null ? "" : nameQuery.trim().toLowerCase();
        return repository.findAll().stream()
            .filter(o -> type == null || o.getType() == type)
            .filter(o -> q.isEmpty() || o.getName().toLowerCase().contains(q))
            .filter(o -> maxDistanceLy == null || o.getDistanceLy() <= maxDistanceLy)
            .filter(o -> minMassKg == null || o.getMassKg() >= minMassKg)
            .collect(Collectors.toList());
    }

    /** Сортировка списка по заданному компаратору. */
    public List<SpaceObject> sort(List<SpaceObject> source, Comparator<SpaceObject> comparator) {
        return source.stream().sorted(comparator).collect(Collectors.toList());
    }

    /** Готовые компараторы для сортировки. */
    public static Comparator<SpaceObject> byName() {
        return Comparator.comparing(SpaceObject::getName, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<SpaceObject> byMass() {
        return Comparator.comparingDouble(SpaceObject::getMassKg);
    }

    public static Comparator<SpaceObject> byDistance() {
        return Comparator.comparingDouble(SpaceObject::getDistanceLy);
    }

    public static Comparator<SpaceObject> byDiscoveryDate() {
        return Comparator.comparing(SpaceObject::getDiscoveryDate,
            Comparator.nullsLast(Comparator.naturalOrder()));
    }
}
