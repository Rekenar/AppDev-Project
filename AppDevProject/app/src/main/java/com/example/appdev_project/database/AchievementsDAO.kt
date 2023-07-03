package com.example.appdev_project.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AchievementsDAO {
    /**@Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(achievements: Achievements)

    @Query("SELECT * FROM achievements")
    fun getAllAchievements():List<Achievements>
    */
}