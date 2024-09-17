package com.example.smartstorageorganizer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private List<Cube> cubes = new ArrayList<>();
    private List<Object> items = new ArrayList<>(); // Placeholder for items
    private Object container = new Object(); // Placeholder for container

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] modelViewProjectionMatrix = new float[16];
    private int shaderProgram;

    public MyGLRenderer(Context context) {
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        initShaderProgram();

        List<BinPackingResult> packedItems = run3DBinPackingAlgorithm(new ArrayList<>(), new Object());

        for (BinPackingResult result : packedItems) {
            cubes.add(new Cube(result.x, result.y, result.z, result.width, result.height, result.depth));
        }

        if (cubes.isEmpty()) {
            cubes.add(new Cube(0, 0, 0, 1, 1, 1));
        }

        Log.d("Renderer", "onSurfaceCreated: Cubes initialized. Total cubes: " + cubes.size());
    }

    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(shaderProgram);

        for (Cube cube : cubes) {
            cube.draw(modelViewProjectionMatrix);
        }

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("GL_ERROR", "OpenGL Error: " + error);
        }

        Log.d("Renderer", "onDrawFrame: Frame drawn.");
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.perspectiveM(projectionMatrix, 0, 45, ratio, 1, 10);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -5, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Log.d("Renderer", "onSurfaceChanged: Width: " + width + ", Height: " + height);
    }

    private List<BinPackingResult> run3DBinPackingAlgorithm(List<Object> items, Object container) {
        return new ArrayList<>();
    }

    public class BinPackingResult {
        public float x, y, z, width, height, depth;
    }

    private void initShaderProgram() {
        String vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";

        String fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        Log.d("ShaderProgram", "Shader program created and linked.");
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            Log.e("Shader", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        Log.d("Shader", "Shader compiled. Type: " + (type == GLES20.GL_VERTEX_SHADER ? "Vertex" : "Fragment"));
        return shader;
    }
}