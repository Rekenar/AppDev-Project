package com.example.appdev_project

import androidx.room.*

@Dao
interface QuestionsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(questions: List<Questions>)


    @Query("SELECT * FROM questions")
    fun getAll(): List<Questions>

}

