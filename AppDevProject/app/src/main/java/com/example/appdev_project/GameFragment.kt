package com.example.appdev_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GameFragment : Fragment() {
    lateinit var questionText: TextView
    lateinit var buttons :Array<Button>
    lateinit var pointsView : TextView
    var pointCounter : Int = 0
    private lateinit var questions: List<Questions>
    private var questionIndex:Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionText = view.findViewById(R.id.txt_Question)
        buttons = arrayOf(view.findViewById(R.id.btn_Ans1),
            view.findViewById(R.id.btn_Ans2),
            view.findViewById(R.id.btn_Ans3),
            view.findViewById(R.id.btn_Ans4))
        pointsView = view.findViewById(R.id.txt_Points)
        val db = Room.databaseBuilder(
            activity!!.applicationContext,
            QuestionsDatabase::class.java, "questions"
        ).fallbackToDestructiveMigration()
            .build()

        val questionsList = listOf(
            Questions(question = "Question 1", answer1 = "Answer 1-1", answer2 = "Answer 1-2", answer3 = "Answer 1-3", answer4 = "Answer 1-4", correctAnswer = 1, alreadyUsed = false),
            Questions(question = "Question 2", answer1 = "Answer 2-1", answer2 = "Answer 2-2", answer3 = "Answer 2-3", answer4 = "Answer 2-4", correctAnswer = 2, alreadyUsed = false),
            Questions(question = "Question 3", answer1 = "Answer 3-1", answer2 = "Answer 3-2", answer3 = "Answer 3-3", answer4 = "Answer 3-4", correctAnswer = 3, alreadyUsed = false),
            Questions(question = "Question 4", answer1 = "Answer 4-1", answer2 = "Answer 4-2", answer3 = "Answer 4-3", answer4 = "Answer 4-4", correctAnswer = 4, alreadyUsed = false),
            Questions(question = "Question 5", answer1 = "Answer 5-1", answer2 = "Answer 5-2", answer3 = "Answer 5-3", answer4 = "Answer 5-4", correctAnswer = 1, alreadyUsed = false),
            Questions(question = "Question 6", answer1 = "Answer 6-1", answer2 = "Answer 6-2", answer3 = "Answer 6-3", answer4 = "Answer 6-4", correctAnswer = 2, alreadyUsed = false),
            Questions(question = "Question 7", answer1 = "Answer 7-1", answer2 = "Answer 7-2", answer3 = "Answer 7-3", answer4 = "Answer 7-4", correctAnswer = 3, alreadyUsed = false),
            Questions(question = "Question 8", answer1 = "Answer 8-1", answer2 = "Answer 8-2", answer3 = "Answer 8-3", answer4 = "Answer 8-4", correctAnswer = 4, alreadyUsed = false),
            Questions(question = "Question 9", answer1 = "Answer 9-1", answer2 = "Answer 9-2", answer3 = "Answer 9-3", answer4 = "Answer 9-4", correctAnswer = 1, alreadyUsed = false),
            Questions(question = "Question 10", answer1 = "Answer 10-1", answer2 = "Answer 10-2", answer3 = "Answer 10-3", answer4 = "Answer 10-4", correctAnswer = 2, alreadyUsed = false)
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                //db.questionsDao().insertQuestions(questionsList)
                questions = db.questionsDao().getAll()
            }
            questionText.text = questions[0].question
            buttons[0].text = questions[0].answer1
            buttons[1].text = questions[0].answer2
            buttons[2].text = questions[0].answer3
            buttons[3].text = questions[0].answer4
        }


        buttons[0].setOnClickListener {
            nextQuestion(1)
        }
        buttons[1].setOnClickListener {
            nextQuestion(2)
        }
        buttons[2].setOnClickListener {
            nextQuestion(3)
        }
        buttons[3].setOnClickListener {
            nextQuestion(4)
        }

    }
    fun nextQuestion(number:Int){
        //if(number == questions[questionIndex].correctAnswer){       }

        if(questionIndex < questions.size){
            questionText.text = questions[questionIndex].question
            buttons[0].text = questions[questionIndex].answer1
            buttons[1].text = questions[questionIndex].answer2
            buttons[2].text = questions[questionIndex].answer3
            buttons[3].text = questions[questionIndex].answer4
            ++questionIndex
        }


    }
    fun addPoint(){
        pointCounter++
        pointsView.text = pointCounter.toString()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}