package com.by5388.learn.v4.kotlin.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mSceneView: View
    private lateinit var mSkyView: View
    private lateinit var mSunView: View

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
            startAnimation()
        }
    }


    private fun startAnimation() {
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
        animatorSet.start()
    }
}