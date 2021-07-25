package com.by5388.learn.v4.kotlin.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.*

private const val TAG = "BoxDrawingView"
private const val KEY_SUPER_DATA = "super_data"
private const val KEY_BOX = "box"
private const val KEY_BOX_LIST = "boxList"

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
            canvas.drawRect(box.left, box.top, box.right, box.bottom, mBoxPaint)
        }
    }

    /**
     * 需要View设置了ID:保存数据
     * https://blog.csdn.net/artzok/article/details/50172657
     */
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(KEY_SUPER_DATA, superState)
        bundle.putParcelable(KEY_BOX, mCurrentBox)
        bundle.putParcelableArrayList(KEY_BOX_LIST, mBoxList as ArrayList<Box>)
        return bundle
    }

    /**
     * 需要View设置了ID：
     * https://blog.csdn.net/artzok/article/details/50172657
     */
    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        val superData = bundle.getParcelable<Parcelable>(KEY_SUPER_DATA)
        mCurrentBox = bundle.getParcelable(KEY_BOX)
        val boxList = bundle.getParcelableArrayList<Box>(KEY_BOX_LIST)
        boxList?.let {
            mBoxList.clear()
            mBoxList.addAll(boxList)
        }
        // TODO: 2021/7/25 不能将原始数据传递给父类，否则会奔溃
        super.onRestoreInstanceState(superData)

    }


    private fun updateCurrentBox(current: PointF) {
        mCurrentBox?.let {
            it.end = current
            invalidate()
        }
    }
}