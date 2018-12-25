#extension GL_OES_EGL_image_external : require
  //samplerExternalOES 纹理采样器，并且要在头部增加使用扩展纹理的声明
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES vTexture;
void main() {
    gl_FragColor = texture2D( vTexture, textureCoordinate );
}
