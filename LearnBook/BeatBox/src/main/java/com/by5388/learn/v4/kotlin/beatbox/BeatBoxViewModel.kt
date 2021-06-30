package com.by5388.learn.v4.kotlin.beatbox

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "BeatBoxViewModel"

class BeatBoxViewModel(application: Application) : AndroidViewModel(application) {
    private val mLiveDataBeatBox: LiveData<BeatBox>

    init {
        val value = BeatBox(application.assets)
        mLiveDataBeatBox = MutableLiveData(value)
        Log.d(TAG, "create: ")
    }

    fun getLiveDataBeatBox(): LiveData<BeatBox> {
        return mLiveDataBeatBox
    }


}