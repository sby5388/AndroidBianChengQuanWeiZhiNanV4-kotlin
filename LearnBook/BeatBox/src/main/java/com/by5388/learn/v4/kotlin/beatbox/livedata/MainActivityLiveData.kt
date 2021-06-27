package com.by5388.learn.v4.kotlin.beatbox.livedata

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.by5388.learn.v4.kotlin.beatbox.BeatBox
import com.by5388.learn.v4.kotlin.beatbox.R
import com.by5388.learn.v4.kotlin.beatbox.databinding.ActivityMainLiveDataBinding

class MainActivityLiveData : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainLiveDataBinding
    private lateinit var mBeatBox: BeatBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBeatBox = BeatBox(assets)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_live_data)
        mBinding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivityLiveData, 3)
            adapter = SoundAdapter2(mBeatBox.sounds, this@MainActivityLiveData)
        }

    }
}