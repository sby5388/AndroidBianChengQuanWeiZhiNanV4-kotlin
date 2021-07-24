package com.by5388.learn.v4.kotlin.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

private const val TAG = "BoxDrawingView"

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var mCurrentBox: Box? = null
    private val mBoxList = mutableListOf<Box>()
    private val mBoxPaint = Paint().apply {
        color = 0x22ff0000.toInt()
    }
    private val mBgPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                mCurrentBox = Box(current).also {
                    mBoxList.add(it)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                updateCurrentBox(current);
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                updateCurrentBox(current)
                mCurrentBox = null

            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                action = "ACTION_UP"
            }
        }
        Log.i(TAG, "$action at x=${current.x},y=${current.y}: ")
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPaint(mBgPaint)
        mBoxList.forEach { box ->
            canvas.drawRect(box.left, box.top, box. right, box.bottom, mBoxPaint)
        }
    }

    private fun updateCurrentBox(currenct: PointF) {
        mCurrentBox?.let {
            it.end = currenct
            invalidate()
        }
    }
}