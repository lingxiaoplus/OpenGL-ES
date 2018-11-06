package com.opengles.example.opengl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.opengles.example.opengl.shape.ConeShape;
import com.opengles.example.opengl.shape.CubeShape;
import com.opengles.example.opengl.shape.TriangleShape;
import com.opengles.example.opengl.widget.MGLSurfaceView;

public class CubeActivity extends BaseActivity {
    private MGLSurfaceView mGLSurfaceView;
    private Button mChangeShapeBtn;
    private FrameLayout mFrameLayout;
    private String[] mShapes = {"三角形","正方体","圆锥","球体","带光源的球体"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cube);
        mFrameLayout = findViewById(R.id.frame_surfaceview);
        mChangeShapeBtn = findViewById(R.id.bt_change);
        mGLSurfaceView = new MGLSurfaceView(this);
        mFrameLayout.addView(mGLSurfaceView);
        mChangeShapeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDia();
            }
        });
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

    private void showSelectDia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(mShapes, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    mGLSurfaceView.setShape(TriangleShape.class);
                }else if (which == 1){
                    mGLSurfaceView.setShape(CubeShape.class);
                }else if (which == 2){
                    mGLSurfaceView.setShape(ConeShape.class);
                }
                mFrameLayout.removeAllViews();
                mFrameLayout.addView(mGLSurfaceView);
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
