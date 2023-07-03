package com.example.appdev_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.appdev_project.database.DatabaseViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = ViewModelProvider(this).get(DatabaseViewModel::class.java)

        /**if(db.getDB().achievementsDAO().getAllAchievements().isEmpty()){
            // TODO: Add Achievements List when starting the app for the first time
        }
*/
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}