package com.opensource.opengles.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.opensource.opengles.common.Constant;
import com.opensource.opengles.render.GLESUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/***
 * 绘制基本的三角形
 */
public class TriangleShapeRender extends BaseViewGLRender  {
    private static final String Vertext_Shader_file = "shape/triangle_vetext_shader.glsl";
    private static final String Fragment_Shader_file = "shape/triangle_fragment_shader.glsl";
    private static final String A_POSITION = "aPosition";
    private static final String U_COLOR = "uColor";

    //在数组中，一个顶点需要3个来描述其位置，需要3个偏移量
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORDS_PER_COLOR = 0;

    //在数组中，描述一个顶点，总共的顶点需要的偏移量。这里因为只有位置顶点，所以和上面的值一样
    private static final int TOTAL_COMPONENT_COUNT = COORDS_PER_VERTEX+COORDS_PER_COLOR;
    //一个点需要的byte偏移量。
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT;

    //顶点的坐标系
    private static float TRIANGLE_COORDS[] = {
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f   // bottom right
    };

    private static float TRIANGLE_COLOR[] = {1.0f, 1.0f, 1.0f, 1.0f};

    private static final int VERTEX_COUNT = TRIANGLE_COORDS.length / TOTAL_COMPONENT_COUNT;

    private final Context context;
    //pragram的指针
    private int mProgramObjectId;
    //顶点数据的内存映射
    private final FloatBuffer mVertexFloatBuffer;

    public TriangleShapeRender(Context context) {
        this.context = context;
        /***
         *      调用GLES20的包的方法时，其实就是调用JNI的方法。
         *         所以分配本地的内存块，将java数据复制到本地内存中，而本地内存可以不受垃圾回收的控制
         *         1. 使用nio中的ByteBuffer来创建内存区域。
         *         2. ByteOrder.nativeOrder()来保证，同一个平台使用相同的顺序
         *         3. 然后可以通过put方法，将内存复制过去。
          */
        mVertexFloatBuffer = ByteBuffer
                .allocateDirect(TRIANGLE_COORDS.length * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TRIANGLE_COORDS);
        mVertexFloatBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //获取shader
        String vertexShaderCode = GLESUtils.readAssetShaderCode(context, Vertext_Shader_file);
        String fragmentShaderCode = GLESUtils.readAssetShaderCode(context, Fragment_Shader_file);
        //编译shader
        int vertexShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

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
        //1.根据我们定义的取出定义的位置
        int vPosition = GLES20.glGetAttribLocation(mProgramObjectId, A_POSITION);
        //2.开始启用我们的position
        GLES20.glEnableVertexAttribArray(vPosition);
        //3.将坐标数据放入
        GLES20.glVertexAttribPointer(
                vPosition,  //上面得到的id
                COORDS_PER_VERTEX, //告诉他用几个偏移量来描述一个顶点
                GLES20.GL_FLOAT, false,
                STRIDE, //一个顶点需要多少个字节的偏移量
                mVertexFloatBuffer);

        //取出颜色
        int uColor = GLES20.glGetUniformLocation(mProgramObjectId, U_COLOR);

        //开始绘制
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(
                uColor,
                1,
                TRIANGLE_COLOR,
                0
        );

        //绘制三角形.
        //draw arrays的几种方式 GL_TRIANGLES三角形 GL_TRIANGLE_STRIP三角形带的方式(开始的3个点描述一个三角形，后面每多一个点，多一个三角形) GL_TRIANGLE_FAN扇形(可以描述圆形)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPosition);
    }
}
