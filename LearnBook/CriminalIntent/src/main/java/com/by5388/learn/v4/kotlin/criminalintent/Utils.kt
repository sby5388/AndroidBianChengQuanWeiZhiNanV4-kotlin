package com.by5388.learn.v4.kotlin.criminalintent

import android.view.LayoutInflater
import android.view.View

fun View.inflater(): LayoutInflater {
    return LayoutInflater.from(this.context)
}