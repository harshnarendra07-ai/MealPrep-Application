package com.example.mymealprep

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MealDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)


    @Query("SELECT * FROM meal_table")
    suspend fun getAllMeals(): List<Meal>


    @Query("SELECT * FROM meal_table WHERE mealName LIKE '%' || :searchQuery || '%' OR ingredient1 LIKE '%' || :searchQuery || '%'")
    suspend fun searchMeals(searchQuery: String): List<Meal>
}