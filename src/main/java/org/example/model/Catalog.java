package org.example.model;
import java.util.ArrayList;
import java.util.List;

public class Catalog {

    private String uuid;
    private String date;
    private String company;

    private List<Plant> plants = new ArrayList<>();

    public Catalog(String uuid, String date, String company) {
        this.uuid = uuid;
        this.date = date;
        this.company = company;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public void addPlant(Plant plant){
        /*this.*/plants.add(plant);
    }

    public String getUuid() {
        return uuid;
    }

    public String getDate() {
        return date;
    }

    public String getCompany() {
        return company;
    }

    @Override
    public String toString() {
        return "Catalog{" +
                "Plant=" + plants +
                '}';
    }


}
