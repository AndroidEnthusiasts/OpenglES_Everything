package com.opensource.opengles.render;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private boolean isRenderSet; //Render是否已经设置了


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isSupportEs2 = GLESUtils.isSupportEs2(this);
        if (isSupportEs2) {
            //创建一个GLSurfaceView
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setEGLContextClientVersion(2);
            //设置自己的Render.Render 内进行图形的绘制
            glSurfaceView.setRenderer(new TextureFilterShapeRender(this));
            isRenderSet = true;
            setContentView(glSurfaceView);
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRenderSet) {
            glSurfaceView.onPause();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRenderSet) {
            glSurfaceView.onResume();
        }

    }
}
