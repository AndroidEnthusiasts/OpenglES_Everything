###  EGL绘制的流程
- 创建 egl display
- 初始化egl和 display之间的连接
- 创建 egl config
- 创建 egl Context
- 创建 egl surface(本地的window有关)
- 绑定Context和surface
--------------------------------------------
-  通过gl函数进行绘制  绘制API
-  eglSwapBuffer()
-  release Context
- 和ffmpeg是一样的 delete surface、Context、display


### 使用opengles的步骤
1、EGL初始化
2、OpenGLes 初始化
3、OpenGLes 设置&绘制
4、OpenGLes 资源释放
5、EGL的释放

离屏渲染:自己实现上面的流程 涂鸦画板 等等业务

~~~
1、eglGetDisplay (EGLNativeDisplayType display_id);
2、eglInitialize (EGLDisplay dpy, EGLint *major, EGLint *minor);
3、eglChooseConfig (EGLDisplay dpy, const EGLint *attrib_list,
                    EGLConfig *configs, EGLint config_size, EGLint *num_config);
4、eglCreateWindowSurface (EGLDisplay dpy, EGLConfig config,
                            EGLNativeWindowType win, const EGLint *attrib_list);
5、eglCreateContext (EGLDisplay dpy, EGLConfig config,
                    EGLContext share_context, const EGLint *attrib_list);
6、eglMakeCurrent (EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
~~~
