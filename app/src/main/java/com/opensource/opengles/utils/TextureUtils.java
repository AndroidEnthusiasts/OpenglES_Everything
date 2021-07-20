package com.opensource.opengles.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import com.opensource.opengles.R;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class TextureUtils {
    private static final String TAG = "TextureUtils";

    private TextureUtils() {

    }

    /***
     * 加载纹理
     * @param context
     * @param resList
     * @return
     */
    public static int[] loadTexture(Context context, int[] resList){
        int count = resList.length;
        final int[] textureIds = new int[count];
        GLES30.glGenTextures(count, textureIds, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        for (int i = 0; i < count; i++) {
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resList[i], options);
            if (bitmap == null) {
                Log.e(TAG, "Resource ID " + resList[i] + " could not be decoded.");
                GLES30.glDeleteTextures(1, textureIds, 0);
            }
            // 绑定纹理到OpenGL
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[i]);

            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

            // 加载bitmap到纹理中
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

            // 生成MIP贴图
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

            // 数据如果已经被加载进OpenGL,则可以回收该bitmap
            bitmap.recycle();

            // 取消绑定纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[i]);
        }
        return textureIds;
    }

    /***
     *
     * @param bitmap
     * @return
     */
    public static int createTextureWithBitmap(Bitmap bitmap) {
        final int[] textureIds = new int[1];
        GLES30.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }
        if (bitmap == null) {
            Log.e(TAG, "Resource ID bitmap could not be decoded.");
            GLES30.glDeleteTextures(1, textureIds, 0);
            return 0;
        }
        // 绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        // 加载bitmap到纹理中
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        // 生成MIP贴图
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        // 数据如果已经被加载进OpenGL,则可以回收该bitmap
        bitmap.recycle();

        // 取消绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textureIds[0];
    }

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureIds = new int[1];
        GLES30.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            Log.e(TAG, "Resource ID " + resourceId + " could not be decoded.");
            GLES30.glDeleteTextures(1, textureIds, 0);
            return 0;
        }
        // 绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        // 加载bitmap到纹理中
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        // 生成MIP贴图
//        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        // 数据如果已经被加载进OpenGL,则可以回收该bitmap
        bitmap.recycle();

        // 取消绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textureIds[0];
    }


    /**
     * 立方体贴图
     * @param context context
     * @param resIds  贴图集合，顺序是：
     *                <ul><li>右{@link GLES20#GL_TEXTURE_CUBE_MAP_POSITIVE_X}</li>
     *                <li>左{@link GLES20#GL_TEXTURE_CUBE_MAP_NEGATIVE_X}</li>
     *                <li>上{@link GLES20#GL_TEXTURE_CUBE_MAP_POSITIVE_Y}</li>
     *                <li>下{@link GLES20#GL_TEXTURE_CUBE_MAP_NEGATIVE_Y}</li>
     *                <li>后{@link GLES20#GL_TEXTURE_CUBE_MAP_POSITIVE_Z}</li>
     *                <li>前{@link GLES20#GL_TEXTURE_CUBE_MAP_NEGATIVE_Z}</li></ul>
     * @return int
     */
    public static int createTextureCube(Context context, int[] resIds) {
        if (resIds != null && resIds.length >= 6) {
            int[] texture = new int[1];
            //生成纹理
            GLES30.glGenTextures(1, texture, 0);
            //生成纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, texture[0]);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
//            if (OpenGLVersion > 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_CLAMP_TO_EDGE);
//            }

            Bitmap bitmap;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            for (int i = 0; i < resIds.length; i++) {
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        resIds[i], options);
                if (bitmap == null) {
                    GLES30.glDeleteTextures(1, texture, 0);
                    return 0;
                }
                GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, bitmap, 0);
                bitmap.recycle();
            }
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
            return texture[0];
        }
        return 0;
    }

}
