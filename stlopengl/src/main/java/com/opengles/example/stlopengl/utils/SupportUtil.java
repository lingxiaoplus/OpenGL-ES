package com.opengles.example.stlopengl.utils;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.os.Build;

/**
 * Created by 任梦林 on 2018/7/20.
 */

public class SupportUtil {
    public static boolean supportEs2(ActivityManager activityManager){
        //ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;
        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));
        supportsEs2 = supportsEs2 || isEmulator;
        return supportsEs2;
    }
}
