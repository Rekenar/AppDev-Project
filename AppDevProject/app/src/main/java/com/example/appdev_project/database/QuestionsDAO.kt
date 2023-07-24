package com.example.appdev_project.database

import androidx.room.*


@Dao
interface QuestionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(question: Question)

    @Query("Select * from question where identifier = :identifier")
    fun getQuestions(identifier:Int):List<Question>

    @Query("Select difficulty from question where identifier = :identifier")
    fun getDifficulty(identifier:Int):String

    @Query("SELECT * FROM question")
    fun getAll(): List<Question>

    @Query("SELECT Max(identifier) FROM question")
    fun getMaxIdentifier(): Int

    @Query("DELETE FROM question")
    fun deleteAllRows()
}

