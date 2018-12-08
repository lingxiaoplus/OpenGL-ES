package com.opengles.example.opengl;

public class ContentValue {
    public static final int TYPE_ORIGIN = 0;
    public static final int TYPE_GRAY = 1;
    public static final int TYPE_COOL = 2;
    public static final int TYPE_WARM = 3;
    public static final int TYPE_BLUR = 4;
    public static final int TYPE_MAGN = 5;
    public enum Filter{
        ORIGIN(0,new float[]{0.0f,0.0f,0.0f}),
        GRAY(1,new float[]{0.299f,0.587f,0.114f}),
        COOL(2,new float[]{0.0f,0.0f,0.1f}),
        WARM(2,new float[]{0.1f,0.1f,0.0f}),
        BLUR(3,new float[]{0.006f,0.004f,0.002f}),
        MAGN(4,new float[]{0.0f,0.0f,0.4f});

        private int mType;
        private float[] mColorData;
        Filter(int type,float[] color){
            this.mType = type;
            this.mColorData = color;
        }

        public int getType() {
            return mType;
        }

        public float[] getColorData() {
            return mColorData;
        }
    }

}
