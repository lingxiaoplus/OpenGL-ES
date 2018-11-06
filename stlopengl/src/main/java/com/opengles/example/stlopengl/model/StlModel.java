package com.opengles.example.stlopengl.model;



import com.opengles.example.stlopengl.utils.BufferUtil;

import java.nio.FloatBuffer;

/**
 * Created by 任梦林 on 2018/7/19.
 */

public class StlModel {
    //三角面个数
    private int faceCount;
    //顶点坐标数组
    private float[] verts;
    //每个顶点坐标对应的法向量数据
    private float[] vnorms;
    //每个三角面的属性信息
    private short[] remarks;

    //顶点数组转换而来的buffer
    private FloatBuffer vertsBuffer;
    //对应的法向量
    private FloatBuffer vnormsBuffer;
    //以下分别保存所有点在x,y,z方向上的最大值、最小值
    public float maxX;
    public float minX;
    public float maxY;
    public float minY;
    public float maxZ;
    public float minZ;

    /**
     * 返回模型的中心点
     * @return
     */
    public Point getCenterPoint(){
        float cx = minX + (maxX - minX)/2;
        float cy = minY + (maxY - minY)/2;
        float cz = minZ + (maxZ - minZ)/2;
        return new Point(cx,cy,cz);
    }

    /**
     * 获取模型的最大半径
     * @return
     */
    public float getR(){
        float dx = maxX - minX;
        float dy = maxY - minY;
        float dz = maxZ - minZ;
        float max = dx;
        if (dy > max){
            max = dy;
        }
        if (dz > max){
            max = dz;
        }
        return max;
    }

    public void setVerts(float[] verts){
        this.verts = verts;
        vertsBuffer = BufferUtil.floatToBuffer(verts);
    }

    public void setVnorms(float[] vnorms){
        this.vnorms = vnorms;
        vnormsBuffer = BufferUtil.floatToBuffer(vnorms);
    }

    public void setRemarks(short[] remarks){
        this.remarks = remarks;
    }
    public void setFaceCount(int count){
        this.faceCount = count;
    }
    public int getFaceCount(){
        return faceCount;
    }

    public float[] getVerts() {
        return verts;
    }

    public float[] getVnorms() {
        return vnorms;
    }

    public short[] getRemarks() {
        return remarks;
    }

    public FloatBuffer getVertsBuffer() {
        return vertsBuffer;
    }

    public FloatBuffer getVnormsBuffer() {
        return vnormsBuffer;
    }
}
