precision mediump float;

uniform vec4 uColor;
uniform sampler2D uTexture;

out vec4 vFragColor;
in vec2 vTexCoord;
void main(){
    gl_FragColor = uColor;
    vFragColor = texture(uTexture,vTexCoord);
}

