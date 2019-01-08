package com.opengles.example.opengl.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.opengles.example.opengl.ContentValue;
import com.opengles.example.opengl.gl.MGLRender;
import com.opengles.example.opengl.shape.Shape;

public class MGLSurfaceView extends GLSurfaceView {
    private MGLRender renderer;
    public MGLSurfaceView(Context context) {
        this(context,null);
    }

    public MGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //The GL_VERTEX_ARRAY client state is not even part of GLES 2.0,
        // it is only present in the fixed function pipeline of GLES 1.x.
        // If you are using an ES 2.0 context,
        // this error does not make any sense at all   指定上下文
        setEGLContextClientVersion(2);
        setRenderer(renderer = new MGLRender(this));
        //设置渲染模式 默认渲染方式为RENDERMODE_CONTINUOUSLY不停地渲染场景  RENDERMODE_WHEN_DIRTY只有在创建和调用requestRender()时才会刷新
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setShape(Class<? extends Shape> clazz){
        try {
            renderer.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setPicType(int picType){
        renderer.setPicType(picType);
    }

}
