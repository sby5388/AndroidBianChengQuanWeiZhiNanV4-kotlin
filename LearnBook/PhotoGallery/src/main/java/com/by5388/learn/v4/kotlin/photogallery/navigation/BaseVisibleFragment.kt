package com.by5388.learn.v4.kotlin.photogallery.navigation

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.by5388.learn.v4.kotlin.photogallery.PollWorker

open class BaseVisibleFragment : Fragment() {

    private val mFilter: IntentFilter = IntentFilter(PollWorker.ACTION_SHOW_SHOW_NOTIFICATION)

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                Toast.makeText(
                    it,
                    "Got a broadcast: ${intent?.action}",
                    Toast.LENGTH_LONG
                ).show()
            }
            //有序广播
            //修改Intent中的数据。同时也可以修改 resultData（setResult(Int, String?, Bundle?)）
            //  或者 setResultExtras
            //默认情况下，发送成功 resultCode 会设置为 Activity.RESULT_OK,然后继续发送给下一个
            //resultCode 设置为Activity.RESULT_CANCELED 之后，后面的接收者则无法接收到该广播
            resultCode = Activity.RESULT_CANCELED

        }
    }

    private val mLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            requireActivity().registerReceiver(
                mReceiver,
                mFilter,
                PollWorker.PERMISSION_PRIVATE,
                null
            )
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            requireActivity().unregisterReceiver(mReceiver)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(mLifecycleObserver)

    }
}