package com.example.appdev_project

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.appdev_project.Categories.CategoriesAdapter
import com.example.appdev_project.database.Category
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.Question
import com.example.appdev_project.database.QuestionsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CreateQuizFragment:Fragment(){
    private lateinit var spinnerAmount: Spinner
    lateinit var spinnerCategory: Spinner
    lateinit var spinnerDifficulty: Spinner
    lateinit var createButton: Button
    lateinit var editText: EditText
    private lateinit var db:QuestionsDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_createquiz, container, false)

        spinnerAmount = view.findViewById(R.id.spinnerAmount)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty)
        createButton = view.findViewById(R.id.createQuizButton)
        editText = view.findViewById(R.id.categoryNameEditText)


        val spinnerAmountOptions = mutableListOf<Int>()


        for(i in 10..50){
            spinnerAmountOptions.add(i)
        }


        val adapterAmount = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerAmountOptions)
        adapterAmount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAmount.adapter = adapterAmount


        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quiz_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quiz_difficulties,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDifficulty.adapter = adapter
        }

        try {
            db = DatabaseCompanionObject.buildDatabase(requireContext())

            createButton.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        addQuiz(db, generateApiUrl(
                            spinnerAmount.selectedItem.toString(),
                            spinnerCategory.selectedItemPosition+9,
                            spinnerDifficulty.selectedItem.toString()),editText.text.toString())
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }



        return view
    }
    fun addQuiz(db: QuestionsDatabase, apiUrl: String, name:String) {
        val response = getJsonResponse(apiUrl)
        addQuestionsToDatabase(db, response, name)
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

    private fun addQuestionsToDatabase(db:QuestionsDatabase, response: String, name: String) {
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
                val difficulty = resultObject.getString("difficulty")
                val correctAnswer = resultObject.getString("correct_answer")
                val incorrectAnswersArray = resultObject.getJSONArray("incorrect_answers")

                val decodedCategory = decodeHtmlEntities(category)
                val decodedQuestion = decodeHtmlEntities(question)
                val decodedDifficulty = decodeHtmlEntities(difficulty)
                val decodedCorrectAnswer = decodeHtmlEntities(correctAnswer)
                val decodedIncorrectAnswer1 = decodeHtmlEntities(incorrectAnswersArray.getString(0))
                val decodedIncorrectAnswer2 = decodeHtmlEntities(incorrectAnswersArray.getString(1))
                val decodedIncorrectAnswer3 = decodeHtmlEntities(incorrectAnswersArray.getString(2))

                db.questionsDao().insertQuestions(
                    Question(
                        identifier = identifier + 1,
                        category = decodedCategory,
                        question = decodedQuestion,
                        difficulty = decodedDifficulty,
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