package com.by5388.learn.v4.kotlin.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.by5388.learn.v4.kotlin.criminalintent.database.CrimeDatabase
import com.by5388.learn.v4.kotlin.criminalintent.database.migration_1_2
import com.by5388.learn.v4.kotlin.criminalintent.database.migration_2_3
import java.io.File
import java.util.*
import java.util.concurrent.Executors

/**
 * Repository
 * Singleton
 * @author  admin  on 2021/6/5.
 */
private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {
    private val mDataBase: CrimeDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        )
            //add database version upgrade
            .addMigrations(migration_1_2)
            .addMigrations(migration_2_3)
            .build()

    private val mCrimeDao = mDataBase.crimeDao()

    /**
     * ThreadPool ,single thread execute Runnable
     */
    private val mExecutor = Executors.newSingleThreadExecutor()

    private val mFileDir = context.applicationContext.filesDir

    fun getCrimes(): LiveData<List<Crime>> = mCrimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = mCrimeDao.getCrime(id)

    fun updateCrime(crime: Crime) {
        mExecutor.execute {
            mCrimeDao.updateCrime(crime)
        }
    }

    fun updateCrime(uuid: UUID, date: Date) {
        mExecutor.execute {
            val crime = mCrimeDao.queryCrime(uuid)
            crime?.let {
                it.date = date
                mCrimeDao.updateCrime(crime)
            }
        }
    }


    fun addCrime(crime: Crime) {
        mExecutor.execute {
            mCrimeDao.insertCrime(crime)
        }
    }

    fun deleteCrime(crime: Crime) {
        mExecutor.execute {
            mCrimeDao.deleteCrime(crime)
        }

    }


    fun getPhotoFile(crime: Crime): File = File(mFileDir, crime.photoFileName)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }

    }

}