package com.opengles.example.opengl.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.opengles.example.opengl.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author 任梦林
 * 圆锥  底部画一个圆  上面的分解成三个...四个...n个三角形
 */
public class ConeShape extends Shape {
    private float height = 2.0f; //圆锥高度
    private int n = 360; //切割的份数
    private float radius = 1.0f; //圆锥底部半径

    private FloatBuffer vertexBuffer;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16]; //投影矩阵
    private float[] mMVPMatrix = new float[16];
    private final int vSize;
    private int mProgram;
    public static final String TAG = ConeShape.class.getSimpleName();

    private float ovelColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    public ConeShape(View view) {
        super(view);
        List<Float> pos = new ArrayList<>();
        pos.add(0.0f);
        pos.add(0.0f);
        pos.add(height); //先添加一个顶点
        float angDegSpan = 360f/n;
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            pos.add((float) (radius * Math.sin(i * Math.PI / 180f)));  //x
            pos.add((float) (radius * Math.cos(i * Math.PI / 180f)));  //y
            pos.add(0.0f);  //z
        }

        pos.add(0.0f);
        pos.add(0.0f);
        pos.add(0.0f); //先添加一个顶点
        for (float i = 0; i < 360 + angDegSpan; i += angDegSpan) {
            pos.add((float) (radius * Math.sin(i * Math.PI / 180f)));  //x
            pos.add((float) (radius * Math.cos(i * Math.PI / 180f)));  //y
            pos.add(0.0f);  //z
        }


        float[] d = new float[pos.size()];    //所有的顶点
        for (int i = 0; i < d.length; i++) {
            d[i] = pos.get(i);
        }
        vSize = d.length/3;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(d.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(d);
        vertexBuffer.position(0);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化顶点、片元着色器并链接程序
        mProgram = ShaderUtil.createProgram(mView.getResources(), "vshader/Cone.sh", "fshader/Cone.sh");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        //near far一个立方体的前面和后面，near和far需要结合拍摄相机即观察者眼睛的位置来设置，
        // 例如setLookAtM中设置cx = 0, cy = 0, cz = 10，
        // near设置的范围需要是小于10才可以看得到绘制的图像，如果大于10，图像就会处于了观察这眼睛的后面，
        // 这样绘制的图像就会消失在镜头前，far参数，far参数影响的是立体图形的背面，
        // far一定比near大，一般会设置得比较大，如果设置的比较小，一
        // 旦3D图形尺寸很大，这时候由于far太小，这个投影矩阵没法容纳图形全部的背面，这样3D图形的背面会有部分隐藏掉的。
        Matrix.frustumM(mProjectMatrix, 0,
                -ratio, ratio, -1, 1, //影响上下缩放比，如果left和right已经设置好缩放，则bottom只需要设置为-1，top设置为1，这样就能保持图像不变形。也可以将left，right 与bottom，top交换比例，即bottom和top设置为 -height/width 和 height/width, left和right设置为-1和1
                3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0,
                1.0f, -10.0f, -4.0f, //摄像机位置
                0f, 0f, 0f, //图像的中心点位置为原点
                0f, 1.0f, 0.0f);  //摄像机旋转向量  这里是y轴正方向
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(mProgram);
        Log.e(TAG, "mProgram结果:" + mProgram);
        int mMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrix, 1, false, mMVPMatrix, 0);
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        Log.e(TAG, "Get Position:" + mPositionHandle);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vSize);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
