package com.opengles.example.stlopengl.model;

import android.content.Context;

import com.opengles.example.stlopengl.utils.BufferUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 任梦林 on 2018/7/19.
 */

public class StlReader {
    private StlLoadListener stlLoadListener;
    public StlModel parseBinStlFromSdCard(String path) throws IOException {
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        return parseBinStl(inputStream);
    }

    public StlModel parserBinStlInAssets(Context context, String fileName)
            throws IOException {
        InputStream is = context.getAssets().open(fileName);
        return parseBinStl(is);
    }

    private StlModel parseBinStl(InputStream in) throws  IOException{
        if (stlLoadListener != null)
            stlLoadListener.onStart();
        StlModel model = new StlModel();
        //跳过前面的80个字节，是文件名称
        in.skip(80);
        //紧接着用 4 个字节的整数来描述模型的三角面片个数
        byte[] bytes = new byte[4];
        in.read(bytes); //读取三角面片个数
        int faceCount = BufferUtil.byte4ToInt(bytes,0);
        model.setFaceCount(faceCount);
        if (faceCount == 0){
            in.close();
            return model;
        }
        //每个三角面片占用固定的50个字节
        byte[] faceBytes = new byte[50 * faceCount];
        in.read(faceBytes);
        in.close();
        parseModel(model,faceBytes);
        if (stlLoadListener != null){
            stlLoadListener.onFinished();
        }
        return model;
    }

    /**
     * 解析模型数据 包括顶点数据 法向量数据 所占空间范围等
     * @param model
     * @param faceBytes
     */
    private void parseModel(StlModel model, byte[] faceBytes) {
        /**
         * 每个三角面片占用固定的50个字节,50字节当中：
         *  三角片的法向量：（1个向量相当于一个点）*（3维/点）*（4字节浮点数/维）=12字节
         *  三角片的三个点坐标：（3个点）*（3维/点）*（4字节浮点数/维）=36字节
         *  最后2个字节用来描述三角面片的属性信息
         */
        int faceCount = model.getFaceCount();
        // 保存所有顶点坐标信息,一个三角形3个顶点，一个顶点3个坐标轴
        float[] verts = new float[faceCount*3*3];
        // 保存所有三角面对应的法向量位置，
        // 一个三角面对应一个法向量，一个法向量有3个点
        // 而绘制模型时，是针对需要每个顶点对应的法向量，因此存储长度需要*3
        // 又同一个三角面的三个顶点的法向量是相同的，
        // 因此后面写入法向量数据的时候，只需连续写入3个相同的法向量即可
        float[] vnorms = new float[faceCount*3*3];
        //保存所有三角面的属性信息
        short[] remarks = new short[faceCount];

        int stlOffset = 0;
        try {
            for (int i = 0; i < faceCount; i++) {
                if (stlLoadListener != null) {
                    stlLoadListener.onLoading(i, faceCount);
                }
                for (int j = 0; j < 4; j++) {
                    float x = BufferUtil.byte4ToFloat(faceBytes, stlOffset);
                    float y = BufferUtil.byte4ToFloat(faceBytes, stlOffset + 4);
                    float z = BufferUtil.byte4ToFloat(faceBytes, stlOffset + 8);
                    stlOffset += 12;

                    if (j == 0) {//法向量
                        vnorms[i * 9] = x;
                        vnorms[i * 9 + 1] = y;
                        vnorms[i * 9 + 2] = z;
                        vnorms[i * 9 + 3] = x;
                        vnorms[i * 9 + 4] = y;
                        vnorms[i * 9 + 5] = z;
                        vnorms[i * 9 + 6] = x;
                        vnorms[i * 9 + 7] = y;
                        vnorms[i * 9 + 8] = z;
                    } else {//三个顶点
                        verts[i * 9 + (j - 1) * 3] = x;
                        verts[i * 9 + (j - 1) * 3 + 1] = y;
                        verts[i * 9 + (j - 1) * 3 + 2] = z;

                        //记录模型中三个坐标轴方向的最大最小值
                        if (i == 0 && j == 1) {
                            model.minX = model.maxX = x;
                            model.minY = model.maxY = y;
                            model.minZ = model.maxZ = z;
                        } else {
                            model.minX = Math.min(model.minX, x);
                            model.minY = Math.min(model.minY, y);
                            model.minZ = Math.min(model.minZ, z);
                            model.maxX = Math.max(model.maxX, x);
                            model.maxY = Math.max(model.maxY, y);
                            model.maxZ = Math.max(model.maxZ, z);
                        }
                    }
                }
                short r = BufferUtil.byte2ToShort(faceBytes, stlOffset);
                stlOffset = stlOffset + 2;
                remarks[i] = r;
            }
        } catch (Exception e) {
            if (stlLoadListener != null) {
                stlLoadListener.onFailure(e);
            } else {
                e.printStackTrace();
            }
        }
        //将读取的数据设置到Model对象中
        model.setVerts(verts);
        model.setVnorms(vnorms);
        model.setRemarks(remarks);

    }

    public interface StlLoadListener{
        void onStart();
        void onLoading(int cur, int total);
        void onFinished();
        void onFailure(Exception e);
    }
}
