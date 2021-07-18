package com.opensource.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class YUVPlayer extends GLSurfaceView implements Runnable, SurfaceHolder.Callback, GLSurfaceView.Renderer {
    private String yuvFilePath = "/sdcard/out.yuv";

    public YUVPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        new Thread(this).start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setRenderer(this);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    @Override
    public void run() {
        loadYuvFile(yuvFilePath,getHolder().getSurface());
    }

    public native void loadYuvFile(String url, Object surface);
}
