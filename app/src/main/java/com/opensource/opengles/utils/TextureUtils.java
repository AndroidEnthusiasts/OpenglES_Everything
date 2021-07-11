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
    public static int[] loadTextures(Context context, int[] resList){
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
}
