package com.opensource.opengles.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.opensource.opengles.camera.base.AbsObjectRender;
import com.opensource.opengles.camera.base.BaseCameraRenderer;
import com.opensource.opengles.camera.render.TrianColorRender;


public class CameraGLSurface extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    private BaseCameraRenderer render;

    public CameraGLSurface(Context context) {
        super(context);
    }

    public CameraGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        render = new CameraQuarRender(context,this);
        setRenderer(render);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        //render.setObjectRender(new TrianColorRender()); //默认给一个render
    }

    public void setObjectRender(AbsObjectRender absObjectRender){
        if (render != null){
            render.setObjectRender(absObjectRender);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }
}
