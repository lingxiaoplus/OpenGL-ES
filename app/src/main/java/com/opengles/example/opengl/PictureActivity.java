package com.opengles.example.opengl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.opengles.example.opengl.shape.PictureShape;
import com.opengles.example.opengl.widget.MGLSurfaceView;

public class PictureActivity extends BaseActivity {

    private MGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        mGLSurfaceView = findViewById(R.id.gl_SurfaceView);
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

}
