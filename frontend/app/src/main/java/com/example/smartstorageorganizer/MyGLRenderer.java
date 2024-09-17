package com.example.smartstorageorganizer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.List;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private List<Cube> cubes = new ArrayList<>();
    private List<Object> items = new ArrayList<>(); // Placeholder for items
    private Object container = new Object(); // Placeholder for container

    public MyGLRenderer(Context context) {
    }

//    public MyGLRenderer(Context context) {
//    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Enable depth testing to handle proper rendering of 3D objects
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Run the 3D bin packing algorithm to get the packed items
        List<BinPackingResult> packedItems = run3DBinPackingAlgorithm(items, container);

        // Create a Cube for each packed item
        for (BinPackingResult result : packedItems) {
            cubes.add(new Cube(result.x, result.y, result.z, result.width, result.height, result.depth));
        }

        // If no cubes are added, add a dummy cube for testing purposes
        if (cubes.isEmpty()) {
            cubes.add(new Cube(0, 0, 0, 1, 1, 1)); // Example cube with size 1x1x1 at origin
        }

        // Log message for debugging
        Log.d("Renderer", "onSurfaceCreated: Cubes initialized.");
    }

    public void onDrawFrame(GL10 gl) {
//        Log.d("Renderer", "onDrawFrame called");
        // create a toast for debugging purposes


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        Toast.makeText(null, "onDrawFrame called", Toast.LENGTH_SHORT).show();
        // Draw each cube representing a packed item
        for (Cube cube : cubes) {
            cube.draw();
        }

        // Check for OpenGL errors and log them if any
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("GL_ERROR", "OpenGL Error: " + error);
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    private List<BinPackingResult> run3DBinPackingAlgorithm(List<Object> items, Object container) {
        // Placeholder implementation
        return new ArrayList<>();
    }

    public class BinPackingResult {
        public float x, y, z, width, height, depth;
    }
}