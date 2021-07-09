#ifndef NDKER_GLUTILS_H
#define NDKER_GLUTILS_H

#include <stdlib.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include "Log.h"

class GLUtils {
public:
    static GLuint glProgram(const char *vertex, const char *fragment); //Shader编译
    static void glProgramDel(GLuint program);//删除shader

private:
    static GLuint glShader(GLenum type, const char *p); //Shader编译
};

#endif //NDKER_GLUTILS_H
