package com.opensource.opengles.base

import android.opengl.GLSurfaceView

abstract class AbsSensorRenderer:GLSurfaceView.Renderer {
    abstract fun rotation(rotationMatrix:FloatArray)
}
