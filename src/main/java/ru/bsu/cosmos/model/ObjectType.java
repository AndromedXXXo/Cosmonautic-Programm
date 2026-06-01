package ru.bsu.cosmos.model;

/**
 * Перечисление типов космических объектов.
 * Используется для классификации и фильтрации объектов в каталоге.
 */
public enum ObjectType {
    STAR("Звезда"),
    PLANET("Планета"),
    SATELLITE("Спутник"),
    ASTEROID("Астероид"),
    COMET("Комета"),
    SPACECRAFT("Космический аппарат");

    private final String russianName;

    ObjectType(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }

    @Override
    public String toString() {
        return russianName;
    }
}
