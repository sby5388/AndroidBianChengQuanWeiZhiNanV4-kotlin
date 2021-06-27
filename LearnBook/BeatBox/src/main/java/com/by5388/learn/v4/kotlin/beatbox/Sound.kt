package com.by5388.learn.v4.kotlin.beatbox

private const val WAV = ".wav"

class Sound(val assetPath: String) {
    val name = assetPath.split("/")
        .last()
        .removeSuffix(WAV)

}