package com.opengles.example.stlopengl;

import android.app.ActivityManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.opengles.example.stlopengl.utils.SupportUtil;


public class StlActivity extends AppCompatActivity {
    private GLSurfaceView mGLSurfaceView;
    private float rotateDegreen = 0;
    private GlStlRenderer glRenderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SupportUtil.supportEs2((ActivityManager) getSystemService(ACTIVITY_SERVICE))){
            mGLSurfaceView = new GLSurfaceView(this);
            glRenderer = new GlStlRenderer(this);
            mGLSurfaceView.setRenderer(glRenderer);
            setContentView(mGLSurfaceView);
        }else {
            setContentView(R.layout.activity_main);
            Toast.makeText(this,"不支持opengles2.0",Toast.LENGTH_SHORT).show();
        }
    }
    public void rotate(float degree) {
        glRenderer.rotate(degree);
        mGLSurfaceView.invalidate();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            rotate(rotateDegreen);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mGLSurfaceView != null){
            mGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGLSurfaceView != null){
            mGLSurfaceView.onResume();
            //不断改变rotateDegreen值，实现旋转
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            sleep(100);
                            rotateDegreen += 5;
                            handler.sendEmptyMessage(0x001);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0x001);
    }
}
