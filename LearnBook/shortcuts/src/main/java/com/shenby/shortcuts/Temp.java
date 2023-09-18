package com.shenby.shortcuts;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class Temp {

    private void temp(Context context) {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse("package:" + BuildConfig.APPCALITION_ID));
    }
}
