package com.opengles.example.opengl;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.opengles.example.opengl.shape.PictureShape;
import com.opengles.example.opengl.widget.MGLSurfaceView;

public class PictureActivity extends BaseActivity {

    private MGLSurfaceView mGLSurfaceView;
    private FrameLayout mFrameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        mFrameLayout = findViewById(R.id.frame_surfaceview);
        mGLSurfaceView = new MGLSurfaceView(this);
        mFrameLayout.addView(mGLSurfaceView);
        mGLSurfaceView.setShape(PictureShape.class);
    }

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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.origin:
                mGLSurfaceView.setPicType(ContentValue.TYPE_ORIGIN);
                break;
            case R.id.gray:
                mGLSurfaceView.setPicType(ContentValue.TYPE_GRAY);
                break;
            case R.id.cold:
                mGLSurfaceView.setPicType(ContentValue.TYPE_COOL);
                break;
            case R.id.hot:
                mGLSurfaceView.setPicType(ContentValue.TYPE_WARM);
                break;
            case R.id.blur:
                mGLSurfaceView.setPicType(ContentValue.TYPE_BLUR);
                break;
            case R.id.magn:
                mGLSurfaceView.setPicType(ContentValue.TYPE_MAGN);
                break;
            default:
                break;
        }
        mGLSurfaceView.requestRender();
        return true;
    }
}
