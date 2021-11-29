package org.example.model;

public class Plant
{
    private String common;
    private String botanical;
    private String zone;
    private String light;
    private String price;
    private int availability;

    public Plant(String common, String botanical, String zone, String light, String price, int availability) {
        this.common = common;
        this.botanical = botanical;
        this.zone = zone;
        this.light = light;
        this.price = price;
        this.availability = availability;
    }

    public String getCommon() {
        return common;
    }

    public String getBotanical() {
        return botanical;
    }

    public String getZone() {
        return zone;
    }

    public String getLight() {
        return light;
    }

    public String getPrice() {
        return price;
    }

    public int getAvailability() {
        return availability;
    }

}
