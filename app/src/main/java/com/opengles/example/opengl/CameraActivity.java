package com.opengles.example.opengl;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.opengles.example.opengl.shape.CameraShape;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraActivity extends BaseActivity {

    @BindView(R.id.shape_camera)
    CameraShape cameraShape;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        setActBarTitle("相机");
    }

    @Override
    protected boolean setShowActionBarBack() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraShape != null){
            cameraShape.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraShape != null){
            cameraShape.onResume();
        }
    }
}
