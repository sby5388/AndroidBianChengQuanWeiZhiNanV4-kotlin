package com.by5388.learn.v4.kotlin.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log

private const val TAG = "BeatBox"
private const val SOUNDS_FOLDER = "sample_sounds"
private const val MAX_SOUNDS = 5

class BeatBox(private val assets: AssetManager) {

    val sounds: List<Sound>
    private val mSoundPool = SoundPool.Builder()
        //最多同事播放的音频数量
        .setMaxStreams(MAX_SOUNDS)
        .build()

    init {
        sounds = loadSounds();
    }

    private var mRate: Float = 1.0f

    fun setRate(rate: Float) {
        Log.d(TAG, "setRate: rate = $rate")
        this.mRate = rate
    }

    val getRate get() = mRate


    private fun loadSounds(): List<Sound> {
        return try {
            val sounds = mutableListOf<Sound>()
            val soundNames = assets.list(SOUNDS_FOLDER)!!
            soundNames.forEach { fileName ->
                val assetPath = "$SOUNDS_FOLDER/$fileName"
                val sound = Sound(assetPath)
                try {
                    load(sound)
                    sounds.add(sound)
                } catch (e: Exception) {
                    Log.e(TAG, "loadSounds: ", e)
                }
            }
            sounds
        } catch (e: Exception) {
            Log.e(TAG, "loadSounds: ", e)
            emptyList()
        }
    }

    private fun load(sound: Sound) {
        val openFd: AssetFileDescriptor = assets.openFd(sound.assetPath)
        val soundId = mSoundPool.load(openFd, 1)
        sound.soundId = soundId
    }

    fun play(sound: Sound) {
        sound.soundId?.let {
            mSoundPool.play(it, 1.0f, 1.0f, 1, 0, mRate)
        }
    }


    fun release() {
        mSoundPool.release()
    }

}


