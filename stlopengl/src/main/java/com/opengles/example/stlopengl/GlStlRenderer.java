package com.opengles.example.stlopengl;

import android.content.Context;
import android.graphics.drawable.shapes.Shape;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;


import com.opengles.example.stlopengl.model.Point;
import com.opengles.example.stlopengl.model.StlModel;
import com.opengles.example.stlopengl.model.StlReader;
import com.opengles.example.stlopengl.utils.BufferUtil;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 任梦林 on 2018/7/19.
 */

public class GlStlRenderer implements GLSurfaceView.Renderer{
    private StlModel model;
    private float mDegress = 0;
    private Point mCenterPoint;
    private Point eye = new Point(0,0,-3);
    private Point up = new Point(0,1,0);
    private Point center = new Point(0,0,0);
    private float mScalef = 1;

    private float[] ambient = {0.9f,0.9f,0.9f,1.0f}; //环境光，在黑暗环境下就是黑色的
    private float[] diffuse = {0.5f,0.5f,0.5f,1.0f}; //漫反射部分,一般是物体表面的颜色
    private float[] specular = {1.0f, 1.0f, 1.0f, 1.0f}; //镜面反射部分，一般是白色，即光照颜色
    private float[] lightPosition = {0.5f, 0.5f, 0.5f, 0.0f}; //灯光位置

    public GlStlRenderer(Context context){
        try {
            model = new StlReader().parserBinStlInAssets(context,"light.stl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rotate(float degress){
        this.mDegress = degress;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_DEPTH_TEST); //启用深度缓存
        gl.glClearDepthf(1.0f);  //设置深度缓存值
        gl.glDepthFunc(GL10.GL_LEQUAL); //设置深度缓存比较函数
        gl.glShadeModel(GL10.GL_SMOOTH); //设置阴影模式 跟据顶点的不同颜色，最终以渐变的形式填充图形
        float r = model.getR();
        mScalef = 0.5f / r; //r是半径，不是直径，因此用0.5/r可以算出放缩比例
        mCenterPoint = model.getCenterPoint();
        openLight(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0,0,width,height);
        gl.glMatrixMode(GL10.GL_PROJECTION); //设置投影矩阵
        gl.glLoadIdentity();
        //设置透视范围
        GLU.gluPerspective(gl,45.0f,(float) (width)/height,1f,100f);
        //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //清屏和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();

        //眼睛对着原点看
        GLU.gluLookAt(gl,eye.x,eye.y,eye.z,
                center.x,center.y,center.z,
                up.x,up.y,up.z);
        //为了能有立体感觉，通过改变mDegree值，让模型不断旋转
        gl.glRotatef(mDegress,0,1,0);
        //将模型放缩到View刚好装下
        gl.glScalef(mScalef,mScalef,mScalef);
        //把模型移动到原点
        gl.glTranslatef(-mCenterPoint.x,-mCenterPoint.y,-mCenterPoint.z);

        //允许给每个顶点设置法向量
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        //允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //设置法向量数据源
        gl.glNormalPointer(GL10.GL_FLOAT,0,model.getVnormsBuffer());
        // 设置三角形顶点数据源
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,model.getVertsBuffer());
        //绘制三角形
        gl.glDrawArrays(GL10.GL_TRIANGLES,0,model.getFaceCount()*3);

        // 取消顶点设置
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //取消法向量设置
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
    }

    public void openLight(GL10 gl) {
        //启用光照功能
        gl.glEnable(GL10.GL_LIGHTING);
        //开启0号灯 即白色的灯光
        gl.glEnable(GL10.GL_LIGHT0);
        //指定各种反射光的颜色
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, BufferUtil.floatToBuffer(ambient));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, BufferUtil.floatToBuffer(diffuse));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, BufferUtil.floatToBuffer(specular));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, BufferUtil.floatToBuffer(lightPosition));
    }
}
