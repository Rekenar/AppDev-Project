package com.example.appdev_project.database


import android.content.Context
import android.text.Html
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

@Database(entities = [Questions::class], version = 8)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao

    companion object QuestionsDB {
            private var db: QuestionsDatabase? = null

            fun getDB(context: Context): QuestionsDatabase {
                if (db == null) {
                    db = buildDatabase(context)
                }
                return db!!
            }

            private fun buildDatabase(context: Context): QuestionsDatabase {
                return Room.databaseBuilder(
                    context.applicationContext,
                    QuestionsDatabase::class.java,
                    "my-database"
                ).fallbackToDestructiveMigration()
                    .build()
            }

        fun addQuiz(apiUrl: String){
            val response = getJsonResponse(apiUrl)
            addQuestionsToDatabase(response)
        }

        private fun getJsonResponse(apiUrl: String): String {
            val url = URL(apiUrl)
            val connection = url.openConnection()

            val input = BufferedReader(InputStreamReader(connection.getInputStream()))
            val response = StringBuilder()
            var inputLine: String?

            while (input.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            input.close()

            return response.toString()
        }

        private fun addQuestionsToDatabase(response:String){
            val jsonObject = JSONObject((response))
            val resultsArray = jsonObject.getJSONArray("results")
            for (i in 0 until resultsArray.length()) {
                val questionObject = resultsArray.getJSONObject(i)

                val question = questionObject.getString("question")
                val fixedQuestion = question.replace("\"", "\\\"")

                questionObject.put("question", fixedQuestion)
            }

            val identifier = db!!.questionsDao().getMaxIdentifier()
            println(response)
            for(i in 0 until resultsArray.length()){
                val resultObject = resultsArray.getJSONObject(i)
                val category = resultObject.getString("category")
                val question = resultObject.getString("question")
                val correctAnswer = resultObject.getString("correct_answer")
                val incorrectAnswersArray = resultObject.getJSONArray("incorrect_answers")


                db!!.questionsDao().insertQuestions(Questions(
                    identifier = identifier+1,
                    category= decodeHtmlEntities(category),
                    question = decodeHtmlEntities(question),
                    correctAnswer = decodeHtmlEntities(correctAnswer),
                    answer1 = decodeHtmlEntities(correctAnswer),
                    answer2 = decodeHtmlEntities(incorrectAnswersArray.getString(0)),
                    answer3 = decodeHtmlEntities(incorrectAnswersArray.getString(1)),
                    answer4 = decodeHtmlEntities(incorrectAnswersArray.getString(2))))
            }
        }

        private fun decodeHtmlEntities(input: String): String {
            return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY).toString()
        }
        fun generateApiUrl(amount: String, categoryNumber: Int, difficulty: String, ):String{
            return "https://opentdb.com/api.php?amount=$amount&category=$categoryNumber&difficulty=$difficulty&type=multiple"
        }
    }
}