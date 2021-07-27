package com.by5388.learn.v4.kotlin.sunset

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mSceneView: View
    private lateinit var mSkyView: View
    private lateinit var mSunView: View

    /**
     * true:执行日落动画
     * false:执行日出动画
     */
    private var mSunset = true

    private val mAnimatorListener: Animator.AnimatorListener =
        object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                mSceneView.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                mSceneView.isEnabled = true
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        }

    private val mBlueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val mSunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val mNightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSceneView = findViewById(R.id.scene)
        mSkyView = findViewById(R.id.sky)
        mSunView = findViewById(R.id.sun)

        mSceneView.setOnClickListener {
            if (mSunset) {
                startSunsetAnimation()
            } else {
                startSunRiseAnimation()
            }
            mSunset = !mSunset
        }
    }


    /**
     * 落日动画
     */
    private fun startSunsetAnimation() {
        //视图的顶部坐标
        //起始位置
        val sunYStart = mSunView.top.toFloat()
        //移动距离：height = top - bottom
        val sunYEnd = mSkyView.height.toFloat()

        val heightAnimator = ObjectAnimator
            .ofFloat(mSunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        //插值器:越来越快
        heightAnimator.interpolator = AccelerateInterpolator()

        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
            .setDuration(3000)
        //颜色变化：颜色渐变，过渡效果
        sunsetSkyAnimator.setEvaluator(ArgbEvaluator())

        val nightSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
            .setDuration(1500)
        nightSkyAnimator.setEvaluator(ArgbEvaluator())

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            //在nightAnimator之前，也就是heightAnimator播完之后播放nightAnimator
            .before(nightSkyAnimator)
        animatorSet.addListener(mAnimatorListener)
        animatorSet.start()
    }

    /**
     * 日出动画：就是日落动画的逆序
     */
    private fun startSunRiseAnimation() {

        //视图的顶部坐标
        //起始位置
        val sunYStart = mSkyView.height.toFloat()
        //移动距离：height = top - bottom
        val sunYEnd = mSunView.top.toFloat()

        val heightAnimator = ObjectAnimator
            .ofFloat(mSunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        //插值器:越来越快
        heightAnimator.interpolator = DecelerateInterpolator()

        val sunRiseSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
            .setDuration(3000)
        //颜色变化：颜色渐变，过渡效果
        sunRiseSkyAnimator.setEvaluator(ArgbEvaluator())

        val daySkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
            .setDuration(1500)
        daySkyAnimator.setEvaluator(ArgbEvaluator())


        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunRiseSkyAnimator)
            //在nightAnimator之后，也就是heightAnimator播之前播放daySkyAnimator
            .after(daySkyAnimator)
        animatorSet.addListener(mAnimatorListener)
        animatorSet.start()

    }
}