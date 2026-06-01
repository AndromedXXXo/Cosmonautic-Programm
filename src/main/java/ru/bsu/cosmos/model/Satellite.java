package ru.bsu.cosmos.model;

import java.time.LocalDate;

/**
 * Естественный спутник — небесное тело, обращающееся вокруг планеты.
 */
public class Satellite extends SpaceObject {

    private String parentBodyName;     // имя родительского тела (планеты)
    private double orbitalRadiusKm;    // средний радиус орбиты в км
    private double orbitalPeriodDays;  // период обращения

    public Satellite() { super(); }

    public Satellite(String name, LocalDate discoveryDate, double distanceLy,
                     double massKg, String description,
                     String parentBodyName, double orbitalRadiusKm, double orbitalPeriodDays) {
        super(name, discoveryDate, distanceLy, massKg, description);
        this.parentBodyName = parentBodyName;
        this.orbitalRadiusKm = orbitalRadiusKm;
        this.orbitalPeriodDays = orbitalPeriodDays;
    }

    @Override
    public ObjectType getType() { return ObjectType.SATELLITE; }

    @Override
    public String getShortInfo() {
        return String.format(
            "Спутник «%s»%nРодительское тело: %s%nРадиус орбиты: %.0f км%nПериод обращения: %.2f сут.",
            getName(), parentBodyName, orbitalRadiusKm, orbitalPeriodDays);
    }

    public String getParentBodyName() { return parentBodyName; }
    public void setParentBodyName(String parentBodyName) { this.parentBodyName = parentBodyName; }

    public double getOrbitalRadiusKm() { return orbitalRadiusKm; }
    public void setOrbitalRadiusKm(double orbitalRadiusKm) {
        if (orbitalRadiusKm < 0) throw new IllegalArgumentException("Радиус должен быть >= 0");
        this.orbitalRadiusKm = orbitalRadiusKm;
    }

    public double getOrbitalPeriodDays() { return orbitalPeriodDays; }
    public void setOrbitalPeriodDays(double orbitalPeriodDays) {
        if (orbitalPeriodDays < 0) throw new IllegalArgumentException("Период должен быть >= 0");
        this.orbitalPeriodDays = orbitalPeriodDays;
    }
}
