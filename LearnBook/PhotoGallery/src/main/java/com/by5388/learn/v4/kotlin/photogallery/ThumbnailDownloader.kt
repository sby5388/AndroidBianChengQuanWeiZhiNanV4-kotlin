package com.by5388.learn.v4.kotlin.photogallery

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.collection.LruCache
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

/**
 * 图片下载工具
 * kotlin 声明范型
 * LifecycleObserver:实现感知生命周期组件
 */
class ThumbnailDownloader<in T>(
    private val mResponseHandler: Handler,
    //private val mLifecycle: Lifecycle,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG) {
    private var mHasQuit = false
    private lateinit var mRequestHandler: Handler
    private val mRequestMap: ConcurrentHashMap<T, String> = ConcurrentHashMap()
    private val mFlickrFetchr = FlickrFetchr()


    private val mCache: LruCache<String, Bitmap>

    init {
        //mLifecycle.addObserver(fragmentLifecycleObserver)
        val cacheSize = Runtime.getRuntime().maxMemory().div(100).toInt()
        mCache = LruCache(cacheSize)
    }

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mRequestHandler = object : Handler(looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL:${mRequestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    override fun quit(): Boolean {
        mHasQuit = true
        return super.quit()
    }

    /**
     * 从技术实现来看，创建Handler实现也创建了一个内部类。
     * 内部类天然 持有外部类(这里指ThumbnailDownloader类)的实例引用。
     * 这样一来，如果内部类的生命周期比外部类长，就会出现外部类的内存泄漏问题。
     * 不过，只有在把Handler添加给主线程的looper时才会有此问题。
     * 这里， 使用@SuppressLint("HandlerLeak")的作用是不让Lint报警，
     * 因为此处创 建的Handler是添加给后台线程的looper的。
     * 如果把你创建的Handler添加 给主线程的looper，
     * 那么它就不会被垃圾回收，自然就会内存泄漏，
     * 进 而导致它引用的ThumbnailDownloader实例也引发内存泄漏问题。
     */
    private fun handleRequest(target: T) {
        val url = mRequestMap[target] ?: return
        val bitmap = mCache[url] ?: mFlickrFetchr.fetchPhoto(url) ?: return
        mResponseHandler.post(Runnable {
            if (mRequestMap[target] != url || mHasQuit) {
                return@Runnable
            }
            mRequestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
            mCache.put(url, bitmap)
        })
    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "queueThumbnail: $url")
        mRequestMap[target] = url
        mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }


    val fragmentLifecycleObserver: LifecycleObserver =
        object : LifecycleObserver {
            /**
             * 当生命周期所有者创建时，回调，通过注解
             */
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun setup() {
                Log.i(TAG, "setup: Starting background thread")
                start()
                looper
            }

            /**
             * 当生命周期所有者销毁时，回调，通过注解
             */
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun tearDown() {
                Log.i(TAG, "Destroying background thread")
                quit()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearQueue() {
                Log.i(TAG, "Clearing all requests from queue")
                mRequestHandler.removeMessages(MESSAGE_DOWNLOAD)
                mRequestMap.clear()
            }

        }

}