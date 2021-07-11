//#version 300 es

attribute vec4 vPosition;
attribute vec4 aColor;
varying vec4 vColor;
void main() {
    gl_Position  = vPosition;
    gl_PointSize = 10.0;
    vColor = aColor;
}
//#version 300 es
//layout(location = 0 ) in vec4 vPostion;
//layout(location = 1 ) in vec4 aColor;
//layout(location = 2 ) in vec2 aTextureCoord;
//
//uniform mat4 u_Matrix;
//out vec4 vColor;
//out vec2 VTextureCoord;
//
//void main(){
//    gl_Postion = u_Matrix*vPostion;
//    gl_PointSize = 10.0;
//    vColor = aColor;
//}
