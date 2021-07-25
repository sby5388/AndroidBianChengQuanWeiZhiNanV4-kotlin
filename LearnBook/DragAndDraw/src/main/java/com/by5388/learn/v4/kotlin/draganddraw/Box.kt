package com.by5388.learn.v4.kotlin.draganddraw

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

class Box() : Parcelable {

    private lateinit var start: PointF
    lateinit var end: PointF

    constructor(s: PointF) : this() {
        start = s
        end = s
    }

    constructor(`in`: Parcel) : this() {
        start = PointF()
        end = PointF()
        start.x = `in`.readFloat()
        start.y = `in`.readFloat()
        end.x = `in`.readFloat()
        end.y = `in`.readFloat()
    }

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

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(start.x)
        dest.writeFloat(start.y)
        dest.writeFloat(end.x)
        dest.writeFloat(end.y)
    }

    companion object CREATOR : Parcelable.Creator<Box> {
        override fun createFromParcel(parcel: Parcel): Box {
            return Box(parcel)
        }

        override fun newArray(size: Int): Array<Box?> {
            return arrayOfNulls(size)
        }
    }


}