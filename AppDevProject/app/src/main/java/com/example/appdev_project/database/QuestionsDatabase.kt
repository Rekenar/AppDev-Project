package com.example.appdev_project.database


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Question::class, Category::class], version = 9)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
    abstract fun categoryDao(): CategoryDAO
    abstract fun achievementsDAO(): AchievementsDAO

}