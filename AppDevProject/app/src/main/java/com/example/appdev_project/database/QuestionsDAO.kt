package com.example.appdev_project.database

import androidx.room.*


@Dao
interface QuestionsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(questions: Questions)


    @Query("SELECT * FROM questions")
    fun getAll(): List<Questions>

    @Query("SELECT Max(identifier) FROM questions")
    fun getMaxIdentifier(): Int

    @Query("DELETE FROM questions")
    fun deleteAllRows()

}

