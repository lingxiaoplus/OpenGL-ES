package com.opengles.example.opengl.shape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

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

public class TriangleShape extends Shape {
    private FloatBuffer mTriangleBuffer;
    private FloatBuffer mColorBuffer;
    private static final String TAG = TriangleShape.class.getSimpleName();
    //模型数据
    private float[] triangleCoords = {
            0f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f
    };
    //三角形各顶点颜色(三个顶点)
    private float[] mColor = new float[]{
            1, 1, 0, 1,
            0, 1, 1, 1,
            1, 0, 1, 1
    };

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;"+
                    "varying  vec4 vColor;"+
                    "attribute vec4 aColor;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vColor=aColor;"+
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private int mProgram;
    private float[] mViewMatrix=new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix=new float[16];

    public TriangleShape(View view){
        super(view);
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

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        //创建一个空的opgles程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram,vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram,fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram,GLES20.GL_LINK_STATUS,linkStatus,0);
        if (linkStatus[0] != GLES20.GL_TRUE){
            Log.e(TAG, "CubeShape 连接失败");
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }

    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 4, 8);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f,
                0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        int matrixHandler = GLES20.glGetUniformLocation(mProgram,"vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(matrixHandler,1,false, mMVPMatrix,0);
        //获取顶点着色器的vPosition成员句柄
        int positionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(positionHandler);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionHandler, 3,
                GLES20.GL_FLOAT, false,
                0, mTriangleBuffer);
        //获取片元着色器的vColor成员的句柄
        int colorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
        //GLES20.glUniform4fv(colorHandle, 1, mColor, 0);
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle,4,
                GLES20.GL_FLOAT,false,
                0,mColorBuffer);

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionHandler);
    }

}
