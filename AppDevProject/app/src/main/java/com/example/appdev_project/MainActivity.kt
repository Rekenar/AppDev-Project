package com.example.appdev_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.appdev_project.database.Achievements
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.QuestionsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var db: QuestionsDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            db = DatabaseCompanionObject.buildDatabase(context = applicationContext)

            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    if(db.achievementsDAO().getAllAchievements().isEmpty()){
                        // TODO: Add Achievements List when starting the app for the first time
                        val achievements = readQuizAchievements()
                        for(achievement in achievements){
                            db.achievementsDAO().insertAchievements(Achievements(name = achievement, finished = false))
                        }
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }





        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }

    private fun readQuizAchievements(): List<String> {
        val achievementsList = ArrayList<String>()

        val achievementsArray = resources.getStringArray(R.array.quiz_achievements)
        achievementsList.addAll(achievementsArray)

        return achievementsList
    }
}