package com.example.getpet.Model;

public class Animals {

    private String animal_name, description, image, category, time, date, animal_id;

    public Animals() {

    }

    public Animals(String animal_name, String description, String image, String category, String time, String date, String animal_id) {
        this.animal_name = animal_name;
        this.description = description;
        this.image = image;
        this.category = category;
        this.time = time;
        this.date = date;
        this.animal_id = animal_id;
    }

    public String getAnimal_name() {
        return animal_name;
    }

    public void setAnimal_name(String animal_name) {
        this.animal_name = animal_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAnimal_id() {
        return animal_id;
    }

    public void setAnimal_id(String animal_id) {
        this.animal_id = animal_id;
    }
}
