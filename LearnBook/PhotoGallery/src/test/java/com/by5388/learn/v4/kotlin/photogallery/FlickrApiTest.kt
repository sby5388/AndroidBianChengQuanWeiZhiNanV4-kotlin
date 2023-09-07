package com.by5388.learn.v4.kotlin.photogallery

import com.by5388.learn.v4.kotlin.photogallery.api.FlickrApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class FlickrApiTest {
    private lateinit var mFlickrApi: FlickrApi

    @Before
    fun start() {
        println("start")
        val flickrFetchr = FlickrFetchr()
        mFlickrApi = flickrFetchr.mFlickrApi

    }

    @After
    fun tearDown() {
        println("tearDown")
    }

    @Test
    fun hellTest() {
        println("hello")
    }

    @Test
    fun searchPhotosTest() {
        //在mac上运行错误如下：
        //WARNING: An illegal reflective access operation has occurred
        //http://www.kuazhi.com/post/536319.html
        //需要把jdk换成jdk17+ 或者 retrofit 降版本为 2.7.x

        //测试方法只用同步的方式了
        val execute = mFlickrApi.searchPhotos("panda").execute()
        println("onResponse ${execute.body()}")
        val errorBody = execute.errorBody()
        println(errorBody)
    }

    @Test
    fun searchPhotosRxTest() {
        val disposable = mFlickrApi.searchPhotosRx("cat")
            .subscribe({ t -> println("searchPhotosRx $t") }
            ) { t -> println(t) }
        println(disposable)
    }

    @Test
    fun searchPhotosSuspend() {
        runBlocking {
            val photosSuspend = mFlickrApi.searchPhotosSuspend("China")
            println(photosSuspend)
        }
    }


}