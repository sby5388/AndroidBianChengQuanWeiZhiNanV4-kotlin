package com.by5388.learn.v4.kotlin.beatbox

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    //测试规则：测试前启动这个类的实例：MainActivity
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
    }

    @Test
    fun showsFirstFileName() {
        //isDisplayed:是否在视图上可见
        //
        onView(ViewMatchers.withText("65_cjipie"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun viewClick() {
//        onView(ViewMatchers.withText("65_cjipie"))
//            .perform()
    }

    @After
    fun tearDown() {
    }
}