package ru.bsu.cosmos.model;

import java.time.LocalDate;

/**
 * Звезда — самосветящееся небесное тело.
 * Дополнительные характеристики: спектральный класс,
 * светимость (в светимостях Солнца), температура поверхности (K).
 */
public class Star extends SpaceObject {

    private String spectralClass;
    private double luminositySolar;
    private double surfaceTemperatureK;

    public Star() { super(); }

    public Star(String name, LocalDate discoveryDate, double distanceLy,
                double massKg, String description,
                String spectralClass, double luminositySolar, double surfaceTemperatureK) {
        super(name, discoveryDate, distanceLy, massKg, description);
        this.spectralClass = spectralClass;
        this.luminositySolar = luminositySolar;
        this.surfaceTemperatureK = surfaceTemperatureK;
    }

    @Override
    public ObjectType getType() { return ObjectType.STAR; }

    @Override
    public String getShortInfo() {
        return String.format(
            "Звезда «%s»%nСпектральный класс: %s%nСветимость: %.2f L⊙%nТемпература: %.0f K",
            getName(), spectralClass, luminositySolar, surfaceTemperatureK);
    }

    public String getSpectralClass() { return spectralClass; }
    public void setSpectralClass(String spectralClass) { this.spectralClass = spectralClass; }

    public double getLuminositySolar() { return luminositySolar; }
    public void setLuminositySolar(double luminositySolar) {
        if (luminositySolar < 0) throw new IllegalArgumentException("Светимость не может быть отрицательной");
        this.luminositySolar = luminositySolar;
    }

    public double getSurfaceTemperatureK() { return surfaceTemperatureK; }
    public void setSurfaceTemperatureK(double surfaceTemperatureK) {
        if (surfaceTemperatureK < 0) throw new IllegalArgumentException("Температура должна быть >= 0 K");
        this.surfaceTemperatureK = surfaceTemperatureK;
    }
}
