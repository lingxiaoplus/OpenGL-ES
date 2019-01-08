package com.opengles.example.opengl.shape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.opengles.example.opengl.ContentValue;
import com.opengles.example.opengl.R;
import com.opengles.example.opengl.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PictureShape extends Shape {
    private final float[] sPos = {
            -1.0f, 1.0f,    //左上角
            -1.0f, -1.0f,   //左下角
            1.0f, 1.0f,     //右上角
            1.0f, -1.0f     //右下角
    };
    private final float[] sCoord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };
    private FloatBuffer mSposBuffer, mScoordBuffer;
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16]; //投影矩阵
    private float[] mMVPMatrix = new float[16];
    private Bitmap mBitmap;
    private int mProgram;
    private static final String TAG = PictureShape.class.getSimpleName();

    public PictureShape(View view) {
        super(view);

        mBitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.gl_picture);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(sPos.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mSposBuffer = byteBuffer.asFloatBuffer();
        mSposBuffer.put(sPos);
        mSposBuffer.position(0);

        ByteBuffer sCoordBuffer = ByteBuffer.allocateDirect(sCoord.length * 4);
        sCoordBuffer.order(ByteOrder.nativeOrder());
        mScoordBuffer = sCoordBuffer.asFloatBuffer();
        mScoordBuffer.put(sCoord);
        mScoordBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgram = ShaderUtil.createProgram(mView.getResources(), "vshader/picture.sh", "fshader/picture.sh");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }


    private int mPictureType;
    @Override
    public void onSetPicType(int picType) {
        super.onSetPicType(picType);
        this.mPictureType = picType;
        Log.e(TAG, "图片类型: "+mPictureType);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        int glHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, mMVPMatrix, 0);
        int glHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(glHPosition);

        int glHCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        int glHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        GLES20.glUniform1i(glHTexture, 0);

        int changeType = GLES20.glGetUniformLocation(mProgram,"vChangeType");
        int changeColor = GLES20.glGetUniformLocation(mProgram,"vChangeColor");

        //int hIsHalf = GLES20.glGetUniformLocation(mProgram,"vIsHalf");
        //GLES20.glUniform1i(hIsHalf,1);
        switch (mPictureType){
            case ContentValue.TYPE_ORIGIN:
                GLES20.glUniform1i(changeType,ContentValue.Filter.ORIGIN.getType());
                GLES20.glUniform3fv(changeColor,0,ContentValue.Filter.ORIGIN.getColorData(),1);
                break;
            case ContentValue.TYPE_GRAY:
                GLES20.glUniform1i(changeType,ContentValue.Filter.GRAY.getType());
                GLES20.glUniform3fv(changeColor,0,ContentValue.Filter.GRAY.getColorData(),1);
                break;
            case ContentValue.TYPE_COOL:
                GLES20.glUniform1i(changeType,ContentValue.Filter.COOL.getType());
                GLES20.glUniform3fv(changeColor,0,ContentValue.Filter.COOL.getColorData(),1);
                break;
            case ContentValue.TYPE_WARM:
                GLES20.glUniform1i(changeType,ContentValue.Filter.WARM.getType());
                GLES20.glUniform3fv(changeColor,0,ContentValue.Filter.WARM.getColorData(),1);
                break;
            case ContentValue.TYPE_BLUR:
                GLES20.glUniform1i(changeType,ContentValue.Filter.BLUR.getType());
                GLES20.glUniform3fv(changeColor,0,ContentValue.Filter.BLUR.getColorData(),1);
                break;
            case ContentValue.TYPE_MAGN:
                GLES20.glUniform1i(changeType,ContentValue.Filter.MAGN.getType());
                GLES20.glUniform3fv(changeColor,0,ContentValue.Filter.MAGN.getColorData(),1);
                break;
            default:
                break;
        }
        int textureId = createTexture();
        Log.d(TAG, "生成纹理返回的纹理id: "+textureId);
        //传入顶点坐标
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, mSposBuffer);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, mScoordBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(glHPosition);
        GLES20.glDisableVertexAttribArray(glHCoordinate);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

}
