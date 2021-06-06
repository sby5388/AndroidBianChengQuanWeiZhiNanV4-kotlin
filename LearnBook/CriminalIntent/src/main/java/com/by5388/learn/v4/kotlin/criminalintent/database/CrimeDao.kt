package com.by5388.learn.v4.kotlin.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.by5388.learn.v4.kotlin.criminalintent.Crime
import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
@Dao
interface CrimeDao {

    @Query("select * from Crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("select * from Crime where id = (:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Update
    fun updateCrime(crime: Crime)

    @Insert
    fun insertCrime(crime: Crime)


}