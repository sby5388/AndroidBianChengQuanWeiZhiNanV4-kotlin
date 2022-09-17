package com.by5388.learn.v4.kotlin.criminalintent

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.text.SimpleDateFormat
import java.util.*

class CrimeItemViewModel(private val context: Context) : BaseObservable() {
    private var mCrime: Crime? = null
    private val mDateFormat =
        SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault())
    var callback: CrimeCallback? = null

    init {

    }

    fun bind(crime: Crime) {

        mCrime = crime
        notifyPropertyChanged(BR.title)
        notifyPropertyChanged(BR.date)
        notifyPropertyChanged(BR.contentDescription)
        notifyPropertyChanged(BR.imageVisibility)

    }

    fun callPolice(v: View) {
        mCrime?.let {
            Toast.makeText(
                context,
                "${it.title} ${context.getString(R.string.call_police)}",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun toDetail(v: View) {
        mCrime?.let {
            callback?.onClick(it)
        }

    }

    val title: String
        @Bindable
        get() = mCrime?.title ?: ""


    val date: String
        @Bindable
        get() = mCrime?.let {
            mDateFormat.format(it.date)
        } ?: ""

    val contentDescription: String
        @Bindable
        get() = mCrime?.let {
            "${it.title} ,${context.getString(getSolved(it))} ,${mDateFormat.format(it.date)}"
        } ?: ""

    val imageVisibility: Int
        @Bindable
        get() = mCrime?.let { if (it.isSolved) View.INVISIBLE else View.VISIBLE } ?: View.INVISIBLE

    private fun getSolved(crime: Crime): Int {
        return if (crime.isSolved) {
            R.string.crime_report_solved
        } else {
            R.string.crime_report_unsolved
        }
    }

    interface CrimeCallback {
        fun onClick(crime: Crime)
    }


}