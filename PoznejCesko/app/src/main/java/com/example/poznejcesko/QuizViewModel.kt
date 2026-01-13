package com.example.poznejcesko

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.poznejcesko.data.PoznejCeskoRepository
import com.example.poznejcesko.data.Question
import com.example.poznejcesko.data.Score
import com.example.poznejcesko.data.UserRegionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: PoznejCeskoRepository) : ViewModel() {

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?> = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> = _quizFinished

    private val _starsEarned = MutableLiveData(0)
    val starsEarned: LiveData<Int> = _starsEarned

    private val _questionsCount = MutableLiveData(0)
    val questionsCount: LiveData<Int> = _questionsCount

    private val _answerFeedback = MutableLiveData<Pair<Int, Boolean>?>()
    val answerFeedback: LiveData<Pair<Int, Boolean>?> = _answerFeedback

    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var regionId: Int = -1
    private var userId: Int = -1
    private var isProcessingAnswer = false

    fun startQuiz(regionId: Int, userId: Int) {
        this.regionId = regionId
        this.userId = userId
        viewModelScope.launch {
            val allQuestions = repository.getQuestionsForRegion(regionId)
            questions = allQuestions.shuffled().take(5)
            
            _questionsCount.value = questions.size
            if (questions.isNotEmpty()) {
                currentQuestionIndex = 0
                _score.value = 0
                _starsEarned.value = 0
                _currentQuestion.value = questions[currentQuestionIndex]
            } else {
                _quizFinished.value = true
            }
        }
    }

    fun onAnswerSelected(answerIndex: Int) {
        if (isProcessingAnswer) return
        val question = _currentQuestion.value ?: return

        isProcessingAnswer = true
        val isCorrect = answerIndex == question.correctAnswerIndex

        if (isCorrect) {
            _score.value = (_score.value ?: 0) + 10
        }

        _answerFeedback.value = Pair(answerIndex, isCorrect)

        viewModelScope.launch {
            delay(800) 
            _answerFeedback.value = null
            isProcessingAnswer = false
            moveToNextQuestion()
        }
    }

    private fun moveToNextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            _currentQuestion.value = questions[currentQuestionIndex]
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            val finalScore = _score.value ?: 0
            
            val stars = when (finalScore) {
                50 -> 3
                in 30..40 -> 2
                in 10..20 -> 1
                else -> 0
            }
            _starsEarned.value = stars

            val scoreData = Score(
                userId = userId,
                regionId = regionId,
                score = finalScore,
                starsEarned = stars
            )
            repository.insertScore(scoreData)

            val currentState = repository.getUserRegionState(userId, regionId)
            val bestScore = Math.max(currentState?.bestScore ?: 0, finalScore)
            
            val newState = UserRegionState(
                userId = userId,
                regionId = regionId,
                isUnlocked = true,
                bestScore = bestScore,
                maxPoints = 50
            )
            repository.updateUserRegionState(newState)

            checkAndUnlockNextRegion(finalScore)

            _quizFinished.value = true
        }
    }
    
    private suspend fun checkAndUnlockNextRegion(currentScore: Int) {
        val currentRegion = repository.getRegion(regionId) ?: return
        val nextRegionOrder = currentRegion.order + 1
        val nextRegion = repository.getRegionByOrder(nextRegionOrder) ?: return
        
        if (currentScore >= nextRegion.requiredScoreToUnlock) {
             repository.unlockRegionForUser(userId, nextRegion.id)
        }
    }
}

class QuizViewModelFactory(private val repository: PoznejCeskoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
