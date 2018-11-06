package com.opengles.example.opengl.gl;

import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 任梦林 on 2018/7/12.
 *
 * 先了解绘制方式 我用的GL_TRIANGLES
 * int GL_POINTS       //将传入的顶点坐标作为单独的点绘制
 * int GL_LINES        //将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
 * int GL_LINE_STRIP   //将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
 * int GL_LINE_LOOP    //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
 * int GL_TRIANGLES    //将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
 * int GL_TRIANGLE_FAN    //将传入的顶点作为扇面绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
 * int GL_TRIANGLE_STRIP   //将传入的顶点作为三角条带绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形
 */

public class GLRenderer implements GLSurfaceView.Renderer {
    private FloatBuffer mTriangleBuffer;
    private FloatBuffer mColorBuffer;
    private static final String TAG = GLRenderer.class.getSimpleName();
    //模型数据
    private float[] mTriangleArray = {
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
    };
    //三角形各顶点颜色(三个顶点)
    private float[] mColor = new float[]{
            1, 1, 0, 1,
            0, 1, 1, 1,
            1, 0, 1, 1,

            1, 1, 0, 1,
            0, 1, 1, 1,
            1, 0, 1, 1
    };

    //正方形 abc cda 两个三角形
    private float triangleCoords[] = {
            -0.5f,  0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f,  0.5f, 0.0f,  // top right
            -0.5f,  0.5f, 0.0f // top left
    };

    public GLRenderer(){
        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        Log.d(TAG, "GLRenderer: "+triangleCoords.length);
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        //以本机字节顺序来修改此缓冲区的字节顺序
        bb.order(ByteOrder.nativeOrder());
        mTriangleBuffer = bb.asFloatBuffer();
        //将给定float[]数据从当前位置开始，依次写入此缓冲区
        mTriangleBuffer.put(triangleCoords);
        //设置此缓冲区的位置。如果标记已定义并且大于新的位置，则要丢弃该标记。
        mTriangleBuffer.position(0);

        //颜色相关
        ByteBuffer bb2 = ByteBuffer.allocateDirect(mColor.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        mColorBuffer = bb2.asFloatBuffer();
        mColorBuffer.put(mColor);
        mColorBuffer.position(0);

    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //gl.glClearColor(1f,0f,0f,0f); // 红色 rgba -> 取值范围 [0~1]
        gl.glClearColor(1,1,1,1); // 白色 铺满
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        gl.glViewport(0,0,width,height);
        //设置投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        // 设置视口的大小
        gl.glFrustumf(-ratio,ratio,-1,1,1,10);
        //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //绘制图形
        //gl.glClear(GL10.GL_COLOR_BUFFER_BIT); //使用glClearColor函数所设置的颜色进行清屏
        //清屏和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // 重置当前的模型观察矩阵
        gl.glLoadIdentity();
        // 允许设置顶点
        //GL10.GL_VERTEX_ARRAY顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //允许设置颜色
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        //将三角形在z轴上移动
        gl.glTranslatef(0f,0f,-2.0f);
        //设置三角形  第一个参数 每个顶点有几个数值描述 第二个参数 每个顶点的坐标类型 第三个参数 数组中每个顶点的间隔，0表示数组是连续的
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,mTriangleBuffer);
        //设置三角形颜色
        gl.glColorPointer(4, GL10.GL_FLOAT,0,mColorBuffer);
        //绘制三角形 第一个参数表示:每三个顶之间绘制三角形，之间不连接,
        // 第二个参数 从数组缓存中的哪一位开始绘制
        // 第三个参数 顶点的数量
        gl.glDrawArrays(GL10.GL_TRIANGLES,0,triangleCoords.length);

        // 取消颜色设置
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        // 取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        //绘制结束
        gl.glFinish();
    }

    private float[]  createPositions(int n, float radius){
        List<Float> data = new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);
        data.add(0.0f);
        float angDegSpan = 360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            data.add((float) (radius*Math.sin(i*Math.PI/180f)));
            data.add((float)(radius*Math.cos(i*Math.PI/180f)));
            data.add(0.0f);
        }
        float[] f=new float[data.size()];
        for (int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }

}
