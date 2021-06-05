package com.by5388.learn.v4.kotlin.geoquiz

import androidx.annotation.StringRes

/**
 * @author  admin  on 2021/6/2.
 */
data class Question(@StringRes val textResId: Int, val answer: Boolean) {
    var check: Boolean = false
}
