package com.opensource.opengles.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.opensource.opengles.R;
import com.opensource.opengles.common.Constant;
import com.opensource.opengles.render.GLESUtils;
import com.opensource.opengles.base.BaseViewGLRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Texture2DShapeRender extends BaseViewGLRender {
    /**
     * 更新shader的位置
     */
    private static final String VERTEX_SHADER_FILE = "texture/texture_vertex_shader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "texture/texture_fragment_shader.glsl";
    private static final String A_POSITION = "a_Position";
    private static final String A_COORDINATE = "a_TextureCoordinates";
    private static final String U_TEXTURE = "u_TextureUnit";
    private static final String U_MATRIX = "u_Matrix";

    private static final int COORDS_PER_VERTEX = 2;
    private static final int COORDS_PER_ST = 2;
    private static final int TOTAL_COMPONENT_COUNT = COORDS_PER_VERTEX + COORDS_PER_ST;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT;

    //顶点的坐标系
    private static float TEXTURE_COORDS[] = {
            //Order of coordinates: X, Y,S,T
            -1.0f, 1.0f, 0.0f, 0.0f,
            -1.0f, -1.0f, 0.0f, 1.0f, //bottom left
            1.0f, 1.0f, 1.0f, 0.0f, // top right
            1.0f, -1.0f, 1.0f, 1.0f, // bottom right
    };


    private static final int VERTEX_COUNT = TEXTURE_COORDS.length / TOTAL_COMPONENT_COUNT;
    private final Context context;

    //pragram的指针
    private int mProgramObjectId;
    //顶点数据的内存映射
    private final FloatBuffer mVertexFloatBuffer;

    //模型矩阵
    private float[] mModelMatrix = new float[16];

    private Matrix mViewMatrix;

    //投影矩阵
    private float[] mProjectionMatrix = new float[16];
    private int uMatrix;
    private int uTexture;
    private int mTextureId;

    public Texture2DShapeRender(Context context) {
        this.context = context;

        mVertexFloatBuffer = ByteBuffer
                .allocateDirect(TEXTURE_COORDS.length * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEXTURE_COORDS);
        mVertexFloatBuffer.position(0);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        String vertexShaderCode = GLESUtils.readAssetShaderCode(context, VERTEX_SHADER_FILE);
        String fragmentShaderCode = GLESUtils.readAssetShaderCode(context, FRAGMENT_SHADER_FILE);
        int vertexShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode,Constant.GLES_VERSION_2);
        int fragmentShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode,Constant.GLES_VERSION_2);

        mProgramObjectId = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);
        GLES20.glLinkProgram(mProgramObjectId);

        GLES20.glUseProgram(mProgramObjectId);

        int aPosition = GLES20.glGetAttribLocation(mProgramObjectId, A_POSITION);
        mVertexFloatBuffer.position(0);
        GLES20.glVertexAttribPointer(
                aPosition,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                STRIDE,
                mVertexFloatBuffer);

        GLES20.glEnableVertexAttribArray(aPosition);


        int aCoordinate = GLES20.glGetAttribLocation(mProgramObjectId, A_COORDINATE);
        mVertexFloatBuffer.position(COORDS_PER_VERTEX);
        GLES20.glVertexAttribPointer(
                aCoordinate,
                COORDS_PER_ST,
                GLES20.GL_FLOAT, false,
                STRIDE,
                mVertexFloatBuffer);

        GLES20.glEnableVertexAttribArray(aCoordinate);

        uMatrix = GLES20.glGetUniformLocation(mProgramObjectId, U_MATRIX);

        uTexture = GLES20.glGetUniformLocation(mProgramObjectId, U_TEXTURE);

        mTextureId = createTexture();
    }

    //使用mip贴图来生成纹理，相当于将图片复制到openGL里面？
    private int createTexture() {
        final Bitmap mBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //加载Bitmap
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cat, options);
        Log.e("mBitmap",mBitmap.getHeight()+"===>"+mBitmap.getByteCount());
        //保存到textureObjectId
        int[] textureObjectId = new int[1];
        try{
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成一个纹理，保存到这个数组中
            GLES20.glGenTextures(1, textureObjectId, 0);
            //绑定GL_TEXTURE_2D
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectId[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

//            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//

            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

            //因为使用贴图的方式来生成纹理,故需要生成纹理
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            //回收释放
            mBitmap.recycle();
            //因为我们已经复制成功了。所以就进行解除绑定。防止修改
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            return textureObjectId[0];
        }}catch (Exception e){
            Log.e("error",e.toString());
        }
        return 0;
    }

    private int createTexture2() {
        final Bitmap mBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //加载Bitmap
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.test, options);
        //保存到textureObjectId
        int[] textureObjectId = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成一个纹理，保存到这个数组中
            GLES20.glGenTextures(1, textureObjectId, 0);
            //绑定GL_TEXTURE_2D
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectId[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

//            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//

            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

            //因为使用贴图的方式来生成纹理,故需要生成纹理
//            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            //回收释放
            mBitmap.recycle();
            //因为我们已经复制成功了。所以就进行解除绑定。防止修改
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            return textureObjectId[0];
        }
        return 0;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        //主要还是长宽进行比例缩放
        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            //横屏。需要设置的就是左右。
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1f, -1.f, 1f);
        } else {
            //竖屏。需要设置的就是上下
            Matrix.orthoM(mProjectionMatrix, 0, -1, 1f, -aspectRatio, aspectRatio, -1.f, 1f);
        }
    }

    //在OnDrawFrame中进行绘制
    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        //传递给着色器
        GLES20.glUniformMatrix4fv(uMatrix, 1, false, mProjectionMatrix, 0);

        //绑定和激活纹理
        //因为我们生成了MIP，放到了GL_TEXTURE0 中，所以重新激活纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //重新去半丁纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);


        //设置纹理的坐标
        GLES20.glUniform1i(uTexture, 0);

        //绘制三角形.
        //draw arrays的几种方式 GL_TRIANGLES三角形 GL_TRIANGLE_STRIP三角形带的方式(开始的3个点描述一个三角形，后面每多一个点，多一个三角形) GL_TRIANGLE_FAN扇形(可以描述圆形)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);

    }
}
