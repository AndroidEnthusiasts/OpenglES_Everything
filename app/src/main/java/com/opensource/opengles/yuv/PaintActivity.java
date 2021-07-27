package com.opensource.opengles.yuv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PaintActivity extends AppCompatActivity implements Camera.PreviewCallback{

    private final String TAG = PaintActivity.class.getSimpleName();
    public final static int WIDTH = 1080;
    public final static int HEIGHT = 1440;
    public final static int SIZE = WIDTH * HEIGHT * 3 / 2;

    private static final boolean DEBUG = false;
    public byte[] mData;

    private GLSurfaceView view;
    private Camera mCamera;

    private PowerManager.WakeLock wlock;

    static {
        System.loadLibrary("CAMERA_LIB");
    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wlock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "fb");
        wlock.acquire();
        view = new GLSurfaceView(PaintActivity.this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(new GLSurfaceView.Renderer() {

            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                openCamera(0);
                mCamera.startPreview();
                init(WIDTH, HEIGHT);
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                changeLayout(width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                if (mData != null)
                {
                    drawFrame(mData, SIZE);
                }
            }
        });
        setContentView(view);
    }

    private void openCamera(int id) {
        mCamera = Camera.open(id);
        Camera.Parameters cp = mCamera.getParameters();
        cp.setPreviewSize(WIDTH, HEIGHT);
        cp.setPictureSize(WIDTH, HEIGHT);
        cp.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        try {
            mCamera.addCallbackBuffer(mData);
            mCamera.setPreviewCallback(PaintActivity.this);
            mCamera.setPreviewTexture(new SurfaceTexture(10));
        } catch (IOException e) {
            Log.w(TAG, "setPreviewTexture error e = " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
        mCamera.stopPreview();
        wlock.release();
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (DEBUG)
        {
            Log.i(TAG, "onPreviewFrame()");
        }
        mData = data;
        camera.addCallbackBuffer(data);
    }



    /**
     * 初始化OpenGL ES 2.0
     */
    public static native void init(int width, int height);

    /**
     * 释放
     */
    public static native void release();

    /**
     * 传入宽高
     * @param width
     * @param height
     */
    public static native void changeLayout(int width, int height);

    /**
     * 渲染Yuv数据
     * @param data
     */
    public static native void drawFrame(byte[] data, int size);
}
