package com.by5388.learn.v4.kotlin.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 * @author  admin  on 2021/6/3.
 */
private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    init {
        Log.d(TAG, "QuizViewModel instance created")
    }

    var mCurrentIndex = 0
    var mCount = 0
    var mCountRight = 0

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )


    fun addCount() {
        mCount++
    }

    fun addCountRight() {
        mCountRight++
    }

    fun setCurrentQuestionCheck() {
        questionBank[mCurrentIndex].check = true
    }

    val questionSize: Int
        get() = questionBank.size


    val currentQuestionAnswer: Boolean
        get() = questionBank[mCurrentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[mCurrentIndex].textResId

    val currentQuestionCheck: Boolean
        get() = questionBank[mCurrentIndex].check

    fun moveToNext() {
        mCurrentIndex = (mCurrentIndex + 1) % questionBank.size
    }

    fun moveToPre() {
        mCurrentIndex = (mCurrentIndex - 1 + questionBank.size) % questionBank.size
    }


    /**
     * 销毁
     */
    override

    fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: QuizViewModel instance destroy")
    }
}