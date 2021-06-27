package com.by5388.learn.v4.kotlin.beatbox

import android.content.res.AssetManager
import android.util.Log

private const val TAG = "BeatBox"
private const val SOUNDS_FOLDER = "sample_sounds"

class BeatBox(private val assets: AssetManager) {

    val sounds: List<Sound>

    init {
        sounds = loadSounds();
    }


    private fun loadSounds(): List<Sound> {
        return try {
            val sounds = mutableListOf<Sound>()
            val soundNames = assets.list(SOUNDS_FOLDER)!!
            soundNames.forEach { fileName ->
                val assetPath = "$SOUNDS_FOLDER/$fileName"
                val sound = Sound(assetPath)
                sounds.add(sound)
            }
            sounds
        } catch (e: Exception) {
            Log.e(TAG, "loadSounds: ", e)
            emptyList()
        }
    }

}


