package com.opensource.opengles.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.opensource.opengles.R;
import com.opensource.opengles.common.Constant;
import com.opensource.opengles.render.GLESUtils;
import com.opensource.opengles.utils.TextureUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CubeShapeRender extends BaseViewGLRender {
    private final Context context;
    private FloatBuffer textureBuffer;
    private FloatBuffer vertexBuffer;
    private ShortBuffer vertexIndexBuffer;

    // 立方体8个顶点坐标
    private float[] vertexPoints = new float[]{
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };

    /**
     * 立方体每个面(2个三角形)的组成顶点的索引
     * 需注意：每个面的第3个点和第5个点坐标一样，是为了和下方的纹理坐标进行一一对应，所采用的排列方式。
     */
    private static final short[] vertexIndexs = {
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,3,7,0,7,4,    //上面
            6,7,4,6,4,5,    //后面
            6,7,3,6,3,2,    //右面
            6,5,1,6,1,2     //下面
    };

    //每个面6个点，3个点组成一个三角形
    private static final float[] vertexSixTotal={
            //正面
            -1.0f,1.0f,1.0f,    //正面左上0
            1.0f,1.0f,1.0f,     //正面右上3
            1.0f,-1.0f,1.0f,    //正面右下2
            -1.0f,1.0f,1.0f,    //正面左上0
            1.0f,-1.0f,1.0f,    //正面右下2
            -1.0f,-1.0f,1.0f,   //正面左下1

            //左面
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            -1.0f,-1.0f,-1.0f,   //反面左下5
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,-1.0f,   //反面左下5
            -1.0f,1.0f,-1.0f,    //反面左上4

            //上面
            -1.0f,1.0f,1.0f,    //正面左上0
            1.0f,1.0f,1.0f,     //正面右上3
            1.0f,1.0f,-1.0f,    //反面右上7
            -1.0f,1.0f,1.0f,    //正面左上0
            1.0f,1.0f,-1.0f,     //反面右上7
            -1.0f,1.0f,-1.0f,    //反面左上4

            //后面
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
            -1.0f,1.0f,-1.0f,    //反面左上4
            1.0f,-1.0f,-1.0f,    //反面右下6
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5

            //右面
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
            1.0f,1.0f,1.0f,     //正面右上3
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,1.0f,     //正面右上3
            1.0f,-1.0f,1.0f,    //正面右下2

            //下面
            1.0f,-1.0f,-1.0f,    //反面右下6
            -1.0f,-1.0f,-1.0f,   //反面左下5
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,-1.0f,    //反面右下6
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2

    };

    //每个面6个点坐标,先按每个面都用同一个纹理处理
    /**  纹理坐标个数要与定点坐标个数一致，顶点数组是36个，纹理坐标个数也必须一一对应是36个
     *   且对应的坐标转换也要一致，下方每个面的纹理坐标都一样，所以对应的前面顶点坐标组成三角形
     *   时，坐标也要跟转换后的纹理坐标一一对应。
     * **/
    float textureVertex[] = {
            0.0f,0.0f,
            1.0f,0.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f,

            0.0f,0.0f,
            1.0f,0.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f,

            0.0f,0.0f,
            1.0f,0.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f,

            0.0f,0.0f,
            1.0f,0.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f,

            0.0f,0.0f,
            1.0f,0.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f,

            0.0f,0.0f,
            1.0f,0.0f,
            1.0f,1.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f,
    };
    //相机矩阵
    private final float[] mViewMatrix = new float[16];
    //投影矩阵
    private final float[] mProjectMatrix = new float[16];
    //最终变换矩阵
    private final float[] mMVPMatrix = new float[16];
    //旋转矩阵  [20200623]  进行物体旋转 要与其他矩阵相乘，最终保存到mMVPMatrix中
    private final float[] rotationMatrix = new float[16];
    private final float[] scaleMatrix = new float[16];
    private final float[] tempMatrix = new float[16];
    private final float[] tempScaleMatrix = new float[16];
    //渲染程序
    private int mProgramObjectId;
    //累计旋转过的角度
    private float angle =0;
    //缩放比例
    private volatile float scale = 0.3f;
    //是否处于持续放大状态
    private volatile boolean isOnScaleSmall = false;
    //上次执行缩放比例变化的时间
    private long lastZoomTime = System.currentTimeMillis();

    //纹理id列表
    private int[] textureIds;

    private static final String Vertext_Shader_file = "shape/vertex_cube_texture_shader.glsl";
    private static final String Fragment_Shader_file = "shape/fragment_cube_texture_shader.glsl";

    public CubeShapeRender(Context context){
        this.context = context;
        textureBuffer = ByteBuffer.allocateDirect(textureVertex.length *4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        textureBuffer.put(textureVertex);
        textureBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        vertexIndexBuffer = ByteBuffer.allocateDirect(vertexIndexs.length * 4)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的数据
        vertexIndexBuffer.put(vertexIndexs);
        vertexIndexBuffer.position(0);


        vertexBuffer = ByteBuffer.allocateDirect(vertexSixTotal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //要把所有6个面的数据都塞进去

        vertexBuffer.put(vertexSixTotal);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl,config);
        //开启深度测试
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        //获取shader
        String vertexShaderCode = GLESUtils.readAssetShaderCode(context, Vertext_Shader_file);
        String fragmentShaderCode = GLESUtils.readAssetShaderCode(context, Fragment_Shader_file);
        //编译shader
        int vertexShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode, Constant.GLES_VERSION_2);
        int fragmentShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode,Constant.GLES_VERSION_2);

        mProgramObjectId = GLES20.glCreateProgram();
        //attach
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);
        //link
        GLES20.glLinkProgram(mProgramObjectId);
        //use
        GLES20.glUseProgram(mProgramObjectId);

        //加载纹理
        int[] resList = {R.drawable.test,R.drawable.cat,R.drawable.test,R.drawable.cat,R.drawable.test,R.drawable.cat};
        textureIds = TextureUtils.loadTextures(context,resList);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl,width,height);
        float ratio = (float) width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1,1,3,20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix,0,5,5,10.0f,//摄像机坐标
                0f,0f,0f,//目标物的中心坐标
                0f,1.0f,0.0f);//相机方向
        //相机旋转，up的方向就会改变，这样就会会影响到绘制图像的角度。
        //例如设置up方向为y轴正方向，upx = 0,upy = 1,upz = 0。这是相机正对着目标图像
        //计算变换矩阵
        Matrix.multiplyMM(tempMatrix,0,mProjectMatrix,0,mViewMatrix,0);
        //先初始化为单位正交矩阵
        Matrix.setIdentityM(rotationMatrix,0);
        //旋转角度
        Matrix.rotateM(rotationMatrix,0,0,0,0,1);
        //透视矩阵*相机矩阵*旋转矩阵
        Matrix.multiplyMM(mMVPMatrix,0,tempMatrix,0,rotationMatrix,0);
        //进行初始旋转操作
        Matrix.setIdentityM(tempMatrix,0);
        Matrix.translateM(tempMatrix,0,0.25f,0,0);
        Matrix.multiplyMM(mMVPMatrix,0,mMVPMatrix,0,tempMatrix,0);
