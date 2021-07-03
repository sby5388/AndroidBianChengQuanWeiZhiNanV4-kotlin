package com.by5388.learn.v4.kotlin.photogallery

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoGalleryActivityTest {

    //测试规则：测试前启动这个类的实例：MainActivity
    @get:Rule
    val activityRule = ActivityTestRule(PhotoGalleryActivity::class.java)

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun test1() {
        val activity = activityRule.activity
        activity.test()
    }
}