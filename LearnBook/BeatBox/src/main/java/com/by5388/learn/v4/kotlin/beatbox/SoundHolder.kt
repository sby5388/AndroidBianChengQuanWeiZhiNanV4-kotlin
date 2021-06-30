package com.by5388.learn.v4.kotlin.beatbox

import androidx.recyclerview.widget.RecyclerView
import com.by5388.learn.v4.kotlin.beatbox.databinding.ListItemSoundBinding

class SoundHolder(private val mBinding: ListItemSoundBinding, private val mBeatBox: BeatBox) :
    RecyclerView.ViewHolder(mBinding.root) {
    init {
        mBinding.viewModel = SoundViewModel(mBeatBox)
    }

    fun bind(sound: Sound) {
        mBinding.apply {
            viewModel?.sound = sound
            //可以不使用，但用了可以立马刷新
            executePendingBindings()
        }
    }
}