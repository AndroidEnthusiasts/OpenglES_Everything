package com.opensource.opengles.skybox

import android.hardware.Sensor
import android.os.Bundle
import com.opensource.opengles.base.AbsGLSurfaceSensorActivity
import com.opensource.opengles.base.AbsSensorRenderer
import com.opensource.opengles.skybox.SkyboxRenderer

class SkyBoxActivity: AbsGLSurfaceSensorActivity() {

    override fun bindRenderer(): AbsSensorRenderer? {
        return SkyboxRenderer()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}