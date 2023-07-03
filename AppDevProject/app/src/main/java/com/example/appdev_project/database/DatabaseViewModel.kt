package com.example.appdev_project.database

import android.app.Application
import android.text.Html
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    private val db: QuestionsDatabase by lazy {
        buildDatabase(application)
    }

    fun getDB(): QuestionsDatabase = db

    private fun buildDatabase(application: Application): QuestionsDatabase {
        return try {
            Room.databaseBuilder(
                application.applicationContext,
                QuestionsDatabase::class.java,
                "my-database"
            ).fallbackToDestructiveMigration()
                .addCallback(MyRoomDatabaseCallback())
                .build()
        } catch (e: Exception) {
            Log.e("Building the database has failed", e.stackTraceToString())
            throw e
        }
    }

    fun addQuiz(apiUrl: String, name:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = getJsonResponse(apiUrl)
            addQuestionsToDatabase(response, name)
        }
    }

    private fun getJsonResponse(apiUrl: String): String {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        return try {
            val input = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var inputLine: String?

            while (input.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }

            response.toString()
        } finally {
            connection.disconnect()
        }
    }

    private fun addQuestionsToDatabase(response: String, name: String) {
        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            val fixedResultsArray = JSONArray()
            for (i in 0 until resultsArray.length()) {
                val questionObject = resultsArray.getJSONObject(i)
                val question = questionObject.getString("question")
                val fixedQuestion = question.replace("\"", "\\\"")
                questionObject.put("question", fixedQuestion)
                fixedResultsArray.put(questionObject)
            }

            val identifier = db.questionsDao().getMaxIdentifier()

            if (db.categoryDao().getAllNamesOfCategories().contains(name)) {
                return
            }

            for (i in 0 until fixedResultsArray.length()) {
                val resultObject = fixedResultsArray.getJSONObject(i)
                val category = resultObject.getString("category")
                val question = resultObject.getString("question")
                val correctAnswer = resultObject.getString("correct_answer")
                val incorrectAnswersArray = resultObject.getJSONArray("incorrect_answers")

                val decodedCategory = decodeHtmlEntities(category)
                val decodedQuestion = decodeHtmlEntities(question)
                val decodedCorrectAnswer = decodeHtmlEntities(correctAnswer)
                val decodedIncorrectAnswer1 = decodeHtmlEntities(incorrectAnswersArray.getString(0))
                val decodedIncorrectAnswer2 = decodeHtmlEntities(incorrectAnswersArray.getString(1))
                val decodedIncorrectAnswer3 = decodeHtmlEntities(incorrectAnswersArray.getString(2))

                db.questionsDao().insertQuestions(
                    Question(
                        identifier = identifier + 1,
                        category = decodedCategory,
                        question = decodedQuestion,
                        correctAnswer = decodedCorrectAnswer,
                        answer1 = decodedCorrectAnswer,
                        answer2 = decodedIncorrectAnswer1,
                        answer3 = decodedIncorrectAnswer2,
                        answer4 = decodedIncorrectAnswer3
                    )
                )
                db.categoryDao().insertCategory(
                    Category(
                        identifier = identifier + 1,
                        name = name
                    )
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decodeHtmlEntities(input: String): String {
        return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    fun generateApiUrl(amount: String, categoryNumber: Int, difficulty: String): String {
        return "https://opentdb.com/api.php?amount=$amount&category=$categoryNumber&difficulty=$difficulty&type=multiple"
    }
}
