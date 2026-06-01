package ru.bsu.cosmos.model;

import java.time.LocalDate;

/**
 * Астероид — малое тело Солнечной системы.
 */
public class Asteroid extends SpaceObject {

    private double diameterKm;
    private String asteroidGroup; // главный пояс, троянцы, NEA и т.д.

    public Asteroid() { super(); }

    public Asteroid(String name, LocalDate discoveryDate, double distanceLy,
                    double massKg, String description,
                    double diameterKm, String asteroidGroup) {
        super(name, discoveryDate, distanceLy, massKg, description);
        this.diameterKm = diameterKm;
        this.asteroidGroup = asteroidGroup;
    }

    @Override
    public ObjectType getType() { return ObjectType.ASTEROID; }

    @Override
    public String getShortInfo() {
        return String.format(
            "Астероид «%s»%nДиаметр: %.2f км%nГруппа: %s",
            getName(), diameterKm, asteroidGroup);
    }

    public double getDiameterKm() { return diameterKm; }
    public void setDiameterKm(double diameterKm) {
        if (diameterKm < 0) throw new IllegalArgumentException("Диаметр должен быть >= 0");
        this.diameterKm = diameterKm;
    }

    public String getAsteroidGroup() { return asteroidGroup; }
    public void setAsteroidGroup(String asteroidGroup) { this.asteroidGroup = asteroidGroup; }
}
