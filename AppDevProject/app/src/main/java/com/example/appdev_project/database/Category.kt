package com.example.appdev_project.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey val identifier: Int,
    val name:String
)