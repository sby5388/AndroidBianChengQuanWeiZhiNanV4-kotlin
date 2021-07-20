package com.by5388.learn.v4.kotlin.photogallery

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class VisibleFragment : Fragment() {
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Toast.makeText(
                requireContext(),
                "Got a broadcast: ${intent.action}",
                Toast.LENGTH_LONG
            )
                .show()
            //有序广播
            //修改Intent中的数据。同时也可以修改 resultData（setResult(Int, String?, Bundle?)）
            //  或者 setResultExtras
            //默认情况下，发送成功 resultCode 会设置为 Activity.RESULT_OK,然后继续发送给下一个
            //resultCode 设置为Activity.RESULT_CANCELED 之后，后面的接收者则无法接收到该广播
            resultCode = Activity.RESULT_CANCELED
        }
    }

    private val mFilter: IntentFilter = IntentFilter(PollWorker.ACTION_SHOW_SHOW_NOTIFICATION)

    override fun onStart() {
        super.onStart()
        // 2021/7/20 注册带权限的广播接收者
        // 任何使用XML定义的IntentFilter均能以代码的方式定义。
        // 要在代码中配置IntentFilter，
        // 可以直接调 用addCategory(String)、
        // addAction(String)和addDataPath(String)等函数。
        requireActivity().registerReceiver(
            mReceiver,
            mFilter,
            PollWorker.PERMISSION_PRIVATE,
            null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(mReceiver)
    }
}