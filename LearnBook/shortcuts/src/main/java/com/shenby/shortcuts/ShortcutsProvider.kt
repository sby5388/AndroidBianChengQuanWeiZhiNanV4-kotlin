package com.shenby.shortcuts

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings

class ShortcutsProvider : BaseProvider() {


    override fun onCreate(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            return true;
        }
        val manager = context!!.getSystemService(ShortcutManager::class.java)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context!!.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shortcutInfo = ShortcutInfo.Builder(context, "setting")
            .setShortLabel("设置")
            .setLongLabel("打开设置")
            .setIcon(Icon.createWithResource(context, R.drawable.baseline_settings_24))
            .setIntent(intent)
            .build()

        val dynamicShortcuts = manager.dynamicShortcuts.apply {
            add(shortcutInfo)
        }
        manager.dynamicShortcuts = dynamicShortcuts
        return true
    }
}