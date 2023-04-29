package com.by5388.learn.v4.kotlin.criminalintent

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
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
    var suspect: String = "",
    var isSolved: Boolean = false,
    var hide: Boolean = false
) {
    //20210608 this property will not write into database
    @Ignore
    val photoFileName = "IMG_{$id}.jpg"

    @Ignore
    constructor() : this(UUID.randomUUID())
}
