package ru.bsu.cosmos.model;

import java.time.LocalDate;

/**
 * Космический аппарат — рукотворное устройство, запущенное в космос.
 * Дополнительные поля: дата запуска, агентство, тип миссии, активность.
 */
public class Spacecraft extends SpaceObject {

    private LocalDate launchDate;
    private String agency;
    private String missionType; // орбитальный, межпланетный, посадочный...
    private boolean active;

    public Spacecraft() { super(); }

    public Spacecraft(String name, LocalDate discoveryDate, double distanceLy,
                      double massKg, String description,
                      LocalDate launchDate, String agency, String missionType, boolean active) {
        super(name, discoveryDate, distanceLy, massKg, description);
        this.launchDate = launchDate;
        this.agency = agency;
        this.missionType = missionType;
        this.active = active;
    }

    @Override
    public ObjectType getType() { return ObjectType.SPACECRAFT; }

    @Override
    public String getShortInfo() {
        return String.format(
            "Космический аппарат «%s»%nЗапуск: %s%nАгентство: %s%nТип миссии: %s%nСтатус: %s",
            getName(), launchDate, agency, missionType, active ? "активен" : "завершён");
    }

    public LocalDate getLaunchDate() { return launchDate; }
    public void setLaunchDate(LocalDate launchDate) { this.launchDate = launchDate; }

    public String getAgency() { return agency; }
    public void setAgency(String agency) { this.agency = agency; }

    public String getMissionType() { return missionType; }
    public void setMissionType(String missionType) { this.missionType = missionType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
