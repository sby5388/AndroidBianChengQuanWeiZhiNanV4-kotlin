package com.by5388.learn.v4.kotlin.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import java.io.File

/**
 * @author  admin  on 2021/6/8.
 */
/**
 * 生成等比例的图片
 */
fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap? {
    val file = File(path)
    if (!file.exists()) {
        return null
    }

    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    var inSampleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth
        inSampleSize = Math.round(Math.max(heightScale, widthScale))
    }
    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize
    return BitmapFactory.decodeFile(path, options)
}

fun getScaledBitmap(path: String, activity: Activity): Bitmap? {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)
    return getScaledBitmap(path, size.x, size.y)

}