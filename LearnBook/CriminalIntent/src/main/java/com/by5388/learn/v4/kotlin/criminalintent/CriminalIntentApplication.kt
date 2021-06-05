package com.by5388.learn.v4.kotlin.criminalintent

import android.app.Application

/**
 * @author  admin  on 2021/6/5.
 */
class CriminalIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this@CriminalIntentApplication)
    }
}