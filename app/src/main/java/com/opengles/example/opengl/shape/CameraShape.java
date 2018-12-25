package com.opengles.example.opengl.shape;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.opengles.example.opengl.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraShape extends Shape{
    private int mProgram;
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
    public CameraShape(View view) {
        super(view);
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
        mProgram = ShaderUtil.createProgram(mView.getResources(), "vshader/picture.sh", "fshader/camera.sh");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        float[] matrix=new float[16];
        /*Gl2Utils.getShowMatrix(matrix,this.dataWidth,this.dataHeight,this.width,this.height);
        if(cameraId==1){
            Gl2Utils.flip(matrix,true,false);
            Gl2Utils.rotate(matrix,90);
        }else{
            Gl2Utils.rotate(matrix,270);
        }
        mOesFilter.setMatrix(matrix);*/

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
    private int createTextureID(){
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }


    //通过传入图片宽高和预览宽高，计算变换矩阵，得到的变换矩阵是预览类似ImageView的centerCrop效果
    public static float[] getShowMatrix(int imgWidth,int imgHeight,int viewWidth,int viewHeight){
        float[] projection=new float[16];
        float[] camera=new float[16];
        float[] matrix=new float[16];

        float sWhView=(float)viewWidth/viewHeight;
        float sWhImg=(float)imgWidth/imgHeight;
        if(sWhImg>sWhView){
            Matrix.orthoM(projection,0,-sWhView/sWhImg,sWhView/sWhImg,-1,1,1,3);
        }else{
            Matrix.orthoM(projection,0,-1,1,-sWhImg/sWhView,sWhImg/sWhView,1,3);
        }
        Matrix.setLookAtM(camera,0,0,0,1,0,0,0,0,1,0);
        Matrix.multiplyMM(matrix,0,projection,0,camera,0);
        return matrix;
    }

    //旋转
    public static float[] rotate(float[] m,float angle){
        Matrix.rotateM(m,0,angle,0,0,1);
        return m;
    }

    //镜像
    public static float[] flip(float[] m,boolean x,boolean y){
        if(x||y){
            Matrix.scaleM(m,0,x?-1:1,y?-1:1,1);
        }
        return m;
    }

}
