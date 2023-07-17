package com.example.appdev_project.database

import androidx.room.*

@Dao
interface AchievementsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAchievements(achievements: Achievements)

    @Query("SELECT * FROM achievements")
    fun getAllAchievements():List<Achievements>

    @Update()
    fun updateAchievements(achievements:Achievements)
}