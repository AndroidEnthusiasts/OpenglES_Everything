#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES vTexture;
//添加一个u_ChangeColor
uniform vec3 u_ChangeColor;

//modifyColor.将color限制在rgb
void modifyColor(vec4 color){
    color.r=max(min(color.r,1.0),0.0);
    color.g=max(min(color.g,1.0),0.0);
    color.b=max(min(color.b,1.0),0.0);
    color.a=max(min(color.a,1.0),0.0);
}


void main() {
//    vec4  nColor = texture2D( vTexture, textureCoordinate);
//    //在这里处理 色彩 冷暖、亮度等等
//    vec4 deltaColor=nColor+vec4(u_ChangeColor,0.0);
//    modifyColor(deltaColor);
    vec4 color = vec4(0.0);
    int coreSize = 3;
    int halfSize=coreSize/2;
    float texelOffset = 0.01;
    //创建卷积核
    float kernel[9];
    kernel[6] = 1.0; kernel[7] = 2.0; kernel[8] = 1.0;
    kernel[3] = 1.0; kernel[4] = 1.0; kernel[5] = 1.0;
    kernel[0] = 1.0; kernel[1] = 2.0; kernel[2] = 1.0;
    int index = 0;
    for(int y=0;y<coreSize;y++)
    {
        for(int x = 0;x<coreSize;x++)
        {
            vec4 currentColor = texture2D(vTexture,textureCoordinate+vec2(float((-1+x))*texelOffset,float((-1+y))*texelOffset));
            color += currentColor*kernel[index];
            index++;
        }
    }
    color = color/16;    //归一处理
    gl_FragColor = color;
}