package com.example.appdev_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.appdev_project.Overview.OverviewFragmentArgs
import com.example.appdev_project.database.DatabaseViewModel
import com.example.appdev_project.database.Question
import com.example.appdev_project.database.QuestionsDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GameFragment : Fragment() {

    lateinit var questionText: TextView
    lateinit var buttons :Array<Button>
    lateinit var pointsView : TextView
    var pointCounter : Int = 0
    private lateinit var questions: List<QuestionsDataClass>
    private var questionIndex:Int = 0
    private val db by viewModels<DatabaseViewModel>()
    private val args: GameFragmentArgs by navArgs()
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

        val id = args.identifier
        println("identifier is: " + id)

        for (i in 0..3){
            buttons[i].setOnClickListener {
                nextQuestion(i)
            }
        }


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                questions = convertQuestionsListToQuestionsDataClassList(db.getDB().questionsDao().getAll().shuffled())
            }
            updateQuestion()
        }
    }

    private fun nextQuestion(number: Int) {
        if (questionIndex < questions.size) {
            if(questions[questionIndex].answers[number] == questions[questionIndex].correctAnswer){
                Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
            }
            questionIndex++
            updateQuestion()
        }
    }


    private fun updateQuestion(){
        if(questionIndex < questions.size && questions.isNotEmpty()){
            questionText.text = questions[questionIndex].question
            buttons[0].text = questions[questionIndex].answers[0]
            buttons[1].text = questions[questionIndex].answers[1]
            buttons[2].text = questions[questionIndex].answers[2]
            buttons[3].text = questions[questionIndex].answers[3]
        }
    }
    fun addPoint(){
        pointCounter++
        pointsView.text = pointCounter.toString()
    }



    private fun convertQuestionsListToQuestionsDataClassList(questionList: List<Question>): List<QuestionsDataClass> {
        return questionList.map { questions ->
            val answers = listOf(
                questions.answer1,
                questions.answer2,
                questions.answer3,
                questions.answer4
            )

            QuestionsDataClass(
                uid = questions.uid,
                identifier = questions.identifier,
                category = questions.category,
                question = questions.question,
                answers = answers.shuffled(),
                correctAnswer = questions.correctAnswer
            )
        }
    }


}