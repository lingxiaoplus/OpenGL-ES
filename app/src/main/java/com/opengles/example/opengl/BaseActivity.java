package com.opengles.example.opengl;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!supportEs2()){
            Toast.makeText(this,"不支持es2.0",Toast.LENGTH_SHORT).show();
        }
        if (setShowActionBarBack()){
            mActionBar = getSupportActionBar();
            if (mActionBar != null){
                mActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }


    public boolean supportEs2(){
        ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
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

    protected boolean setShowActionBarBack(){
        return false;
    }

    public void setActBarTitle(String title){
        if (mActionBar != null){
            mActionBar.setTitle(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (setShowActionBarBack()){
            switch (item.getItemId()){
                case android.R.id.home:
                    finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
