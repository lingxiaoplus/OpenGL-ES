package com.opengles.example.opengl;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.opengles.example.opengl.gl.GLRenderer;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private GLSurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检测设备是否支持OpenGL2.0 这段代码不能很好的在模拟器上工作
        /*final ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2=configurationInfo.reqGlEsVersion>=0x2000;*/
        /*if (supportEs2()){
            mGLSurfaceView = new GLSurfaceView(this);
            mGLSurfaceView.setRenderer(new GLRenderer());
            setContentView(mGLSurfaceView);
        }else {
            setContentView(R.layout.activity_main);
            Toast.makeText(this,"不支持opengles2.0",Toast.LENGTH_SHORT).show();
        }*/
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission();
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
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cubepos:
                Intent intent = new Intent(getApplicationContext(),CubeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_shape)
    public void onShapeClick(){
        startActivity(new Intent(getApplicationContext(),CubeActivity.class));
    }
    @OnClick(R.id.button_pcture)
    public void onPictureClick(){
        startActivity(new Intent(getApplicationContext(),PictureActivity.class));
    }
    @OnClick(R.id.button_camera)
    public void onCameraPreview(){
        startActivity(new Intent(getApplicationContext(),CameraActivity.class));
    }

    //获取摄像头权限
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        }
    }
}
