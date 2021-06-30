package com.by5388.learn.v4.kotlin.beatbox

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SoundViewModelTest {
    private lateinit var mBeatBox: BeatBox
    private lateinit var sound: Sound
    private lateinit var mSubject: SoundViewModel

    @Before
    fun setUp() {
        mBeatBox = mock(BeatBox::class.java)
        sound = Sound("assetPath")
        mSubject = SoundViewModel(mBeatBox)
        mSubject.sound = sound
    }

    @After
    fun tearDown() {
    }

    @Test
    fun exposeSoundNameAsTitle() {
        //assertThat(mSubject.title,'is'(sound.name))
        assertThat(mSubject.title, `is`(sound.name))
    }

    @Test
    fun callsBeatBoxPlayOnButtonClicked() {
        mSubject.onButtonClick()
        verify(mBeatBox)
        mBeatBox.play(sound)
    }
}