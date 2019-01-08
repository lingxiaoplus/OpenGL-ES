package com.opengles.example.opengl.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 任梦林 on 2018/4/12.
 */

public class FileUtil {
    public static void saveFile(String filename, String filepath, byte[] data) throws Exception {
        if (data != null) {
            File file = new File(filepath + filename);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
        }
    }

    /**
     * Bitmap缩小的方法
     */

    public static Bitmap small(Bitmap bitmap) {
        float scale = 0.3f;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10;  // 图片的大小设置为原来的十分之一
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }


    /**
     * 处理旋转后的图片
     * @param originpath 原图路径
     * @param context 上下文
     * @return 返回修复完毕后的图片路径
     *//*
    public static String amendRotatePhoto(String originpath, Context context) {

        // 取得图片旋转角度
        int angle = readPictureDegree(originpath);

        // 把原图压缩后得到Bitmap对象
        Bitmap bmp = getCompressPhoto(originpath);;

        // 修复图片被旋转的角度
        Bitmap bitmap = rotaingImageView(angle, bmp);

        // 保存修复后的图片并返回保存后的图片路径
        return savePhotoToSD(bitmap, context);
    }*/

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingBitmap(int angle, Bitmap bitmap) {
        //bitmap = small(bitmap);   不缩放
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        /*if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }*/
        return returnBm;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean decideFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            /*try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            return true;
        }
        return false;
    }

    /**
     * 判断文件夹是否存在，如果不存在，就新建一个
     *
     * @param mDirPath
     */
    public static void decideDirExist(String mDirPath) {
        File file = new File(mDirPath);
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 将Bitmap转换成文件
     * 保存文件
     *
     * @param bm
     * @param filename
     * @throws IOException
     */
    public static File saveFile(Bitmap bm, String filename, String filepath) throws IOException {
        File file = new File(filepath + filename);
        if (file.exists()) {
            file.delete();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return file;
    }


    public static byte[] File2byte(String filePath)
    {
        byte[] buffer = null;
        try
        {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 通过分辨率压缩bitmap
     * @param bitmap
     * @param width 需要压缩的尺寸
     * @param height
     * @return
     */
    public static Bitmap compressImageByResolution(Bitmap bitmap,float width,float height){
        try {
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
                return null;
            //图片分辨率以480x800为标准
            //float hh = 800f;//这里设置高度为800f
            //float ww = 480f;//这里设置宽度为480f
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            float be = 1;//be=1表示不缩放
            if (originalWidth > originalHeight && originalWidth > width) {//如果宽度大的话根据宽度固定大小缩放
                be =  (originalWidth / width);
            } else if (originalWidth < originalHeight && originalHeight > height) {//如果高度高的话根据宽度固定大小缩放
                be =  (originalHeight / height);
            }
            if (be <= 0)
                be = 1;

            //比例压缩
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = (int) be;//设置缩放比例
            bitmapOptions.inDither = true;
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            ByteArrayInputStream input = new ByteArrayInputStream(baos.toByteArray());

            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);

            return compressImage(bitmap);//再进行质量压缩
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 1000) {  //循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        float maxSize = Math.max(width,height);
        float scale;
        double newWidth = 1920;
        double newHeight = 1080;
        if (maxSize > 1920){
            if (width > height){
                scale = width/height;
                if (scale >1.5f){
                    newWidth = 1920;
                    newHeight = 1080;
                }else if (scale >1.0f){
                    newWidth = 1440;
                    newHeight = 1080;
                }
            }else {
                scale = height/width;
                if (scale >1.5f){
                    newWidth = 1080;
                    newHeight = 1920;
                }else if (scale >1.0f){
                    newWidth = 1080;
                    newHeight = 1440;
                }
            }
        }else {
            return bgimage;
        }
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 获取照片旋转角度
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
    /**
     * 按正方形裁切图片
     */
    public static Bitmap ImageCrop(Bitmap bitmap,  Rect rect) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        //int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        /*int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;*/
        int top = rect.top;
        int left = rect.left;  //取左上角
        int bottom = rect.bottom;
        int right = rect.right;
        int scaleW = right - left;   //取最大的一边
        int scaleH = bottom - top;

        Bitmap bmp = Bitmap.createBitmap(bitmap, left, top, scaleW, scaleH, null,
                false);
        return bmp;
    }

    /**
     * 获取本地图片
     */
    private Bitmap getLocationImgFile(String path) {
        //图片长宽缩小1/2倍
        //Bitmap bitmap = BitmapFactory.decodeFile(mFilePath, getBitmapOption(2));
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public static List<String> getFileDirNames(String path){
        List<String> files = new ArrayList<>();
        //获取此目录下的所有文件名
        File dirFile = new File(path);
        String[] fileList = dirFile.list();
        if (null == fileList){
            return files;
        }
        for (int i = 0; i < fileList.length; i++) {
            //遍历文件目录
            String string = fileList[i];
            File file = new File(dirFile.getPath(),string);
            String name = file.getName();
            files.add(name);
        }
        return files;
    }
    /**
     * 计算md5值
     *
     * @param path
     * @return
     */
    public static String getMD5(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
            return bi.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 判断md5时是否相等
     * @param packagePath  文件路径
     * @param crc       正确的文件md5值
     * @return
     */
    public static boolean verifyInstallPackage(String packagePath,String crc) {
        try {
            MessageDigest sig = MessageDigest.getInstance("MD5");
            File packageFile = new File(packagePath);
            InputStream signedData = new FileInputStream(packageFile);
            byte[] buffer = new byte[4096];//每次检验的文件区大小
            long toRead = packageFile.length();
            long soFar = 0;
            boolean interrupted = false;
            while (soFar < toRead) {
                interrupted = Thread.interrupted();
                if (interrupted) break;
                int read = signedData.read(buffer);
                soFar += read;
                sig.update(buffer, 0, read);
            }
            byte[] digest = sig.digest();
            String digestStr = bytesToHexString(digest);//将得到的MD5值进行移位转换
            digestStr = digestStr.toLowerCase();
            crc = crc.toLowerCase();
            if (digestStr.equals(crc)) {//比较两个文件的MD5值，如果一样则返回true
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 将md5值位移运算
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        int i = 0;
        while (i < src.length) {
            int v;
            String hv;
            v = (src[i] >> 4) & 0x0F;
            hv = Integer.toHexString(v);
            stringBuilder.append(hv);

            v = src[i] & 0x0F;
            hv = Integer.toHexString(v);
            stringBuilder.append(hv);
            i++;
        }
        return stringBuilder.toString();
    }
}
