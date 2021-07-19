package com.opensource.opengles.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;

import com.opensource.opengles.AppCore;
import com.opensource.opengles.R;
import com.opensource.opengles.base.BaseViewGLRender;
import com.opensource.opengles.common.Constant;
import com.opensource.opengles.render.GLESUtils;
import com.opensource.opengles.utils.GLDataUtil;
import com.opensource.opengles.utils.TextureUtils;
import com.opensource.opengles.utils.model.bean.LoadObjectUtil;
import com.opensource.opengles.utils.model.bean.ObjectBean;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ModelLoadRenderer extends BaseViewGLRender {
    private static final String TAG = "ModelLoadRenderer";
    private static final String Vertext_Shader_file = "shape/mode_vetext_shader.glsl";
    private static final String Fragment_Shader_file = "shape/mode_fragment_shader.glsl";
    //渲染程序
    private int mProgram;
    //相机矩阵
    private final float[] mViewMatrix = new float[16];
    //投影矩阵
    private final float[] mProjectMatrix = new float[16];
    //最终变换矩阵
    private final float[] mMVPMatrix = new float[16];

    private List<ObjectBean> list;
    public static final String planetDir = "planet", rockDir = "rock";
    private int mProgramObjectId;

    public ModelLoadRenderer() {
        list = LoadObjectUtil.loadObject(rockDir + "/rock.obj",
                AppCore.getInstance().getResources(), rockDir);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        //获取shader
        String vertexShaderCode = GLESUtils.readAssetShaderCode(AppCore.getInstance().getContext(), Vertext_Shader_file);
        String fragmentShaderCode = GLESUtils.readAssetShaderCode(AppCore.getInstance().getContext(), Fragment_Shader_file);
        //编译shader
        int vertexShaderObjectId = GLESUtils.compileShaderCode(GLES30.GL_VERTEX_SHADER, vertexShaderCode, Constant.GLES_VERSION_3);
        int fragmentShaderObjectId = GLESUtils.compileShaderCode(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode,Constant.GLES_VERSION_3);

        mProgramObjectId = GLES20.glCreateProgram();
        //attach
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);
        //link
        GLES20.glLinkProgram(mProgramObjectId);
        //use
        GLES20.glUseProgram(mProgramObjectId);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1,1,1,10);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix,0,0,0,4.5f,//摄像机坐标
                0f,0f,0f,//目标物的中心坐标
                0f,1.0f,0.0f);//相机方向
        //设置up方向为y轴正方向，upx = 0,upy = 1,upz = 0。这是相机正对着目标图像
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        //开启深度测试
        GLES30.glEnable(GLES20.GL_DEPTH_TEST);
        //左乘矩阵
        int uMaxtrixLocation = GLES30.glGetUniformLocation(mProgram,"vMatrix");
        // 将前面计算得到的mMVPMatrix(frustumM setLookAtM 通过multiplyMM 相乘得到的矩阵) 传入vMatrix中，与顶点矩阵进行相乘
        GLES30.glUniformMatrix4fv(uMaxtrixLocation,1,false,mMVPMatrix,0);

        int aPositionLocation = GLES30.glGetAttribLocation(mProgram,"vPosition");
        GLES30.glEnableVertexAttribArray(aPositionLocation);

        int aTextureLocation = GLES20.glGetAttribLocation(mProgram,"vTextureCoord");
        int vTextureFilterLoc = GLES20.glGetUniformLocation(mProgram, "vTexture");
        Log.e(TAG, "onDrawFrame: textureLocation="+aTextureLocation);
        //启用顶点颜色句柄
        GLES30.glEnableVertexAttribArray(aTextureLocation);

        drawModel(aPositionLocation,aTextureLocation,vTextureFilterLoc);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(aPositionLocation);
        GLES30.glDisableVertexAttribArray(aTextureLocation);
    }


    // 参数顶点坐标handle位置，纹理坐标handle位置，纹理位置
    private void drawModel(int vertexPosLoc, int textPosLoc, int textureLoc) {
        for (ObjectBean item : list) {
            if (item != null) {

                GLES20.glVertexAttribPointer(vertexPosLoc, 3, GLES20.GL_FLOAT,
                        false, 3 * 4, GLDataUtil.createFloatBuffer(item.aVertices));
                GLES20.glVertexAttribPointer(textPosLoc, 2, GLES20.GL_FLOAT,
                        false, 2 * 4, GLDataUtil.createFloatBuffer(item.aTexCoords));

                if (item.mtl != null) {
                    if (!TextUtils.isEmpty(item.mtl.Kd_Texture)) {
                        if (item.diffuse < 0) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(AppCore.getInstance().getContext().getAssets().open(
                                        rockDir + "/" + item.mtl.Kd_Texture));
                                item.diffuse = TextureUtils.createTextureWithBitmap(bitmap);
                                bitmap.recycle();
                            } catch (IOException e) {
                                Log.e(TAG, "onDrawFrame: "+e);
                            }
                        }
                    } else {
                        if (item.diffuse < 0) {
                            item.diffuse = TextureUtils.loadTexture(AppCore.getInstance().getContext(), R.drawable.ic_launcher_background);
                        }
                    }

                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, item.diffuse);
                    GLES20.glUniform1i(textureLoc, 0);
                }

                // 绘制顶点
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, item.aVertices.length / 3);
            }
        }
    }
}
