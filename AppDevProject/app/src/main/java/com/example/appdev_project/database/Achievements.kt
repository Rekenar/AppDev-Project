package com.example.appdev_project.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Achievements(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name:String,
    var finished:Boolean
)