package com.example.appdev_project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Questions(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val question:String,
    val answer1:String,
    val answer2:String,
    val answer3:String,
    val answer4:String,
    val correctAnswer:Int,
    val alreadyUsed:Boolean
)