package com.example.appdev_project.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Question(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val identifier: Int,
    val category: String,
    val question:String,
    val answer1:String,
    val answer2:String,
    val answer3:String,
    val answer4:String,
    val correctAnswer:String
)

