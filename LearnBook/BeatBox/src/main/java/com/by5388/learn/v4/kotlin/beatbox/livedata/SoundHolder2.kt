package com.by5388.learn.v4.kotlin.beatbox.livedata

import androidx.recyclerview.widget.RecyclerView
import com.by5388.learn.v4.kotlin.beatbox.Sound
import com.by5388.learn.v4.kotlin.beatbox.databinding.ListItemSoundLiveDataBinding

class SoundHolder2(private val mBinding: ListItemSoundLiveDataBinding) :
    RecyclerView.ViewHolder(mBinding.root) {
    init {
        mBinding.viewModel = SoundViewModel2()
    }

    fun bind(sound: Sound) {
        mBinding.apply {
            viewModel?.sound = sound
            //可以不使用，但用了可以立马刷新
            executePendingBindings()
        }
    }
}