package com.opensource.opengles.shape;

import android.content.Context;
import android.opengl.GLES20;

import com.opensource.opengles.base.BaseViewGLRender;
import com.opensource.opengles.common.Constant;
import com.opensource.opengles.render.GLESUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.opensource.opengles.common.Constant.BYTES_PER_FLOAT;

/***
 * 绘制基本的点和线、以及三角形
 */
public class SimpleShapeRender extends BaseViewGLRender {
    private static final String Vertext_Shader_file = "shape/vertex_simple_shader.glsl";
    private static final String Fragment_Shader_file = "shape/fragment_simple_shader.glsl";
    private static final String A_POSITION = "aPosition";
    private static final String U_COLOR = "uColor";

    //在数组中，一个顶点需要3个来描述其位置，需要3个偏移量
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORDS_PER_COLOR = 0;

    //在数组中，描述一个顶点，总共的顶点需要的偏移量。这里因为只有位置顶点，所以和上面的值一样
    private static final int TOTAL_COMPONENT_COUNT = COORDS_PER_VERTEX+COORDS_PER_COLOR;
    //一个点需要的byte偏移量。
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    //顶点的坐标系
    private static float TRIANGLE_COORDS[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f   // bottom right
    };

    //三个顶点的颜色参数
    private float color[] = {
            1.0f, 0.0f, 0.0f, 1.0f,// top
            0.0f, 1.0f, 0.0f, 1.0f,// bottom left
            0.0f, 0.0f, 1.0f, 1.0f// bottom right
    };

    private static float TRIANGLE_COLOR[] = {1.0f, 1.0f, 1.0f, 1.0f};

    private static final int VERTEX_COUNT = TRIANGLE_COORDS.length / TOTAL_COMPONENT_COUNT;

    private final Context context;
    //pragram的指针
    private int mProgramObjectId;
    //顶点数据的内存映射
    private final FloatBuffer mVertexFloatBuffer;
    //顶点颜色缓存
    private final FloatBuffer colorFloatBuffer;

    public SimpleShapeRender(Context context) {
        this.context = context;
        /***
         *      调用GLES20的包的方法时，其实就是调用JNI的方法。
         *         所以分配本地的内存块，将java数据复制到本地内存中，而本地内存可以不受垃圾回收的控制
         *         1. 使用nio中的ByteBuffer来创建内存区域。
         *         2. ByteOrder.nativeOrder()来保证，同一个平台使用相同的顺序
         *         3. 然后可以通过put方法，将内存复制过去。
          */
        mVertexFloatBuffer = ByteBuffer
                .allocateDirect(TRIANGLE_COORDS.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TRIANGLE_COORDS);
        mVertexFloatBuffer.position(0);

        //顶点颜色相关
        colorFloatBuffer = ByteBuffer
                .allocateDirect(color.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(color);
        colorFloatBuffer.position(0);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl,config);
        //获取shader
        String vertexShaderCode = GLESUtils.readAssetShaderCode(context, Vertext_Shader_file);
        String fragmentShaderCode = GLESUtils.readAssetShaderCode(context, Fragment_Shader_file);
        //编译shader
        int vertexShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode,Constant.GLES_VERSION_2);
        int fragmentShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode,Constant.GLES_VERSION_2);

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
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, mVertexFloatBuffer);
        //2.开始启用我们的position
        GLES20.glEnableVertexAttribArray(0);
        //准备颜色数据
        GLES20.glVertexAttribPointer(1, 4, GLES20.GL_FLOAT, false, 0, colorFloatBuffer);
//        GLES20.glVertexAttribPointer(2, 4, GLES20.GL_FLOAT, false, 0, colorFloatBuffer);
        //启用顶点颜色句柄
        GLES20.glEnableVertexAttribArray(1);

        //绘制三个点
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, COORDS_PER_VERTEX);

        //绘制三条线
        GLES20.glLineWidth(3);//设置线宽
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, COORDS_PER_VERTEX);

        //绘制三角形
        //GLES20.glDrawArrays(GLES30.GL_TRIANGLES, 0, POSITION_COMPONENT_COUNT);

        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(0);
        GLES20.glDisableVertexAttribArray(1);

    }
}
