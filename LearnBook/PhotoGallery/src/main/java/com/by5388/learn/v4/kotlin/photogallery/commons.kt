package com.by5388.learn.v4.kotlin.photogallery

import android.app.Activity
import android.content.Context
import android.content.Intent


fun <T> Context.newIntent(clazz: Class<T>): Intent where T : Activity {
    return Intent(this, clazz)
}


fun <T> newIntent2(context: Context, clazz: Class<T>): Intent where T : Activity {
    return Intent(context, clazz)
}
