package com.by5388.learn.v4.kotlin.photogallery

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "PollWorker"

class PollWorker(private val mContext: Context, workerParameters: WorkerParameters) :
    Worker(mContext, workerParameters) {

    /**
     * 轮询
     * 运行在后台线程
     * 返回三种结果：
     * [androidx.work.ListenableWorker.Result.Failure] 失败：任务不再运行
     * [androidx.work.ListenableWorker.Result.Retry] 重试：出现问题，可以再次运行
     * [androidx.work.ListenableWorker.Result.Success] 成功：任务成功
     * TODO 原文 "doWork()会在后台线程上调用，你不能安排它做任何耗时任务"？？
     *  -->"doWork()会在后台线程上调用，你不能安排它做任何 UI 操作"
     */
    override fun doWork(): Result {
        //Work request triggered:工作请求已触发
        Log.i(TAG, "Work request triggered")
        val storedQuery = QueryPreferences.getStoredQuery(mContext)
        val lastResultId = QueryPreferences.getLastResultId(mContext)

        val item: List<GalleryItem> = if (storedQuery.isEmpty()) {
            FlickrFetchr().fetchPhotoRequest()
                .execute()
                .body()?.galleryItems
        } else {
            FlickrFetchr().searchPhotosRequest(storedQuery)
                .execute()
                .body()?.galleryItems
        } ?: emptyList()

        if (item.isEmpty()) {
            return Result.success()
        }
        val resultId = item.first().id
        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            //更新新的ID
            Log.i(TAG, "Got a new result: $resultId")
            QueryPreferences.setLastResultId(mContext, resultId)
            showNotification()
        }

        return Result.success()
    }

    /**
     * 设置通知栏事件
     */
    private fun showNotification() {
        val newIntent = PhotoGalleryActivity.newIntent(mContext)
        val pendingIntent = PendingIntent.getActivity(mContext, 0, newIntent, 0)
        val resources = mContext.resources
        val notification = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
            //记号文字：不会显示出来，但是会发送给Android辅助服务使用，如屏幕阅读器会用它通知有视力障碍的用户
            .setTicker(resources.getString(R.string.new_pictures_title))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_pictures_title))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        //使用了兼容包
//        val notificationManager = NotificationManagerCompat.from(mContext)
//        notificationManager.notify(0, notification)

        showBackgroundNotification(0, notification)

    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification) {
        val intent = Intent(ACTION_SHOW_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notification)
        }
        //发送带权限限制的有序广播：只有声明并获取权限的广播接收者可以接受到
        mContext.sendOrderedBroadcast(intent, PERMISSION_PRIVATE)

    }


    companion object {
        const val ACTION_SHOW_SHOW_NOTIFICATION = BuildConfig.APPLICATION_ID + ".SHOW_NOTIFICATION"
        const val PERMISSION_PRIVATE = BuildConfig.APPLICATION_ID + ".PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}