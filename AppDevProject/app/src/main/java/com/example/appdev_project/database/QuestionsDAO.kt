package com.example.appdev_project.database

import androidx.room.*


@Dao
interface QuestionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(question: Question)

    @Query("Select question from question where identifier = :identifier")
    fun getQuestions(identifier:Int):List<String>

    @Query("SELECT * FROM question")
    fun getAll(): List<Question>

    @Query("SELECT Max(identifier) FROM question")
    fun getMaxIdentifier(): Int

    @Query("DELETE FROM question")
    fun deleteAllRows()
}

