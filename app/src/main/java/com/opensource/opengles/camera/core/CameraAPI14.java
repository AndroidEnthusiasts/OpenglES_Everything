package com.opensource.opengles.camera.core;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraAPI14 implements ICamera {
    //当前的id
    private int mCameraId;
    private Camera mCamera;
    public Camera.Parameters mCameraParameters;
    private AspectRatio mRatio;
    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private final ISize.ISizeMap mPreviewSizes = new ISize.ISizeMap();
    private final ISize.ISizeMap mPictureSizes = new ISize.ISizeMap();

    private int mDesiredHeight = 1920;
    private int mDesiredWidth = 1080;
    private boolean mAutoFocus;
    public ISize mPreviewSize;
    public ISize mPicSize;

    private final AtomicBoolean isPictureCaptureInProgress = new AtomicBoolean(false);
    private TakePhotoCallback photoCallBack;


    public CameraAPI14() {
        mDesiredHeight = 1920;
        mDesiredWidth = 1080;
        //创建默认的比例.因为后置摄像头的比例，默认的情况下，都是旋转了270
        mRatio = AspectRatio.of(mDesiredWidth, mDesiredHeight).inverse();
    }

    @Override
    public boolean open(int cameraId) {
        if (mCamera != null) {
            releaseCamera();
        }
        mCameraId = cameraId;
        mCamera = Camera.open(cameraId);
        //开启Camera之后，必然要涉及到的操作就是设置参数
        if (mCamera != null) {
            mCameraParameters = mCamera.getParameters();
            mPreviewSizes.clear();
            //先收集参数
            for (Camera.Size size : mCameraParameters.getSupportedPreviewSizes()) {
                mPreviewSizes.add(new ISize(size.width, size.height));
            }

            mPictureSizes.clear();
            for (Camera.Size size : mCameraParameters.getSupportedPictureSizes()) {
                mPictureSizes.add(new ISize(size.width, size.height));
            }
            //挑选出最需要的参数
            adJustParametersByAspectRatio();
            return true;
        }
        return false;
    }

    private void adJustParametersByAspectRatio() {
        SortedSet<ISize> sizes = mPreviewSizes.sizes(mRatio);
        if (sizes == null) {  //表示不支持
            return;
        }
        //当前先不考虑Orientation
        ISize previewSize;
        mPreviewSize = new ISize(mDesiredWidth, mDesiredHeight);
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            previewSize = new ISize(mDesiredHeight, mDesiredWidth);
            ;
//            mCameraParameters.setRotation(90);
        } else {
            previewSize = mPreviewSize;
        }

        //默认去取最大的尺寸
        mPicSize = mPictureSizes.sizes(mRatio).last();

        mCameraParameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        mCameraParameters.setPictureSize(mPicSize.getWidth(), mPicSize.getHeight());

        //设置对角和闪光灯
        setAutoFocusInternal(mAutoFocus);
        //先不设置闪光灯
//        mCameraParameters.setFlashMode("FLASH_MODE_OFF");

        //设置到camera中
        mCameraParameters.setRotation(90);
        mCamera.setParameters(mCameraParameters);
        mCamera.setDisplayOrientation(90);
    }

    private boolean setAutoFocusInternal(boolean autoFocus) {
        mAutoFocus = autoFocus;
//        if (isCameraOpened()) {
        final List<String> modes = mCameraParameters.getSupportedFocusModes();
        if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
            mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        } else {
            mCameraParameters.setFocusMode(modes.get(0));
        }
        return true;
//        } else {
//            return false;
//        }
    }

    private boolean setFlashInternal(int flash) {
//        if (isCameraOpened()) {
//            List<String> modes = mCameraParameters.getSupportedFlashModes();
//            String mode = FLASH_MODES.get(flash);
//            if (modes != null && modes.contains(mode)) {
//                mCameraParameters.setFlashMode(mode);
//                mFlash = flash;
//                return true;
//            }
//            String currentMode = FLASH_MODES.get(mFlash);
//            if (modes == null || !modes.contains(currentMode)) {
//                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                mFlash = Constants.FLASH_OFF;
//                return true;
//            }
        return false;
//        } else {
//            mFlash = flash;
//            return false;
//        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void setAspectRatio(AspectRatio ratio) {
        this.mRatio = ratio;
    }

    @Override
    public boolean preview() {
        if (mCamera != null) {
            mCamera.startPreview();
            return true;
        }
        return false;
    }

    @Override
    public boolean switchTo(int cameraId) {
        close();
        open(cameraId);
        return false;
    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ISize getPreviewSize() {
        return mPreviewSize;
    }

    @Override
    public ISize getPictureSize() {
        return mPicSize;
    }

    @Override
    public void takePhoto(TakePhotoCallback callback) {
        this.photoCallBack = callback;

        if (getAutoFocus()) {
            mCamera.cancelAutoFocus();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    takePictureInternal();
                }
            });
        } else {
            takePictureInternal();
        }


    }


    void takePictureInternal() {
        if (!isPictureCaptureInProgress.getAndSet(true)) {
            mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    isPictureCaptureInProgress.set(false);
                    if (photoCallBack != null) {
                        photoCallBack.onTakePhoto(data, mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    }
                    camera.cancelAutoFocus();
                    camera.startPreview();
                }
            });
        }
    }


    boolean getAutoFocus() {
        String focusMode = mCameraParameters.getFocusMode();
        return focusMode != null && focusMode.contains("continuous");
    }


    @Override
    public void setOnPreviewFrameCallback(PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreviewSize.getWidth(), mPreviewSize.getHeight());
                }
            });
        }
    }
}
