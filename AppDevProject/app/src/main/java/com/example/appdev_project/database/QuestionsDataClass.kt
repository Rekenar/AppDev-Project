package com.example.appdev_project.database


data class QuestionsDataClass(
    val uid: Int,
    val identifier: Int,
    val category: String,
    val question:String,
    val answers:List<String>,
    val correctAnswer:String
)