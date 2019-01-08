package com.opengles.example.opengl.shape;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.opengles.example.opengl.gl.MGLRender;
import com.opengles.example.opengl.utils.CameraUtil;
import com.opengles.example.opengl.utils.Gl2Utils;
import com.opengles.example.opengl.utils.ShaderUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraShape extends GLSurfaceView implements GLSurfaceView.Renderer{
    private int mProgram;
    private final float[] sPos = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };
    private final float[] sCoord = {
            0.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  0.0f,
            1.0f, 1.0f,
    };
    private FloatBuffer mSposBuffer, mScoordBuffer;
    private SurfaceTexture surfaceTexture;
    private CameraUtil mCameraUtil;
    private Context mContext;

    private int mTextureId;
    private int textureType = 0;
    private float[] matrix = new float[16];
    public CameraShape(Context context) {
        this(context,null);
    }

    public CameraShape(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mTextureId = createTexture();
        surfaceTexture = new SurfaceTexture(mTextureId);
        Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCameraUtil = new CameraUtil(camera,Camera.CameraInfo.CAMERA_FACING_BACK);
        try {
            mCameraUtil.getCamera().setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        mCameraUtil.initCamera(1080,1080);
        mProgram = ShaderUtil.createProgram(mContext.getResources(), "vshader/camera.sh", "fshader/camera.sh");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        //通过传入图片宽高和预览宽高，计算变换矩阵，得到的变换矩阵是预览类似ImageView的centerCrop效果
        Gl2Utils.getShowMatrix(matrix,mCameraUtil.getWidth(),mCameraUtil.getHeight(),width,height);
        if(mCameraUtil.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT){
            Gl2Utils.flip(matrix,true,false);//镜像
            Gl2Utils.rotate(matrix,90);//旋转
        }else{
            Gl2Utils.rotate(matrix,270);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (surfaceTexture != null){
            surfaceTexture.updateTexImage();
        }

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        int hMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        int coordMatrix = GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");
        GLES20.glUniformMatrix4fv(hMatrix,1,false,matrix,0);
        GLES20.glUniformMatrix4fv(coordMatrix,1,false,getOriginalMatrix(),0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,mTextureId);
        int texture = GLES20.glGetUniformLocation(mProgram,"vTexture");
        GLES20.glUniform1i(texture,textureType);


        int hPosition = GLES20.glGetAttribLocation(mProgram,"vPosition");
        GLES20.glEnableVertexAttribArray(hPosition);
        GLES20.glVertexAttribPointer(hPosition,2, GLES20.GL_FLOAT, false, 0,mSposBuffer);
        int hCoord = GLES20.glGetAttribLocation(mProgram,"vCoord");
        GLES20.glEnableVertexAttribArray(hCoord);
        GLES20.glVertexAttribPointer(hCoord, 2, GLES20.GL_FLOAT, false, 0, mScoordBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(hPosition);
        GLES20.glDisableVertexAttribArray(hCoord);

    }
    public static float[] getOriginalMatrix(){
        return new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
    }

    private int createTexture() {
        int[] texture = new int[1];
        //生成纹理
        GLES20.glGenTextures(1, texture, 0);
        //生成纹理
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCameraUtil != null){
            mCameraUtil.stopPreview();
        }
    }
}
