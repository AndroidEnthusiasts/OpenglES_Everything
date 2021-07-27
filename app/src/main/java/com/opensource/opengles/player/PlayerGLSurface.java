package com.opensource.opengles.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class PlayerGLSurface extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    private MediaGLRenderer render;

    public PlayerGLSurface(Context context) {
        super(context);
    }

    public PlayerGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        render = new MediaGLRenderer(context,this);
        setRenderer(render);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }


}

