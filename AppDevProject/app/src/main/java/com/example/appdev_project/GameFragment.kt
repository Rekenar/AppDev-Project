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
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.appdev_project.database.*
import androidx.navigation.fragment.navArgs
import com.example.appdev_project.databinding.FragmentGameBinding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.*


class GameFragment : Fragment() {
    lateinit var sensorManager : SensorManager
    private var accel : Float = 0f
    private var accelCurrent : Float = 0f
    private var accelLast : Float = 0f

    private lateinit var binding: FragmentGameBinding
    var pointCounter : Int = 0
    private lateinit var questions: List<QuestionsDataClass>
    private var questionIndex:Int = 0
    lateinit var difficulty:String
    private var hints: Int = 3
    private lateinit var db:QuestionsDatabase

    private val args: GameFragmentArgs by navArgs()
    private var id:Int = 0

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
        binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = args.identifier

        binding.txtHints.text = "Skips: $hints"



        setupUI()
        fetchQuestions()
    }

    private fun setupUI() {
        binding.apply {
            btnAns1.setOnClickListener { nextQuestion(0) }
            btnAns2.setOnClickListener { nextQuestion(1) }
            btnAns3.setOnClickListener { nextQuestion(2) }
            btnAns4.setOnClickListener { nextQuestion(3) }
        }
    }

    private fun fetchQuestions() {
        lifecycleScope.launch {
            try {
                db = DatabaseCompanionObject.buildDatabase(requireContext())
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    withContext(Dispatchers.IO) {
                        val questionList = db.questionsDao().getQuestions(id).shuffled()
                        questions = convertQuestionsListToQuestionsDataClassList(questionList)
                        difficulty = questions[0].difficulty
                    }
                    updateQuestion()
                    refreshAnswerCounter()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (::db.isInitialized && db.isOpen) {
                    db.close()
                }
            }
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
                    binding.txtHints.text = "Skips: $hints"
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
        if(questions.lastIndex <= questionIndex){
            val action = GameFragmentDirections.actionGameFragmentToOverviewFragment(args.identifier)
            this.findNavController().navigate(action)
        }
        else if(questionIndex < questions.size || number == -1 ) {
            if(number == -1 || questions[questionIndex].answers[number] == questions[questionIndex].correctAnswer){
                Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
                pointCounter++
            }
            else{
                pointCounter = 0
            }
            questionIndex++
            refreshPointCounter()
            refreshAnswerCounter()
            updateQuestion()
            checkForAchievement()
        }
    }

    private fun checkForAchievement() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = DatabaseCompanionObject.buildDatabase(requireContext())


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
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (::db.isInitialized && db.isOpen) {
                    db.close()
                }
            }
        }
    }



    private fun updateQuestion(){
        if (questionIndex < questions.size && questions.isNotEmpty()) {
            binding.apply {
                txtQuestion.text = questions[questionIndex].question
                btnAns1.text = questions[questionIndex].answers[0]
                btnAns2.text = questions[questionIndex].answers[1]
                btnAns3.text = questions[questionIndex].answers[2]
                btnAns4.text = questions[questionIndex].answers[3]
            }
        }
    }



    private fun refreshPointCounter(){
        binding.txtCorrectAnswers.text = pointCounter.toString()
    }
    private fun refreshAnswerCounter(){
        binding.txtAmountOfAnswers.text = "${questionIndex+1} / ${questions.size}"
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