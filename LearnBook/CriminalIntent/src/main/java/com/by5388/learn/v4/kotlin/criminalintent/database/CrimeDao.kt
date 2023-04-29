package com.by5388.learn.v4.kotlin.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.by5388.learn.v4.kotlin.criminalintent.Crime
import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
@Dao
interface CrimeDao {

    /**
     * 只显示没有被隐藏的
     */
    @Query("select * from Crime where hide = 0 ")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("select * from Crime where id = (:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Update
    fun updateCrime(crime: Crime)

    @Insert
    fun insertCrime(crime: Crime)


    @Query("select * from Crime where id = (:id)")
    fun queryCrime(id: UUID): Crime?

    @Delete
    fun deleteCrime(crime: Crime)

}