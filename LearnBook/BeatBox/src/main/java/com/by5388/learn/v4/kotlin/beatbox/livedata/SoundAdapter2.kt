package com.by5388.learn.v4.kotlin.beatbox.livedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.by5388.learn.v4.kotlin.beatbox.R
import com.by5388.learn.v4.kotlin.beatbox.Sound
import com.by5388.learn.v4.kotlin.beatbox.databinding.ListItemSoundLiveDataBinding

class SoundAdapter2(private val sounds: List<Sound>, private val mLifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<SoundHolder2>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder2 {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ListItemSoundLiveDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.list_item_sound_live_data, parent, false)
        binding.lifecycleOwner = mLifecycleOwner
        return SoundHolder2(binding)
    }

    override fun onBindViewHolder(holder: SoundHolder2, position: Int) {
        holder.bind(sounds[position])
    }

    override fun getItemCount(): Int {
        return sounds.size
    }
}