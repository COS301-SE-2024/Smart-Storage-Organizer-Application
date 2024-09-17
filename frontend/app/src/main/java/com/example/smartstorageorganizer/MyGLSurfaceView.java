package com.example.smartstorageorganizer;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.smartstorageorganizer.MyGLRenderer;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        // Set the OpenGL ES version
        setEGLContextClientVersion(2);
        // Set the renderer
        renderer = new MyGLRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}