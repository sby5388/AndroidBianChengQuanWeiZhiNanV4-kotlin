package com.by5388.learn.v4.kotlin.beatbox

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import com.by5388.learn.v4.kotlin.beatbox.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private var mBeatBox: BeatBox? = null
    var mFirst = true

    private val mBeatBoxViewModel: BeatBoxViewModel by lazy {
        // FIXME: 2021/6/4 这种方式出错了:旋转屏幕时 每次都会创建 BeatBoxViewModel 实例
        //defaultViewModelProviderFactory.create(BeatBoxViewModel::class.java)
        // TODO: 2021/6/4 这种方式正确：旋转屏幕时 只会创建一次BeatBoxViewModel实例
        ViewModelProviders.of(this).get(BeatBoxViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //mBeatBoxViewModel = defaultViewModelProviderFactory.create(BeatBoxViewModel::class.java)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            //adapter = SoundAdapter(mBeatBox.sounds, mBeatBox)
        }
        mBinding.seekbarSpeed.max = 100
        mBinding.seekbarSpeed.progress = 100
        updateText()
        mBinding.seekbarSpeed.setOnSeekBarChangeListener(object :
        // TODO: 2021/6/30 接口类的构造方法无需加(),或者根本就没有构造方法
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mBeatBox?.setRate(seekBar.progress.div(100.0f))
                    updateText()
                }

            }
        })
        mBeatBoxViewModel.getLiveDataBeatBox()
            .observe(this, Observer {
                it?.let {
                    mBeatBox = it
                    mBinding.recyclerView.apply {
                        layoutManager = GridLayoutManager(this@MainActivity, 3)
                        adapter = SoundAdapter(mBeatBox!!)
                    }
                    mBinding.seekbarSpeed.progress = it.getRate.times(100).toInt()
                    updateText()
                }
            })
    }

    private fun updateText() {
        Log.d(TAG, "updateText: ${mBinding.seekbarSpeed.progress}")
        mBinding.textViewSpeed.text = getString(R.string.play_speed, mBinding.seekbarSpeed.progress)
    }


    override fun onDestroy() {
        super.onDestroy()
        // fixme: 2021/6/30 新的问题：如果在这里选择release(),那么旋转屏幕后 重建Activity将会导致BeatBox没有声音，
        // 应该在应用完全退出时 释放这个对象
        //mBeatBox?.release()
    }
}