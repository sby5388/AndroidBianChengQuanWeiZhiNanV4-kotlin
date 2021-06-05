package com.by5388.learn.v4.kotlin.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.by5388.learn.v4.kotlin.criminalintent.Crime

/**
 * @author  admin  on 2021/6/5.
 * exportSchema=false:禁用导出功能
 */
@Database(entities = [Crime::class], version = 1, exportSchema = false)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao(): CrimeDao
}