package ru.bsu.cosmos.service;

import ru.bsu.cosmos.model.ObjectType;
import ru.bsu.cosmos.model.SpaceObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис вычисления статистики по каталогу космических объектов.
 */
public class StatisticsService {

    private final SpaceObjectRepository repository;

    public StatisticsService(SpaceObjectRepository repository) {
        this.repository = repository;
    }

    /** Возвращает количество объектов каждого типа. */
    public Map<ObjectType, Long> countByType() {
        List<SpaceObject> all = repository.findAll();
        Map<ObjectType, Long> result = new EnumMap<>(ObjectType.class);
        for (ObjectType t : ObjectType.values()) result.put(t, 0L);
        all.forEach(o -> result.merge(o.getType(), 1L, Long::sum));
        return result;
    }

    public int totalCount() {
        return repository.size();
    }

    public double averageDistanceLy() {
        return repository.findAll().stream()
            .mapToDouble(SpaceObject::getDistanceLy)
            .average().orElse(0);
    }

    public double averageMassKg() {
        return repository.findAll().stream()
            .mapToDouble(SpaceObject::getMassKg)
            .average().orElse(0);
    }

    public Optional<SpaceObject> heaviest() {
        return repository.findAll().stream()
            .max(Comparator.comparingDouble(SpaceObject::getMassKg));
    }

    public Optional<SpaceObject> farthest() {
        return repository.findAll().stream()
            .max(Comparator.comparingDouble(SpaceObject::getDistanceLy));
    }

    public Optional<SpaceObject> nearest() {
        return repository.findAll().stream()
            .min(Comparator.comparingDouble(SpaceObject::getDistanceLy));
    }

    /** Формирует сводный текстовый отчёт. */
    public String buildSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== СВОДНАЯ СТАТИСТИКА КАТАЛОГА ===\n\n");
        sb.append(String.format("Всего объектов в каталоге: %d%n%n", totalCount()));
        sb.append("Распределение по типам:\n");
        countByType().forEach((type, count) ->
            sb.append(String.format("  • %-25s — %d%n", type.getRussianName(), count)));
        sb.append("\nСредние значения:\n");
        sb.append(String.format("  • Среднее расстояние от Земли: %.4f св. лет%n", averageDistanceLy()));
        sb.append(String.format("  • Средняя масса: %.3e кг%n", averageMassKg()));
        sb.append("\nРекорды каталога:\n");
        nearest().ifPresent(o -> sb.append(String.format(
            "  • Ближайший объект: %s (%.4f св. лет)%n", o.getName(), o.getDistanceLy())));
        farthest().ifPresent(o -> sb.append(String.format(
            "  • Самый удалённый: %s (%.4f св. лет)%n", o.getName(), o.getDistanceLy())));
        heaviest().ifPresent(o -> sb.append(String.format(
            "  • Самый массивный: %s (%.3e кг)%n", o.getName(), o.getMassKg())));
        return sb.toString();
    }
}
