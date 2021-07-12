package com.opensource.opengles.ui_yida.shader;

public class VideoShader {
    /**
     * 视频播放定点着色器
     */
    public static final String mVideoVertexShader = "uniform mat4 uMVPMatrix;\n"
            + "uniform mat4 uSTMatrix;\n"
            + "attribute vec4 aPosition;\n"
            + "attribute vec4 aTextureCoord;\n"
            + "varying vec2 vTextureCoord;\n"
            + "void main() {\n"
            + "  gl_Position = uMVPMatrix * aPosition;\n"
            + "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n"
            + "}\n";
    /**
     * 视频播放片段着色器
     */
    public static final String mVideoShader =
            "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
                    + "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
                    + "}\n";
}
