package com.by5388.learn.v4.kotlin.beatbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.by5388.learn.v4.kotlin.beatbox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mBeatBox: BeatBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBeatBox = BeatBox(assets)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = SoundAdapter(mBeatBox.sounds)
        }

    }
}