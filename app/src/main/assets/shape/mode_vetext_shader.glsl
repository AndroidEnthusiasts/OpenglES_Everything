attribute vec4 vPosition; // 顶点坐标

attribute vec2 vTextureCoord;  //纹理坐标

varying vec2 aCoord;
uniform mat4 vMatrix;


void main(){
    gl_Position =vMatrix * vPosition;
    aCoord = vTextureCoord;
}