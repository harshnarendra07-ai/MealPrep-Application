package com.example.mymealprep

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "meal_table")
data class Meal(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,


    var mealName: String,
    var category: String,
    var area: String,
    var instructions: String,
    var youtubeLink: String,

    var ingredient1: String,
    var ingredient2: String,
    var measure1: String,
    var measure2: String
)