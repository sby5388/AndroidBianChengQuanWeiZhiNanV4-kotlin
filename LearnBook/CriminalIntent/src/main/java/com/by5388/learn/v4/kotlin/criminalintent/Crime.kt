package com.by5388.learn.v4.kotlin.criminalintent

import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
data class Crime(
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
)
