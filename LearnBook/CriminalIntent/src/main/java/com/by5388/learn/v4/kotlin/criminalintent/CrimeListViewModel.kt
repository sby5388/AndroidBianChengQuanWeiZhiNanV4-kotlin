package com.by5388.learn.v4.kotlin.criminalintent

import androidx.lifecycle.ViewModel

/**
 * @author  admin  on 2021/6/5.
 */
class CrimeListViewModel : ViewModel() {
    private val mCrimeRepository = CrimeRepository.get()
    val mCrimesListLiveData = mCrimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        mCrimeRepository.addCrime(crime)
    }

    fun loadData() {
    }
}