package com.opensource.opengles.render;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class TextureFilterShapeRender implements GLSurfaceView.Renderer {
    public TextureFilterShapeRender(ViewActivity viewActivity) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //简单的给窗口填充一种颜色
        GLES20.glClearColor(0.0f,0.0f,0.0f,0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //glClear（）的唯一参数表示需要被清除的缓冲区。当前可写的颜色缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
