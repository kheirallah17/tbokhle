package com.example.tbokhle.model;

public class Recipe {
    public int id;
    public String name;
    public String ingredientsTag;
    public String time;
    public String difficulty;
    public String kcal;
    public String imageUrl;
    public Recipe(int id, String name, String ingredientsTag, String time,
                  String difficulty, String kcal, String imageUrl) {
        this.id = id;
        this.name = name;
        this.ingredientsTag = ingredientsTag;
        this.time = time;
        this.difficulty = difficulty;
        this.kcal = kcal;
        this.imageUrl = imageUrl;
    }
}




