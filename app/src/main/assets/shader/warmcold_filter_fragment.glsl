#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES vTexture;
//添加一个u_ChangeColor
uniform vec3 u_ChangeColor;
//uniform int u_ChangeType;

//modifyColor.将color限制在rgb
void modifyColor(vec4 color){
    color.r=max(min(color.r,1.0),0.0);
    color.g=max(min(color.g,1.0),0.0);
    color.b=max(min(color.b,1.0),0.0);
    color.a=max(min(color.a,1.0),0.0);
}


void main() {
    vec4  nColor = texture2D( vTexture, textureCoordinate);
    //在这里处理 色彩 冷暖、亮度等等
    vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
    modifyColor(deltaColor);
    gl_FragColor = deltaColor;
}