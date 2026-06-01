package ru.bsu.cosmos.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Абстрактный базовый класс для всех космических объектов.
 * Реализует общие свойства: уникальный идентификатор, название,
 * дату обнаружения, расстояние от Земли (св. лет), массу (кг) и описание.
 * <p>
 * Является корнем иерархии наследования и демонстрирует принципы
 * инкапсуляции (private-поля + геттеры/сеттеры с валидацией),
 * абстракции (абстрактные методы getType и getShortInfo)
 * и полиморфизма (toString, equals и потомки переопределяют поведение).
 */
public abstract class SpaceObject implements Identifiable, Comparable<SpaceObject> {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    private long id;
    private String name;
    private LocalDate discoveryDate;
    private double distanceLy;   // расстояние от Земли в световых годах
    private double massKg;       // масса в килограммах
    private String description;

    protected SpaceObject() {
        // конструктор по умолчанию (используется Gson)
        this.id = ID_GENERATOR.getAndIncrement();
    }

    protected SpaceObject(String name, LocalDate discoveryDate,
                          double distanceLy, double massKg, String description) {
        this();
        setName(name);
        setDiscoveryDate(discoveryDate);
        setDistanceLy(distanceLy);
        setMassKg(massKg);
        setDescription(description);
    }

    /** Возвращает тип объекта (реализуется потомками). */
    public abstract ObjectType getType();

    /** Краткая многострочная информация для отображения в карточке. */
    public abstract String getShortInfo();

    // ===== Геттеры и сеттеры =====

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        if (id >= ID_GENERATOR.get()) {
            ID_GENERATOR.set(id + 1);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.name = name.trim();
    }

    public LocalDate getDiscoveryDate() {
        return discoveryDate;
    }

    public void setDiscoveryDate(LocalDate discoveryDate) {
        this.discoveryDate = discoveryDate;
    }

    public double getDistanceLy() {
        return distanceLy;
    }

    public void setDistanceLy(double distanceLy) {
        if (distanceLy < 0) {
            throw new IllegalArgumentException("Расстояние не может быть отрицательным");
        }
        this.distanceLy = distanceLy;
    }

    public double getMassKg() {
        return massKg;
    }

    public void setMassKg(double massKg) {
        if (massKg < 0) {
            throw new IllegalArgumentException("Масса не может быть отрицательной");
        }
        this.massKg = massKg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    // ===== Стандартные методы =====

    @Override
    public int compareTo(SpaceObject other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpaceObject)) return false;
        SpaceObject that = (SpaceObject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s «%s» (id=%d)", getType().getRussianName(), name, id);
    }
}
