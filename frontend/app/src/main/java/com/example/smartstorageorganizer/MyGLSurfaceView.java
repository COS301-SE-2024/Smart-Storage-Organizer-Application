package com.example.smartstorageorganizer;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class MyGLSurfaceView extends GLSurfaceView {
//    private final MyGLRenderer renderer;

//    public MyGLSurfaceView(Context context) {
//        super(context);
//        // Set the OpenGL ES version
//        setEGLContextClientVersion(2);
//        // Set the renderer
//        renderer = new MyGLRenderer();
//        setRenderer(renderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//    }

    public MyGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context);
        init();
    }

    private void init() {
        // Set the EGL context to use OpenGL ES 2.0
        setEGLContextClientVersion(2);

        // Set an EGLConfigChooser to handle the wide gamut support issue
        setEGLConfigChooser(new MyEGLConfigChooser());

        // Set the renderer
        setRenderer(new MyGLRenderer(getContext()));

        // Set the render mode to only render when requested
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private static class MyEGLConfigChooser implements GLSurfaceView.EGLConfigChooser {
        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] attribList = {
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL10.EGL_NONE
            };
//            EGLConfig[] configs = new EGLConfig[1];
            javax.microedition.khronos.egl.EGLConfig[] configs = new javax.microedition.khronos.egl.EGLConfig[1];
            int[] numConfigs = new int[1];
            boolean results = egl.eglChooseConfig(display, attribList, configs, 1, numConfigs);
            Log.d("EGL", "Result: " + results + ", Number of Configs: " + numConfigs[0]);


            if (results && numConfigs[0] > 0) {
                return configs[0];
            } else {
                return null;
            }
        }
    }
}