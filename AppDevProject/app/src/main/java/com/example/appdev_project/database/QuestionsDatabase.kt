package com.example.appdev_project.database


import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

@Database(entities = [Questions::class], version = 6)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao

    companion object QuestionsDB {
            private var db: QuestionsDatabase? = null
            fun getDB(context: Context, lifecycleScope: LifecycleCoroutineScope): QuestionsDatabase {
                if (db == null) {
                    db = buildDatabase(context, lifecycleScope)
                }
                return db!!
            }

            private fun buildDatabase(context: Context, lifecycleScope: CoroutineScope): QuestionsDatabase {
                return Room.databaseBuilder(
                    context.applicationContext,
                    QuestionsDatabase::class.java,
                    "my-database"
                ).fallbackToDestructiveMigration()
                    .build().also { database ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                if(database.questionsDao().getAll().isEmpty()){
                                    val questionsList = loadQuestionsFromAsset(context)
                                    database.questionsDao().insertQuestions(questionsList)
                                }
                            }
                        }
                    }
            }

            private fun jsonToList(inputStream: InputStream): List<Questions> {
                val gson = Gson()
                val reader = InputStreamReader(inputStream)
                val listType = object : TypeToken<List<Questions>>() {}.type
                return gson.fromJson(reader, listType)
            }

            private fun loadQuestionsFromAsset(context: Context): List<Questions> {
                var questionsList: List<Questions>
                val assetManager = context.assets
                try {
                    val inputStream = assetManager.open("questions.json")
                    questionsList = jsonToList(inputStream)
                    inputStream.close()
                } catch (e: IOException) {
                    questionsList = emptyList()
                }
                return questionsList
            }
        }

}