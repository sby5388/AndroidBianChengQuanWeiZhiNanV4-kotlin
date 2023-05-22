package com.by5388.learn.v4.kotlin.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

const val NOTIFICATION_CHANNEL_ID = "flickr_poll"

class PhotoGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //注册 通知渠道
        registerNotificationChannel()
    }

    private fun registerNotificationChannel() {
        //Android 8.0 之后才生效
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channelName = getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }
}