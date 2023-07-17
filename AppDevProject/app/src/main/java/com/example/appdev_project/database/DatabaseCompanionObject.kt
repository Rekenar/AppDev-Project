package com.example.appdev_project.database

import android.content.Context
import android.text.Html
import android.util.Log
import androidx.room.Room
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DatabaseCompanionObject(){
    companion object DatabaseFunctions{


    fun buildDatabase(context: Context): QuestionsDatabase {
        return try {
            Room.databaseBuilder(
                context = context,
                QuestionsDatabase::class.java,
                "my-database"
            ).fallbackToDestructiveMigration()
                .build()
        } catch (e: Exception) {
            Log.e("Building the database has failed", e.stackTraceToString())
            throw e
        }
    }


}
}
