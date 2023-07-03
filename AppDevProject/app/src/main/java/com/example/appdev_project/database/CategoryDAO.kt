package com.example.appdev_project.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDAO {

    @Query("SELECT name FROM category WHERE identifier = :identifier")
    fun getNameOfCategory(identifier: Int):String

    @Query("SELECT name FROM category")
    fun getAllNamesOfCategories():List<String>

    @Query("SELECT * FROM category")
    fun getAll():List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)
}
