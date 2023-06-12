package com.example.appdev_project

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Questions::class], version = 3)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
}