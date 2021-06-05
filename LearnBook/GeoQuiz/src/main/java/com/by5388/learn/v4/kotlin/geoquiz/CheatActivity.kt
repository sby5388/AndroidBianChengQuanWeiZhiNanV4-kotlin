package com.by5388.learn.v4.kotlin.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_ANSWER_IS_TRUE =
    "com.by5388.learn.v4.kotlin.geoquiz.extra_answer_is_true"
private const val EXTRA_ANSWER_SHOWN =
    "com.by5388.learn.v4.kotlin.geoquiz.extra_answer_shown"
private const val KEY_CHEAT = "cheat"

class CheatActivity : AppCompatActivity() {
    private lateinit var mAnswerTextView: TextView
    private lateinit var mShowAnswerButton: Button

    /**
     * 答案
     */
    private var mAnswerIsTrue = false

    /**
     * 是否作弊
     */
    private var mCheat = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        mAnswerTextView = findViewById(R.id.answer_text_view)
        mShowAnswerButton = findViewById(R.id.show_answer_button)

        mShowAnswerButton.setOnClickListener {
            val answerText = when {
                mAnswerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            mAnswerTextView.setText(answerText)
            mCheat = true
            setAnswerShowResult()
        }
        mCheat = savedInstanceState?.getBoolean(KEY_CHEAT, false) ?: false
        setAnswerShowResult()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_CHEAT, mCheat)
    }

    private fun setAnswerShowResult() {
        val apply = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, mCheat)
        }
        setResult(Activity.RESULT_OK, apply)
    }


    companion object {
        fun newIntent(context: Context, answerIsTrue: Boolean): Intent {
            return Intent(context, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }

        fun getShowAnswer(data: Intent?): Boolean {
            return data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
}