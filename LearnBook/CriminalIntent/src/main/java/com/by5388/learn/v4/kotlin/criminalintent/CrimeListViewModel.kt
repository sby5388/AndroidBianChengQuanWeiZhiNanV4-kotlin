package com.by5388.learn.v4.kotlin.criminalintent

import androidx.lifecycle.ViewModel

/**
 * @author  admin  on 2021/6/5.
 */
class CrimeListViewModel : ViewModel() {
    val mCrimes = mutableListOf<Crime>()

    init {
        for (i in 0..100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i % 2 == 0
            //todo kotlin list fun api
            mCrimes += crime
        }
    }

}