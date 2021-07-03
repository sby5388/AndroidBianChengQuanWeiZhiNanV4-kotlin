package com.by5388.learn.v4.kotlin.photogallery

import org.junit.After
import org.junit.Before
import org.junit.Test

class FlickrFetchr2Test {
    private lateinit var mSubject: FlickrFetchr2

    @Before
    fun setUp() {
        mSubject = FlickrFetchr2()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testFetchPhotos() {
        mSubject.testFetchPhotos()
    }
}