package com.by5388.learn.v4.kotlin.photogallery

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_SEARCH_QUERY = "searchQuery"

/**
 * 保存最近一次搜索的关键字
 * object:单例模式
 */
object QueryPreferences {
    fun getStoredQuery(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SEARCH_QUERY, "")!!
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            //TODO 26.5 use android KTX
            // 扩展函数edit :会自动调用apply();使用 edit(true){}则会调用 commit()
            .edit {
                putString(PREF_SEARCH_QUERY, query)
            }
    }
}