package com.opengles.example.opengl.gl;

import android.opengl.GLES20;
import android.util.Log;
import android.view.View;

import com.opengles.example.opengl.shape.CubeShape;
import com.opengles.example.opengl.shape.Shape;
import com.opengles.example.opengl.shape.TriangleShape;

import java.lang.reflect.Constructor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MGLRender extends Shape {
    private Shape shape;
    private Class<? extends Shape> clazz = TriangleShape.class;
    private static final String TAG = MGLRender.class.getSimpleName();

    public void setShape(Class<? extends Shape> shape){
        this.clazz = shape;
    }
    public MGLRender(View view) {
        super(view);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1,1,1,1); // 白色 铺满
        Log.e(TAG,"onSurfaceCreated");
        try {
            //根据 参数类型（可变参数）， 获取具体 Constructor
            Constructor constructor = clazz.getDeclaredConstructor(View.class);
            //设置访问权限
            constructor.setAccessible(true);
            shape = (Shape) constructor.newInstance(mView);
        } catch (Exception e) {
            e.printStackTrace();
            shape = new TriangleShape(mView);
        }
        shape.onSurfaceCreated(gl,config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG,"onSurfaceChanged");
        GLES20.glViewport(0,0,width,height);
        shape.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.e(TAG,"onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        shape.onDrawFrame(gl);
    }
}
