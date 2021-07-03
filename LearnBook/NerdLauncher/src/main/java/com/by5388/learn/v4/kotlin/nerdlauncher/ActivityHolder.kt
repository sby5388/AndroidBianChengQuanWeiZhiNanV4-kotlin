package com.by5388.learn.v4.kotlin.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private val mNameTextView = itemView.findViewById<TextView>(R.id.textview_app_name)
    private val mIconImageView = itemView.findViewById<ImageView>(R.id.image_app_icon)


    init {
        itemView.setOnClickListener(this)
    }

    private lateinit var mResolveInfo: ResolveInfo

    override fun onClick(v: View) {
        val activityInfo = mResolveInfo.activityInfo
        val intent = Intent(Intent.ACTION_MAIN).apply {
            setClassName(
                activityInfo.applicationInfo.packageName,
                activityInfo.name
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        v.context.startActivity(intent)

    }

    fun bindActivity(resolveInfo: ResolveInfo) {
        this.mResolveInfo = resolveInfo
        val packageManager = itemView.context.packageManager
        val appName = resolveInfo.loadLabel(packageManager).toString()
        mNameTextView.text = appName

        val drawable = resolveInfo.loadIcon(packageManager)
        mIconImageView.setImageDrawable(drawable)
    }
}