//                Matrix.rotateM(mViewMatrix,0,45,0,0,1);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        GLES20.glClear(GLES30.GL_COLOR_BUFFER_BIT| GLES30.GL_DEPTH_BUFFER_BIT);
        //先初始化为单位正交矩阵
        Matrix.setIdentityM(rotationMatrix,0);
        //旋转角度
        Matrix.rotateM(rotationMatrix,0,angle,0,1,0);//angle每次绘制完进行自增
        //正解---如果要进行自动旋转、平移、缩放等操作。则每次mMVPMatrix矩阵要重新走一遍流程进行计算，而不是在上一次mMVPMatrix的基础上进行矩阵相乘
        Matrix.multiplyMM(tempMatrix,0,mProjectMatrix,0,mViewMatrix,0);
        Matrix.setIdentityM(scaleMatrix,0);
        //设置缩放比例
        Matrix.scaleM(scaleMatrix,0,scale,scale,scale);
        //执行缩放
        Matrix.multiplyMM(tempScaleMatrix,0,tempMatrix,0,scaleMatrix,0);
        //执行旋转
        Matrix.multiplyMM(mMVPMatrix,0,tempScaleMatrix,0,rotationMatrix,0);


        //左乘矩阵
        int uMaxtrixLocation = GLES30.glGetUniformLocation(mProgramObjectId,"vMatrix");
        // 将前面计算得到的mMVPMatrix(frustumM setLookAtM 通过multiplyMM 相乘得到的矩阵) 传入vMatrix中，与顶点矩阵进行相乘
        GLES30.glUniformMatrix4fv(uMaxtrixLocation,1,false,mMVPMatrix,0);

        int aPositionLocation = GLES30.glGetAttribLocation(mProgramObjectId,"vPosition");
        GLES30.glEnableVertexAttribArray(aPositionLocation);
        //x y z 所以数据size 是3
        GLES30.glVertexAttribPointer(aPositionLocation,3,GLES30.GL_FLOAT,false,0, vertexBuffer);

        int aTextureLocation = GLES20.glGetAttribLocation(mProgramObjectId,"aTextureCoord");
        //纹理坐标数据 x、y，所以数据size是 2
        GLES30.glVertexAttribPointer(aTextureLocation, 2, GLES30.GL_FLOAT, false, 0, textureBuffer);
        //启用纹理颜色句柄
        GLES30.glEnableVertexAttribArray(aTextureLocation);
        //启用纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //每个面6个顶点数据，使用不同的纹理贴图
        for (int i = 0; i < textureIds.length; i++) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[i]);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES,i*6,6);

        }

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(aPositionLocation);
        GLES30.glDisableVertexAttribArray(aTextureLocation);
        //每次绘制旋转1度
        angle += 1;
        //每隔500ms进行一次缩放比例变化
        long timeNow = System.currentTimeMillis();
        if (timeNow - lastZoomTime <500){
            return;
        }else{
            lastZoomTime = timeNow;
        }
        if (!isOnScaleSmall){
            scale += 0.1;
            if (scale >=0.9){
                isOnScaleSmall = true;
            }
        }else{
            scale -= 0.1;
            if (scale <= 0.3){
                isOnScaleSmall = false;
            }
        }
    }
}
