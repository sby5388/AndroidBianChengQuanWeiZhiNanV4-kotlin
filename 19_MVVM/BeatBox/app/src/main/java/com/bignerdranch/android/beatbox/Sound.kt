package com.bignerdranch.android.beatbox

private const val WAV = ".wav"

class Sound(val assetPath: String) {

    val name = assetPath.split("/").last().removeSuffix(WAV)
}