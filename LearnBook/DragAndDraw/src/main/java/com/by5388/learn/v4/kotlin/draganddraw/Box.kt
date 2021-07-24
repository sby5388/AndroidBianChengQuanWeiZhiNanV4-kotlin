package com.by5388.learn.v4.kotlin.draganddraw

import android.graphics.PointF

class Box(private val start: PointF) {
    var end: PointF = start
    //Math.min(start.x, end.x)
    val left: Float
        get() = start.x.coerceAtMost(end.x)
    //Math.max(start.x, end.x)
    val right: Float
        get() = start.x.coerceAtLeast(end.x)
    //min
    val top: Float
        get() = start.y.coerceAtMost(end.y)
    //max
    val bottom: Float
        get() = start.y.coerceAtLeast(end.y)
}