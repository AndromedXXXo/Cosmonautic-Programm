package ru.bsu.cosmos.model;

import java.time.LocalDate;

/**
 * Планета — небесное тело, обращающееся вокруг звезды.
 * Дополнительные поля: период обращения (сутки), наличие атмосферы,
 * количество естественных спутников, имя родительской звезды.
 */
public class Planet extends SpaceObject {

    private double orbitalPeriodDays;
    private boolean hasAtmosphere;
    private int moonsCount;
    private String parentStarName;

    public Planet() { super(); }

    public Planet(String name, LocalDate discoveryDate, double distanceLy,
                  double massKg, String description,
                  double orbitalPeriodDays, boolean hasAtmosphere,
                  int moonsCount, String parentStarName) {
        super(name, discoveryDate, distanceLy, massKg, description);
        this.orbitalPeriodDays = orbitalPeriodDays;
        this.hasAtmosphere = hasAtmosphere;
        this.moonsCount = moonsCount;
        this.parentStarName = parentStarName;
    }

    @Override
    public ObjectType getType() { return ObjectType.PLANET; }

    @Override
    public String getShortInfo() {
        return String.format(
            "Планета «%s»%nРодительская звезда: %s%nПериод обращения: %.2f сут.%n" +
            "Атмосфера: %s%nКол-во спутников: %d",
            getName(), parentStarName, orbitalPeriodDays,
            hasAtmosphere ? "есть" : "нет", moonsCount);
    }

    public double getOrbitalPeriodDays() { return orbitalPeriodDays; }
    public void setOrbitalPeriodDays(double orbitalPeriodDays) {
        if (orbitalPeriodDays < 0) throw new IllegalArgumentException("Период должен быть >= 0");
        this.orbitalPeriodDays = orbitalPeriodDays;
    }

    public boolean isHasAtmosphere() { return hasAtmosphere; }
    public void setHasAtmosphere(boolean hasAtmosphere) { this.hasAtmosphere = hasAtmosphere; }

    public int getMoonsCount() { return moonsCount; }
    public void setMoonsCount(int moonsCount) {
        if (moonsCount < 0) throw new IllegalArgumentException("Кол-во спутников >= 0");
        this.moonsCount = moonsCount;
    }

    public String getParentStarName() { return parentStarName; }
    public void setParentStarName(String parentStarName) { this.parentStarName = parentStarName; }
}
