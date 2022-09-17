package com.by5388.learn.v4.kotlin.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var mButtonTrue: Button
    private lateinit var mButtonFalse: Button
    private lateinit var mButtonCheat: Button
    private lateinit var mButtonNext: ImageButton
    private lateinit var mButtonPre: ImageButton
    private lateinit var mQuestionTextView: TextView

    /**
     * TODO 惰性初始化
     * 使用by lazy关键字，可以确保quizViewModel属性是val类型，而不是var类型。
     * 这简直太棒了，因为只在activity实例对象被创建后，
     * 才需要获取和保存QuizViewModel，也就是说，quizViewModel一次只应该赋一个值。
     * 更为重要的是，使用了by lazy关键字，
     * quizViewModel的计算和赋值只在首次获取quizViewModel时才会发生。
     * 这很有用，因为只有在Activity.onCreate(...)被调用后，
     * 才能安全地获取到一个ViewModel。
     * 如果在Activity.onCreate(...)被调用之前调用
     * ViewModelProviders.of(this).get(QuizViewModel::class.java)，
     * 应用则会抛出IllegalStateException异常。
     */
    private val mQuizViewModel: QuizViewModel by lazy {
        // FIXME: 2021/6/4 这种方式出错了
        defaultViewModelProviderFactory.create(QuizViewModel::class.java)
        // TODO: 2021/6/4 这种方式正确
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mButtonTrue = findViewById(R.id.true_button)
        mButtonFalse = findViewById(R.id.false_button)
        mButtonCheat = findViewById(R.id.cheat_button)
        mButtonNext = findViewById(R.id.next_button)
        mButtonPre = findViewById(R.id.pre_button)
        mQuestionTextView = findViewById(R.id.question_text_view)


        val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)


        // TODO: 2021/6/5 当 savedInstanceState==null 时，取0
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.mCurrentIndex = currentIndex

        updateQuestion()

        mButtonTrue.setOnClickListener { _: View ->
            //Toast.makeText(this, R.string.correct_toast, Toast.LENGTH_SHORT).show()
            checkAnswer(true)
        }

        mButtonFalse.setOnClickListener {
            /*
            val makeText =
                Toast.makeText(this, R.string.incorrect_toast, Toast.LENGTH_SHORT)
                    .apply {
                        setGravity(Gravity.TOP, 0, 0)
                    }
            makeText.show()
             */
            checkAnswer(false)
        }
        mButtonNext.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        mButtonPre.setOnClickListener {
            quizViewModel.moveToPre()
            updateQuestion()
        }
        mButtonCheat.setOnClickListener { view ->
            toCheatActivity(view)
        }
        //add for 2.8　挑战练习：为TextView添加监听器
        mQuestionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(KEY_INDEX, mQuizViewModel.mCurrentIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            val cheat = CheatActivity.getShowAnswer(data)
            if (cheat) {
                //只要作弊了就不会重新更改为未作弊
                mQuizViewModel.setCurrentQuestionCheat(true)
            }
            updateQuestion()
        }
    }


    private fun updateQuestion() {
        val textResId = mQuizViewModel.currentQuestionText
        mQuestionTextView.setText(textResId)

        mButtonFalse.isEnabled = !mQuizViewModel.currentQuestionCheck
        mButtonTrue.isEnabled = !mQuizViewModel.currentQuestionCheck
        mButtonCheat.isEnabled = mQuizViewModel.canCheat
    }


    private fun checkAnswer(userAnswer: Boolean): Unit {
        mQuizViewModel.setCurrentQuestionCheck()
        mQuizViewModel.addCount()
        val answer = mQuizViewModel.currentQuestionAnswer
        val messageResId: Int = when {
            mQuizViewModel.currentQuestionCheat -> R.string.judgment_toast
            (userAnswer == answer) -> {
                mQuizViewModel.addCountRight()
                //最后一行是返回值
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }
        updateQuestion()
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        if (mQuizViewModel.mCount == mQuizViewModel.questionSize) {
            val d = mQuizViewModel.mCountRight * 100 / mQuizViewModel.mCount
            Toast.makeText(
                this,
                String.format("分数为%d", d),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun toCheatActivity(view: View) {
        val newIntent = CheatActivity.newIntent(this, mQuizViewModel.currentQuestionAnswer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val options = ActivityOptions
                .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
            startActivityForResult(newIntent, REQUEST_CODE_CHEAT, options.toBundle())
        } else {
            startActivityForResult(newIntent, REQUEST_CODE_CHEAT)
        }
    }

    private fun toCheatActivityCompat(view: View) {
        val newIntent = CheatActivity.newIntent(this, mQuizViewModel.currentQuestionAnswer)
        val optionsCompat =
            ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
        startActivityForResult(newIntent, REQUEST_CODE_CHEAT, optionsCompat.toBundle())
    }

    private fun toCheatActivity2(view: View) {
        val newIntent = CheatActivity.newIntent(
            this@MainActivity, mQuizViewModel.currentQuestionAnswer
        )
        startActivityForResult(newIntent, REQUEST_CODE_CHEAT)
    }

    /**
     * @Deprecated #onCreate
     */
    private fun oldFunction(): Unit {
        val provider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
    }

    // FIXME: 2021/6/4 P153 #4.2 出现了旋转页面 数据丢失的情况
}