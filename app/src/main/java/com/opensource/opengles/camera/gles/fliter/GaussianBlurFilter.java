package com.opensource.opengles.camera.gles.fliter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.util.Arrays;

public class GaussianBlurFilter extends AFilter{
    private int mHCoordMatrix;
    private float[] mCoordMatrix = Arrays.copyOf(OM, 16);
    private int mUChangeColor;


    public GaussianBlurFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/gaussianblur_filter_vertex.glsl", "shader/gaussianblur_filter_fragment.glsl");
        mHCoordMatrix = GLES20.glGetUniformLocation(mProgram, "vCoordMatrix");
        mUChangeColor = GLES20.glGetUniformLocation(mProgram, "u_ChangeColor");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

    public void setmCoordMatrix(float[] mCoordMatrix) {
        this.mCoordMatrix = mCoordMatrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniformMatrix4fv(mHCoordMatrix, 1, false, mCoordMatrix, 0);
        //设置自己的颜色矩阵
//        GLES20.glUniform3fv(mUChangeColor, 1, coolFilterColorData, 0);
    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        GLES20.glUniform1i(mHTexture, getTextureType());
    }

}
