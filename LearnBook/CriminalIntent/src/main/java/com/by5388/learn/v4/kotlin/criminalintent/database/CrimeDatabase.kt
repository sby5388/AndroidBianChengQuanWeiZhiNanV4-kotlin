package com.by5388.learn.v4.kotlin.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.by5388.learn.v4.kotlin.criminalintent.Crime

/**
 * @author  admin  on 2021/6/5.
 * exportSchema=false:禁用导出功能
 */
@Database(entities = [Crime::class], version = 2)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao(): CrimeDao
}

//upgrade databaseVersion: 1->2
//对表结构进行修改:crime，
// 增加一列，
// 名字叫 “suspect”(嫌疑人)
//类型:text
//不为空，默认为 ''
val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "alter table crime add column suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}