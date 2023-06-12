package com.example.appdev_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    private lateinit var questions: List<Questions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(
            applicationContext,
            QuestionsDatabase::class.java, "questions"
            ).createFromAsset("questions.json")
            .fallbackToDestructiveMigration()
            .build()


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                questions = db.questionsDao().getAll()
            }
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = questions[0].answer1

            
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}