package com.by5388.learn.v4.kotlin.criminalintent

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
@Entity
data class Crime(
    @PrimaryKey
    @NonNull
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
)
