package com.example.appdev_project

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appdev_project.database.Category
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.Question
import com.example.appdev_project.database.QuestionsDatabase
import com.example.appdev_project.databinding.FragmentCreatequizBinding
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
    private lateinit var binding: FragmentCreatequizBinding
    private lateinit var db:QuestionsDatabase


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreatequizBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        binding.createQuizButton.setOnClickListener {
            fetchAndSaveQuizData()
        }
    }
    private fun setupUI(){
        val spinnerAmountOptions = mutableListOf<Int>()
        for(i in 10..50){
            spinnerAmountOptions.add(i)
        }
        val adapterAmount = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerAmountOptions)
        adapterAmount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAmount.adapter = adapterAmount

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quiz_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quiz_difficulties,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDifficulty.adapter = adapter
        }
    }

    private fun fetchAndSaveQuizData() {
        val apiUrl = generateApiUrl(
            binding.spinnerAmount.selectedItem.toString(),
            binding.spinnerCategory.selectedItemPosition + 9,
            binding.spinnerDifficulty.selectedItem.toString()
        )
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO){
                    val db = DatabaseCompanionObject.buildDatabase(requireContext())
                    val response = getJsonResponse(apiUrl)
                    addQuestionsToDatabase(db, response, binding.categoryNameEditText.text.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }finally {
                if (::db.isInitialized && db.isOpen) {
                    db.close()
                }
            }
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

    private fun generateApiUrl(amount: String, categoryNumber: Int, difficulty: String): String {
        return "https://opentdb.com/api.php?amount=$amount&category=$categoryNumber&difficulty=$difficulty&type=multiple"
    }

}