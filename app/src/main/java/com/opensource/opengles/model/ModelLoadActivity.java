package com.opensource.opengles.model;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.opensource.opengles.base.AbsGLSurfaceActivity;

public class ModelLoadActivity extends AbsGLSurfaceActivity {
    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new ModelLoadRenderer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
