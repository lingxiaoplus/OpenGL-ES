package com.opengles.example.opengl.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.InputStream;

public class ShaderUtil {
    private static final String TAG = ShaderUtil.class.getSimpleName();

    private ShaderUtil() {

    }

    public static void checkGLError(String op) {
        Log.e(TAG, op);
    }

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader:" + shaderType);
                Log.e(TAG, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int loadShader(Resources res, int shaderType, String resName) {
        return loadShader(shaderType, loadFromAssetsFile(resName, res));
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0)
            return 0;
        int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0)
            return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            checkGLError("Attach Vertex Shader");
            GLES20.glAttachShader(program, fragment);
            checkGLError("Attach Fragment Shader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 从脚本文件中创建顶点着色器和片元着色器
     * @param res
     * @param vertexRes 顶点着色器脚本位置
     * @param fragmentRes 片元着色器脚本位置
     * @return 如果是0 有错
     */
    public static int createProgram(Resources res, String vertexRes, String fragmentRes) {
        return createProgram(loadFromAssetsFile(vertexRes, res), loadFromAssetsFile(fragmentRes, res));
    }

    public static String loadFromAssetsFile(String fname, Resources res) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = res.getAssets().open(fname);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }
}
