package com.by5388.learn.v4.kotlin.beatbox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.by5388.learn.v4.kotlin.beatbox.databinding.ListItemSoundBinding

class SoundAdapter(private val mBeatBox: BeatBox) : RecyclerView.Adapter<SoundHolder>() {
    private val sounds: List<Sound> = mBeatBox.sounds

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ListItemSoundBinding =
            DataBindingUtil.inflate(inflater, R.layout.list_item_sound, parent, false)
        return SoundHolder(binding, mBeatBox)
    }

    override fun onBindViewHolder(holder: SoundHolder, position: Int) {
        holder.bind(sounds[position])
    }

    override fun getItemCount(): Int {
        return sounds.size
    }
}