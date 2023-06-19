package com.example.appdev_project

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.appdev_project.database.Questions
import com.example.appdev_project.database.QuestionsDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class GameFragment : Fragment() {
    lateinit var questionText: TextView
    lateinit var buttons :Array<Button>
    private lateinit var questions: List<Questions>
    private var questionIndex:Int = 0
    lateinit var db:QuestionsDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        questionText = view.findViewById(R.id.txt_Question)
        buttons = arrayOf(view.findViewById(R.id.btn_Ans1),
                          view.findViewById(R.id.btn_Ans2),
                          view.findViewById(R.id.btn_Ans3),
                          view.findViewById(R.id.btn_Ans4))

        for (i in 0..3){
            buttons[i].setOnClickListener {
                nextQuestion(i+1)
            }
        }


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                questions = db.questionsDao().getAll()
            }
            updateQuestion()
        }
        return view
    }

    private fun nextQuestion(number: Int) {
        if (questionIndex < questions.size) {
            if (number == questions[questionIndex].correctAnswer) {
                Toast.makeText(requireContext(), "Correct Answer", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Incorrect Answer", Toast.LENGTH_SHORT).show()
            }

            questionIndex++
            updateQuestion()
        }
    }

    private fun updateQuestion(){
        if(questionIndex < questions.size && questions.isNotEmpty()){
            questionText.text = questions[questionIndex].question
            buttons[0].text = questions[questionIndex].answer1
            buttons[1].text = questions[questionIndex].answer2
            buttons[2].text = questions[questionIndex].answer3
            buttons[3].text = questions[questionIndex].answer4
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = QuestionsDatabase.getDB(context, lifecycleScope)
    }
}