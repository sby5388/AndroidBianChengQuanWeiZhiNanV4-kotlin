package com.by5388.learn.v4.kotlin.photogallery

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

/**
 * 保存最近一次搜索的关键字
 * object:单例模式
 */
object QueryPreferences {
    private const val PREF_ADAPTER_TYPE = "adapter_type"
    private const val PREF_SEARCH_QUERY = "searchQuery"
    private const val PREF_LAST_RESULT_ID = "lastResultId"
    private const val PREF_IS_POLLING = "isPolling"
    private const val PREF_IS_CHROME_TAB = "useChromeCustomTab"
    private const val PREF_SEARCH_TYPE = "search_type"

    private fun Context.preferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }


    fun getStoredQuery(context: Context): String {
        return context.preferences().getString(PREF_SEARCH_QUERY, "")!!
    }

    fun setStoredQuery(context: Context, query: String) {
        context.preferences()
            // 26.5 use android KTX
            // 扩展函数edit :会自动调用apply();使用 edit(true){}则会调用 commit()
            .edit {
                putString(PREF_SEARCH_QUERY, query)
            }
    }

    fun getLastResultId(context: Context): String {
        return context.preferences().getString(PREF_LAST_RESULT_ID, "")!!
    }

    fun setLastResultId(context: Context, lastResultId: String) {
        context.preferences().edit {
            putString(PREF_LAST_RESULT_ID, lastResultId)
        }
    }

    fun isPolling(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(PREF_IS_POLLING, false)
    }

    fun setPolling(context: Context, isOn: Boolean) {
        context.preferences().edit {
            putBoolean(PREF_IS_POLLING, isOn)
        }
    }

    fun isUseChromeCustomTab(context: Context): Boolean {
        return context.preferences().getBoolean(PREF_IS_CHROME_TAB, false)
    }

    fun setUseChromeCustomTab(context: Context, isOn: Boolean) {
        context.preferences().edit {
            putBoolean(PREF_IS_CHROME_TAB, isOn)
        }
    }


    fun getAdapterType(context: Context, defaultType: Int): Int {
        return context.preferences().getInt(PREF_ADAPTER_TYPE, defaultType)
    }

    fun setAdapterType(context: Context, type: Int) {
        context.preferences().edit {
            putInt(PREF_ADAPTER_TYPE, type)
        }
    }


    fun setSearchType(context: Context, type: Int) {
        context.preferences().edit {
            putInt(PREF_SEARCH_TYPE, type)
        }
    }

    fun getSearchType(context: Context, defaultType: Int): Int {
        return context.preferences().getInt(PREF_SEARCH_TYPE, defaultType)
    }


}