package com.example.tbokhle;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Recipe {
    public String name;
    public String ingredientsTag;
    public String time;
    public String difficulty;
    public String kcal;
    public int imageResId;

    public Recipe(String name, String ingredientsTag, String time,
                  String difficulty, String kcal, int imageResId) {
        this.name = name;
        this.ingredientsTag = ingredientsTag;
        this.time = time;
        this.difficulty = difficulty;
        this.kcal = kcal;
        this.imageResId = imageResId;
    }
}



