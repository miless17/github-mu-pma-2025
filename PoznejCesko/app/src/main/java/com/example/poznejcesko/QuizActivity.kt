package com.example.poznejcesko

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.poznejcesko.data.Question
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import android.content.res.ColorStateList


class QuizActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModelFactory((application as PoznejCeskoApplication).repository)
    }

    private lateinit var questionTextView: TextView
    private lateinit var option1Button: MaterialButton
    private lateinit var option2Button: MaterialButton
    private lateinit var option3Button: MaterialButton
    private lateinit var option4Button: MaterialButton
    private lateinit var scoreTextView: TextView
    private lateinit var progressBar: LinearProgressIndicator

    private val optionButtons by lazy {
        listOf(option1Button, option2Button, option3Button, option4Button)
    }

    private var totalQuestionsCount = 0
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        questionTextView = findViewById(R.id.questionTextView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        option4Button = findViewById(R.id.option4Button)
        scoreTextView = findViewById(R.id.scoreTextView)
        progressBar = findViewById(R.id.quizProgressBar)

        val regionId = intent.getIntExtra(EXTRA_REGION_ID, -1)
        val userId = intent.getIntExtra(EXTRA_USER_ID, -1)

        if (regionId == -1 || userId == -1) {
            Toast.makeText(this, "Chyba: Uživatel nebo kraj nebyl nalezen", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        quizViewModel.startQuiz(regionId, userId)

        quizViewModel.questionsCount.observe(this) { count ->
            totalQuestionsCount = count
            currentQuestionIndex = 0
            progressBar.progress = 0
        }

        quizViewModel.currentQuestion.observe(this) { question ->
            resetButtonStyles()
            updateUiForQuestion(question)
            if (question != null) {
                currentQuestionIndex++
                updateProgress()
            }
        }
        
        quizViewModel.score.observe(this) { score ->
            scoreTextView.text = "Body: $score"
        }

        quizViewModel.quizFinished.observe(this) { isFinished ->
            if (isFinished) {
                val finalScore = quizViewModel.score.value ?: 0
                // Přebíráme hvězdy vypočítané ve ViewModelu
                val stars = quizViewModel.starsEarned.value ?: 0
                
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_SCORE, finalScore)
                intent.putExtra(ResultActivity.EXTRA_STARS, stars)
                startActivity(intent)
                finish()
            }
        }
        
        quizViewModel.answerFeedback.observe(this) { feedback ->
            feedback?.let { (index, isCorrect) ->
                highlightAnswer(index, isCorrect)
            }
        }
    }
    
    private fun updateProgress() {
        if (totalQuestionsCount > 0) {
            val progress = (currentQuestionIndex.toFloat() / totalQuestionsCount * 100).toInt()
            progressBar.setProgress(progress, true)
        }
    }

    private fun updateUiForQuestion(question: Question?) {
        question ?: return
        
        questionTextView.text = question.text
        optionButtons.forEach { it.visibility = View.GONE }
        
        question.options.forEachIndexed { index, option ->
            if(index < optionButtons.size) {
                val button = optionButtons[index]
                button.visibility = View.VISIBLE
                button.text = option
                button.isEnabled = true
                button.setOnClickListener {
                    quizViewModel.onAnswerSelected(index)
                }
            }
        }
    }
    
    private fun resetButtonStyles() {
        val primaryColor = ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary)
        optionButtons.forEach { button ->
            button.strokeColor = ContextCompat.getColorStateList(this, com.google.android.material.R.color.design_default_color_primary)
            button.setTextColor(primaryColor)
            button.setBackgroundColor(Color.TRANSPARENT)
            button.icon = null
        }
    }

    private fun highlightAnswer(index: Int, isCorrect: Boolean) {
        if (index in optionButtons.indices) {
            val button = optionButtons[index]
            if (isCorrect) {
                button.setBackgroundColor(Color.parseColor("#E8F5E9"))
                button.strokeColor = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                button.setTextColor(Color.parseColor("#2E7D32"))
                button.setIconResource(android.R.drawable.checkbox_on_background)
                button.iconTint = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            } else {
                button.setBackgroundColor(Color.parseColor("#FFEBEE"))
                button.strokeColor = ColorStateList.valueOf(Color.parseColor("#F44336"))
                button.setTextColor(Color.parseColor("#C62828"))
                button.setIconResource(android.R.drawable.ic_delete)
                button.iconTint = ColorStateList.valueOf(Color.parseColor("#F44336"))
            }
            
            optionButtons.forEach { it.isEnabled = false }
        }
    }

    companion object {
        const val EXTRA_REGION_ID = "com.example.poznejcesko.REGION_ID"
        const val EXTRA_USER_ID = "com.example.poznejcesko.USER_ID"
    }
}
