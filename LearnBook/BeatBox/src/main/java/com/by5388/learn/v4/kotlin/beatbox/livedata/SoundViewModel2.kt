package com.by5388.learn.v4.kotlin.beatbox.livedata

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.by5388.learn.v4.kotlin.beatbox.Sound

class SoundViewModel2 {

    val title: MutableLiveData<String?> = MutableLiveData()

    var sound: Sound? = null
        set(sound) {
            field = sound
            title.postValue(sound?.name)
        }

    fun onButtonClick(v: View) {
        Toast.makeText(v.context, sound?.name, Toast.LENGTH_SHORT).show()
    }
}