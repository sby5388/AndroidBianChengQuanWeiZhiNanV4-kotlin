package com.by5388.learn.v4.kotlin.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.by5388.learn.v4.kotlin.criminalintent.database.CrimeDatabase
import java.util.*

/**
 * Repository
 * Singleton
 * @author  admin  on 2021/6/5.
 */
private const val DATABASE_NAME = "crime.db"

class CrimeRepository private constructor(context: Context) {
    private val mDataBase: CrimeDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        ).build()

    private val mCrimeDao = mDataBase.crimeDao()

    fun getCrimes(): LiveData<List<Crime>> = mCrimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = mCrimeDao.getCrime(id)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialzed")
        }

    }

}