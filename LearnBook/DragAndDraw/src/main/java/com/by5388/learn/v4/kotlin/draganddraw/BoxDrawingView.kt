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
import kotlin.math.atan2

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

    /**
     * 旋转参考自:https://blog.csdn.net/qq_27840621/article/details/75040216
     */
    var mPointer1: Boolean = false
    var mPointer2: Boolean = false

    var mPointerId1 = -1
    var mPointerId2 = -1

    private var mPointFOriginalOne: PointF? = null
    private var mPointFOriginalTwo: PointF? = null
    private var mPointFLastOne: PointF? = null
    private var mPointFLastTwo: PointF? = null
    private var mAngel: Float = 0.0f

    private var mX:Float = 0.0f
    private var mY:Float = 0.0f


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        // TODO: 2021/7/25 java中的& ：kotlin 使用 and
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                //第一个触摸点：按下
                mPointerId1 = event.getPointerId(0)
                action = "ACTION_DOWN"
                mCurrentBox = Box(current).also {
                    mBoxList.add(it)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                action = "ACTION_POINTER_DOWN"
                //其他触摸点按下
                val actionIndex = event.actionIndex
                mPointerId2 = event.getPointerId(actionIndex)
                mPointFOriginalOne = PointF().apply {
                    x = event.getX(event.findPointerIndex(mPointerId1))
                    y = event.getY(event.findPointerIndex(mPointerId1))
                }
                mPointFOriginalTwo = PointF().apply {
                    x = event.getX(event.findPointerIndex(mPointerId2))
                    y = event.getY(event.findPointerIndex(mPointerId2))
                }
                mCurrentBox = null
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                if (mPointerId1 != -1 && mPointerId2 != -1) {
                    mPointFLastOne = PointF().apply {
                        x = event.getX(event.findPointerIndex(mPointerId1))
                        y = event.getY(event.findPointerIndex(mPointerId1))
                    }
                    mPointFLastTwo = PointF().apply {
                        x = event.getX(event.findPointerIndex(mPointerId2))
                        y = event.getY(event.findPointerIndex(mPointerId2))
                    }
                    mAngel = angleBetweenLines(
                        mPointFOriginalOne,
                        mPointFOriginalTwo,
                        mPointFLastOne,
                        mPointFLastTwo
                    )

                }
                //单个手指时
                if (mCurrentBox != null && mPointerId2 == -1) {
                    updateCurrentBox(current)
                }else{
                    updateCurrentBox(current)
                }
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                mPointerId1 = -1
                mPointerId2 = -1
                updateCurrentBox(current)
                mCurrentBox = null

            }
            MotionEvent.ACTION_POINTER_UP -> {
                action = "ACTION_POINTER_UP"
                mPointerId2 = -1
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                mPointerId1 = -1
                mPointerId2 = -1
                mCurrentBox = null
            }
        }
        Log.i(TAG, "$action at x=${current.x},y=${current.y}: ")
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mY = (height / 2).toFloat()
        mX= (width / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPaint(mBgPaint)
//        canvas.save()
        mBoxList.forEach { box ->
            if (mAngel != 0.0f) {
                canvas.rotate(mAngel,mX,mY)
                mAngel = 0.0f
            }
            canvas.drawRect(box.left, box.top, box.right, box.bottom, mBoxPaint)
        }
//        canvas.restore()
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

    fun angleBetweenLines(
        fx: Float,
        fy: Float,
        sx: Float,
        sy: Float,
        nfx: Float,
        nfy: Float,
        nsx: Float,
        nsy: Float
    ): Float {
        val radian1 = Math.atan2((fy - sy).toDouble(), (fx - sx).toDouble()).toFloat()
        val radian2 =
            Math.atan2((nfy - nsy).toDouble(), (nfx - nsx).toDouble()).toFloat()
        var angle = (Math.toDegrees((radian2 - radian1).toDouble()) % 360).toFloat()
        if (angle < 0) {
            angle += 360f
        }
        return angle
    }

    private fun angleBetweenLines(
        one: PointF?,
        two: PointF?,
        newOne: PointF?,
        newTwo: PointF?
    ): Float {
        if (one == null || two == null || newOne == null || newTwo == null) {
            return 0.0f
        }
        val radian1 = atan2((one.y - two.y).toDouble(), (one.x - two.x).toDouble()).toFloat()
        val radian2 =
            atan2((newOne.y - newTwo.y).toDouble(), (newOne.x - newTwo.x).toDouble()).toFloat()
        var angle = (Math.toDegrees((radian2 - radian1).toDouble()) % 360).toFloat()
        if (angle < 0) {
            angle += 360f
        }
        return angle
    }
}