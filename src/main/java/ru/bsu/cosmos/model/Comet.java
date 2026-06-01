package ru.bsu.cosmos.model;

import java.time.LocalDate;

/**
 * Комета — малое тело с протяжённым хвостом из газа и пыли.
 */
public class Comet extends SpaceObject {

    private double orbitalPeriodYears;
    private double tailLengthKm;

    public Comet() { super(); }

    public Comet(String name, LocalDate discoveryDate, double distanceLy,
                 double massKg, String description,
                 double orbitalPeriodYears, double tailLengthKm) {
        super(name, discoveryDate, distanceLy, massKg, description);
        this.orbitalPeriodYears = orbitalPeriodYears;
        this.tailLengthKm = tailLengthKm;
    }

    @Override
    public ObjectType getType() { return ObjectType.COMET; }

    @Override
    public String getShortInfo() {
        return String.format(
            "Комета «%s»%nПериод обращения: %.2f лет%nДлина хвоста: %.0f км",
            getName(), orbitalPeriodYears, tailLengthKm);
    }

    public double getOrbitalPeriodYears() { return orbitalPeriodYears; }
    public void setOrbitalPeriodYears(double orbitalPeriodYears) {
        if (orbitalPeriodYears < 0) throw new IllegalArgumentException("Период должен быть >= 0");
        this.orbitalPeriodYears = orbitalPeriodYears;
    }

    public double getTailLengthKm() { return tailLengthKm; }
    public void setTailLengthKm(double tailLengthKm) {
        if (tailLengthKm < 0) throw new IllegalArgumentException("Длина хвоста >= 0");
        this.tailLengthKm = tailLengthKm;
    }
}
