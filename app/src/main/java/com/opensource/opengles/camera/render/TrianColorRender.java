package com.opensource.opengles.camera.render;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.opensource.opengles.R;
import com.opensource.opengles.camera.base.AbsObjectRender;
import com.opensource.opengles.utils.GLDataUtil;
import com.opensource.opengles.utils.ResReadUtils;
import com.opensource.opengles.utils.ShaderUtils;

import java.nio.FloatBuffer;

public class TrianColorRender extends AbsObjectRender {
    private static final String TAG = "TriangleColorRender";
    //3个定点，等腰直角
    private float vertexCoords[] ={
            0.5f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };
    private float colorCoords[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };
    //顶点数组buffer
    private FloatBuffer vertexBuffer;
    //颜色数组buffer
    private FloatBuffer colorBuffer;

    //三角形变换临时矩阵
    private final float[] rotationMatrix = new float[16];
    private final float[] mTriangleTempMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    //旋转角度
    private float angle =0;

    public TrianColorRender() {
    }



    @Override
    public void initProgram() {
        // 三角形绘制相关初始化
        vertexBuffer = GLDataUtil.createFloatBuffer(vertexCoords);
        colorBuffer = GLDataUtil.createFloatBuffer(colorCoords);
        //编译顶点着色程序
        String verTriShaderStr = ResReadUtils.readResource(R.raw.vertex_base_matrix_shader);
        int verTriShaderId = ShaderUtils.compileVertexShader(verTriShaderStr);
        //编译片段着色程序
        String fragTriShaderStr = ResReadUtils.readResource(R.raw.fragment_base_common_shader);
        int fragTriShaderId = ShaderUtils.compileFragmentShader(fragTriShaderStr);
        //连接程序
        mProgram = ShaderUtils.linkProgram(verTriShaderId, fragTriShaderId);
        if (mProgram == 0){
            Log.e(TAG, "initProgram: 初始化失败");
        }else{
            Log.e(TAG, "initProgram: 初始化成功"+mProgram);
        }
    }

    @Override
    public void onDrawFrame() {
        GLES30.glUseProgram(mProgram);
        Matrix.setIdentityM(rotationMatrix,0);
        Matrix.multiplyMM(mTriangleTempMatrix, 0, projectMatrix, 0, cameraMatrix, 0);
        Matrix.rotateM(rotationMatrix,0,angle,0,0,1);
        Matrix.multiplyMM(mvpMatrix,0,mTriangleTempMatrix,0,rotationMatrix,0);

        //左乘矩阵
        int uMaxtrixLocation = GLES30.glGetUniformLocation(mProgram,"vMatrix");
        // 将前面计算得到的mMVPMatrix(frustumM setLookAtM 通过multiplyMM 相乘得到的矩阵) 传入vMatrix中，与顶点矩阵进行相乘
        GLES30.glUniformMatrix4fv(uMaxtrixLocation,1,false,mvpMatrix,0);

        int aPositionLocation = GLES30.glGetAttribLocation(mProgram,"vPosition");
        GLES30.glEnableVertexAttribArray(aPositionLocation);
        //x y z 所以数据size 是3
        GLES30.glVertexAttribPointer(aPositionLocation,3,GLES30.GL_FLOAT,false,0,vertexBuffer);

        int aColorLocation = GLES20.glGetAttribLocation(mProgram,"aColor");
        //准备颜色数据 rgba 所以数据size是 4
        GLES30.glVertexAttribPointer(aColorLocation, 4, GLES30.GL_FLOAT, false, 0, colorBuffer);
        //启用顶点颜色句柄
        GLES30.glEnableVertexAttribArray(aColorLocation);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(aPositionLocation);
        GLES30.glDisableVertexAttribArray(aColorLocation);
        GLES30.glUseProgram(0);
        angle += 1;
    }
}
