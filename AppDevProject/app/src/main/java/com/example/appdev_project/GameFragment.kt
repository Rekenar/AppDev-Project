package com.example.appdev_project

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.appdev_project.database.*
import androidx.navigation.fragment.navArgs

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.*


class GameFragment : Fragment() {
    lateinit var sensorManager : SensorManager
    private var accel : Float = 0f
    private var accelCurrent : Float = 0f
    private var accelLast : Float = 0f
    lateinit var questionText: TextView
    lateinit var buttons :Array<Button>
    lateinit var pointsView : TextView
    var pointCounter : Int = 0
    private lateinit var questions: List<QuestionsDataClass>
    private var questionIndex:Int = 0
    lateinit var difficulty:String
    private var hints: Int = 3
    lateinit var hintsCounter: TextView
    private lateinit var db:QuestionsDatabase

    private val args: GameFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = this.context?.getSystemService(SENSOR_SERVICE) as SensorManager

        Objects.requireNonNull(sensorManager).registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hintsCounter = view.findViewById(R.id.txt_Hints)
        hintsCounter.text = "Skips: $hints"
        questionText = view.findViewById(R.id.txt_Question)
        buttons = arrayOf(view.findViewById(R.id.btn_Ans1),
            view.findViewById(R.id.btn_Ans2),
            view.findViewById(R.id.btn_Ans3),
            view.findViewById(R.id.btn_Ans4))
        pointsView = view.findViewById(R.id.txt_Points)

        val id = args.identifier

        for (i in 0..3){
            buttons[i].setOnClickListener {
                nextQuestion(i)
            }
        }

        try {
            db = DatabaseCompanionObject.buildDatabase(requireContext())

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    questions = convertQuestionsListToQuestionsDataClassList(db.questionsDao().getQuestions(id).shuffled())
                    difficulty = questions[0].difficulty
                }
                updateQuestion()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }

    }
    private val sensorListener: SensorEventListener = object : SensorEventListener{
        override fun onSensorChanged(event: SensorEvent?) {
            // Fetching x,y,z values
            val x = event?.values!![0]
            val y = event.values[1]
            val z = event.values[2]
            accelLast = accelCurrent

            // Getting current accelerations
            // with the help of fetched x,y,z values
            accelCurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = accelCurrent - accelLast
            accel = accel * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (accel > 14) {
                if(hints > 0) {
                    hints -= 1
                    Toast.makeText(context, "Shake detected", Toast.LENGTH_SHORT).show()
                    hintsCounter.text = "Skips: $hints"
                    nextQuestion(-1)
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }
    override fun onPause() {
        sensorManager.unregisterListener(sensorListener)
        super.onPause()
    }
    private fun nextQuestion(number: Int) {
        if(number == -1 && questionIndex < questions.size){
            pointCounter++
            questionIndex++
            updateQuestion()
            checkForAchievement()
        }else if(questions.lastIndex <= questionIndex){
            val action = GameFragmentDirections.actionGameFragmentToOverviewFragment(args.identifier)
            this.findNavController().navigate(action)
        }else if(questionIndex < questions.size) {
            if(questions[questionIndex].answers[number] == questions[questionIndex].correctAnswer){
                Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
                pointCounter++
            }
            else{
                pointCounter = 0
            }
            questionIndex++
            updateQuestion()
            checkForAchievement()
        }
    }

    private fun checkForAchievement() {
        try {
            val db = DatabaseCompanionObject.buildDatabase(requireContext())

            lifecycleScope.launch(Dispatchers.IO) {
                val achievements = db.achievementsDAO().getAllAchievements()

                val difficultyIndex = when (difficulty) {
                    "easy" -> 0
                    "medium" -> 1
                    "hard" -> 2
                    else -> 0
                }

                val pointThresholds = listOf(2, 5, 10, 25, 50)
                val achievementIndex = difficultyIndex * pointThresholds.size

                val currentThreshold = pointThresholds.indexOf(pointCounter)
                if (currentThreshold != -1) {
                    val targetAchievement = achievements[achievementIndex + currentThreshold]
                    targetAchievement.finished = true
                    db.achievementsDAO().updateAchievements(targetAchievement)
                }
            }
        } catch (e: Exception) {

        }
        finally {
            db.close()
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
                difficulty = questions.difficulty,
                question = questions.question,
                answers = answers.shuffled(),
                correctAnswer = questions.correctAnswer
            )
        }
    }


}