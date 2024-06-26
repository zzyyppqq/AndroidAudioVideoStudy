package com.zyp.av.yuv.opengl;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class GLProgram {
    static final String TAG = "GLProgram";

    // program id
    private int _program;
    // window position
    public final int mWinPosition;
    // texture id
    private int _textureI;
    private int _textureII;
    private int _textureIII;
    private int _texture4;
    private int _texture5;
    // texture index in gles
    private int _tIindex;
    private int _tIIindex;
    private int _tIIIindex;
    private int _t4index;
    private int _t5index;
    // vertices on screen
    private float[] _vertices;
    // handles
    private int _positionHandle = -1, _coordHandle = -1;
    private int _yhandle = -1, _uhandle = -1, _vhandle = -1;
    private int _rgbhandle = -1, _rgbahandle = -1;
    private int _ytid = -1, _utid = -1, _vtid = -1;
    private int _rgbtid = -1, _rgbatid = -1;
    // vertices buffer
    private ByteBuffer _vertice_buffer;
    private ByteBuffer _coord_buffer;
    // video width and height
    private int _video_width = -1;
    private int _video_height = -1;
    // flow control
    private boolean isProgBuilt = false;

    /**
     * position can only be 0~4:
     * fullscreen => 0
     * left-top => 1
     * right-top => 2
     * left-bottom => 3
     * right-bottom => 4
     */
    public GLProgram(int position) {
        if (position < 0 || position > 4) {
            throw new RuntimeException("Index can only be 0 to 4");
        }
        mWinPosition = position;
        setup(mWinPosition);
    }

    /**
     * prepared for later use
     */
    public void setup(int position) {
        switch (position) {
            case 1:
                _vertices = squareVertices;
                _textureI = GLES20.GL_TEXTURE0;
                _textureII = GLES20.GL_TEXTURE1;
                _textureIII = GLES20.GL_TEXTURE2;
                _texture4 = GLES20.GL_TEXTURE3;
                _texture5 = GLES20.GL_TEXTURE4;
                _tIindex = 0;
                _tIIindex = 1;
                _tIIIindex = 2;
                _t4index = 3;
                _t5index = 4;
                break;
            case 2:
                _vertices = squareVertices2;
                _textureI = GLES20.GL_TEXTURE3;
                _textureII = GLES20.GL_TEXTURE4;
                _textureIII = GLES20.GL_TEXTURE5;
                _tIindex = 3;
                _tIIindex = 4;
                _tIIIindex = 5;
                break;
            case 3:
                _vertices = squareVertices3;
                _textureI = GLES20.GL_TEXTURE6;
                _textureII = GLES20.GL_TEXTURE7;
                _textureIII = GLES20.GL_TEXTURE8;
                _tIindex = 6;
                _tIIindex = 7;
                _tIIIindex = 8;
                break;
            case 4:
                _vertices = squareVertices4;
                _textureI = GLES20.GL_TEXTURE9;
                _textureII = GLES20.GL_TEXTURE10;
                _textureIII = GLES20.GL_TEXTURE11;
                _tIindex = 9;
                _tIIindex = 10;
                _tIIIindex = 11;
                break;
            case 0:
            default:
                _vertices = squareVertices;
                _textureI = GLES20.GL_TEXTURE0;
                _textureII = GLES20.GL_TEXTURE1;
                _textureIII = GLES20.GL_TEXTURE2;
                _tIindex = 0;
                _tIIindex = 1;
                _tIIIindex = 2;
                break;
        }
    }

    public boolean isProgramBuilt() {
        return isProgBuilt;
    }

    public void buildProgram(int yuvType) {
        createBuffers(_vertices, coordVertices);

        if (yuvType == MyGLRender.YUV_TYPE) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        } else if (yuvType == MyGLRender.Y_TYPE) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_Y);
        } else if (yuvType == MyGLRender.U_TYPE) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_U);
        } else if (yuvType == MyGLRender.V_TYPE) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_V);
        } else if (yuvType == MyGLRender.YUV_GRAY_TYPE) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_GRAY);
        } else if (yuvType == MyGLRender.RGB_TYPE) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_RGB);
        }  else if (yuvType == MyGLRender.RGBA_TYPE) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_RGBA);
        }
        Log.i(TAG, "_program = " + _program);

        /*
         * get handle for "vPosition" and "a_texCoord"
         */
        _positionHandle = GLES20.glGetAttribLocation(_program, "vPosition");
        Log.i(TAG, "_positionHandle = " + _positionHandle);
        checkGlError("glGetAttribLocation vPosition");
        if (_positionHandle == -1) {
            throw new RuntimeException("Could not get attribute location for vPosition");
        }
        _coordHandle = GLES20.glGetAttribLocation(_program, "a_texCoord");
        Log.i(TAG, "_coordHandle = " + _coordHandle);
        checkGlError("glGetAttribLocation a_texCoord");
        if (_coordHandle == -1) {
            throw new RuntimeException("Could not get attribute location for a_texCoord");
        }

        /*
         * get uniform location for y/u/v, we pass data through these uniforms
         */
        _yhandle = GLES20.glGetUniformLocation(_program, "tex_y");
        Log.i(TAG, "_yhandle = " + _yhandle);
//        checkGlError("glGetUniformLocation tex_y");
//        if (_yhandle == -1) {
//            throw new RuntimeException("Could not get uniform location for tex_y");
//        }
        _uhandle = GLES20.glGetUniformLocation(_program, "tex_u");
        Log.i(TAG, "_uhandle = " + _uhandle);
//        checkGlError("glGetUniformLocation tex_u");
//        if (_uhandle == -1) {
//            throw new RuntimeException("Could not get uniform location for tex_u");
//        }
        _vhandle = GLES20.glGetUniformLocation(_program, "tex_v");
        Log.i(TAG, "_vhandle = " + _vhandle);
//        checkGlError("glGetUniformLocation tex_v");
//        if (_vhandle == -1) {
//            throw new RuntimeException("Could not get uniform location for tex_v");
//        }

        _rgbhandle = GLES20.glGetUniformLocation(_program, "tex_rgb");
        Log.i(TAG, "_rgbhandle = " + _rgbhandle);
//        checkGlError("glGetUniformLocation tex_rgb");

        _rgbahandle = GLES20.glGetUniformLocation(_program, "tex_rgba");
        Log.i(TAG, "_rgbahandle = " + _rgbahandle);
//        checkGlError("glGetUniformLocation tex_rgba");

        isProgBuilt = true;
    }

    /**
     * build a set of textures, one for Y, one for U, and one for V.
     */
    public void buildTextures(Buffer y, Buffer u, Buffer v, int width, int height) {
        boolean videoSizeChanged = (width != _video_width || height != _video_height);
        if (videoSizeChanged) {
            _video_width = width;
            _video_height = height;
            Log.i(TAG, "buildTextures videoSizeChanged: w=" + _video_width + " h=" + _video_height);
        }

        // building texture for Y data
        if (_ytid < 0 || videoSizeChanged) {
            if (_ytid >= 0) {
                Log.i(TAG, "glDeleteTextures Y");
                GLES20.glDeleteTextures(1, new int[]{_ytid}, 0);
                checkGlError("glDeleteTextures");
            }
            // GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            checkGlError("glGenTextures");
            _ytid = textures[0];
            Log.i(TAG, "glGenTextures Y = " + _ytid);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _ytid);
        checkGlError("glBindTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, _video_width, _video_height, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);
        checkGlError("glTexImage2D");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // building texture for U data
        if (_utid < 0 || videoSizeChanged) {
            if (_utid >= 0) {
                Log.i(TAG, "glDeleteTextures U");
                GLES20.glDeleteTextures(1, new int[]{_utid}, 0);
                checkGlError("glDeleteTextures");
            }
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            checkGlError("glGenTextures");
            _utid = textures[0];
            Log.i(TAG, "glGenTextures U = " + _utid);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _utid);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, _video_width / 2, _video_height / 2, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // building texture for V data
        if (_vtid < 0 || videoSizeChanged) {
            if (_vtid >= 0) {
                Log.i(TAG, "glDeleteTextures V");
                GLES20.glDeleteTextures(1, new int[]{_vtid}, 0);
                checkGlError("glDeleteTextures");
            }
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            checkGlError("glGenTextures");
            _vtid = textures[0];
            Log.i(TAG, "glGenTextures V = " + _vtid);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _vtid);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, _video_width / 2, _video_height / 2, 0,
                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public void buildTexturesRGB(Buffer rgb, Buffer rgba, int width, int height) {
        boolean videoSizeChanged = (width != _video_width || height != _video_height);
        if (videoSizeChanged) {
            _video_width = width;
            _video_height = height;
            Log.i(TAG, "buildTextures videoSizeChanged: w=" + _video_width + " h=" + _video_height);
        }

        // building texture for rgba data
        if (_rgbtid < 0 || videoSizeChanged) {
            if (_rgbtid >= 0) {
                Log.i(TAG, "glDeleteTextures rgb");
                GLES20.glDeleteTextures(1, new int[]{_rgbtid}, 0);
                checkGlError("glDeleteTextures");
            }
            // GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            checkGlError("glGenTextures");
            _rgbtid = textures[0];
            Log.i(TAG, "glGenTextures rgb = " + _rgbtid);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _rgbtid);
        checkGlError("glBindTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, _video_width, _video_height, 0,
                GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, rgb);
        checkGlError("glTexImage2D");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);


        // building texture for rgba data
        if (_rgbatid < 0 || videoSizeChanged) {
            if (_rgbatid >= 0) {
                Log.i(TAG, "glDeleteTextures rgba");
                GLES20.glDeleteTextures(1, new int[]{_rgbatid}, 0);
                checkGlError("glDeleteTextures");
            }
            // GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            checkGlError("glGenTextures");
            _rgbatid = textures[0];
            Log.i(TAG, "glGenTextures rgba = " + _rgbatid);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _rgbatid);
        checkGlError("glBindTexture");
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, _video_width, _video_height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, rgba);
        checkGlError("glTexImage2D");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

        /**
         * render the frame
         * the YUV data will be converted to RGB by shader.
         */
    public void drawFrame() {
        GLES20.glUseProgram(_program);
        checkGlError("glUseProgram");

        GLES20.glVertexAttribPointer(_positionHandle, 2, GLES20.GL_FLOAT, false, 8, _vertice_buffer);
        checkGlError("glVertexAttribPointer mPositionHandle");
        GLES20.glEnableVertexAttribArray(_positionHandle);

        GLES20.glVertexAttribPointer(_coordHandle, 2, GLES20.GL_FLOAT, false, 8, _coord_buffer);
        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(_coordHandle);

        // bind textures
        GLES20.glActiveTexture(_textureI);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _ytid);
        GLES20.glUniform1i(_yhandle, _tIindex);

        GLES20.glActiveTexture(_textureII);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _utid);
        GLES20.glUniform1i(_uhandle, _tIIindex);

        GLES20.glActiveTexture(_textureIII);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _vtid);
        GLES20.glUniform1i(_vhandle, _tIIIindex);

        GLES20.glActiveTexture(_texture4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _rgbtid);
        GLES20.glUniform1i(_rgbhandle, _t4index);

        GLES20.glActiveTexture(_texture5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _rgbatid);
        GLES20.glUniform1i(_rgbahandle, _t5index);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFinish();

        GLES20.glDisableVertexAttribArray(_positionHandle);
        GLES20.glDisableVertexAttribArray(_coordHandle);
    }

    /**
     * create program and load shaders, fragment shader is very important.
     */
    public int createProgram(String vertexSource, String fragmentSource) {
        // create shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        // just check
        Log.i(TAG, "vertexShader = " + vertexShader);
        Log.i(TAG, "pixelShader = " + pixelShader);

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ", null);
                Log.e(TAG, GLES20.glGetProgramInfoLog(program), null);
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * create shader with given source.
     */
    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":", null);
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader), null);
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * these two buffers are used for holding vertices, screen vertices and texture vertices.
     */
    private void createBuffers(float[] vert, float[] coord) {
        _vertice_buffer = ByteBuffer.allocateDirect(vert.length * 4);
        _vertice_buffer.order(ByteOrder.nativeOrder());
        _vertice_buffer.asFloatBuffer().put(vert);
        _vertice_buffer.position(0);

        if (_coord_buffer == null) {
            _coord_buffer = ByteBuffer.allocateDirect(coord.length * 4);
            _coord_buffer.order(ByteOrder.nativeOrder());
            _coord_buffer.asFloatBuffer().put(coord);
            _coord_buffer.position(0);
        }
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "***** " + op + ": glError " + error, null);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private static float[] squareVertices = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f,}; // fullscreen
    private static float[] squareVertices1 = {-1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f,}; // left-top
    private static float[] squareVertices2 = {0.0f, -1.0f, 1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,}; // right-bottom
    private static float[] squareVertices3 = {-1.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,}; // left-bottom
    private static float[] squareVertices4 = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,}; // right-top

    private static float[] coordVertices = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,};// whole-texture

    private static final String VERTEX_SHADER = "attribute vec4 vPosition;\n"
            + "attribute vec2 a_texCoord;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "gl_Position = vPosition;\n"
            + "tc = a_texCoord;\n"
            + "}\n";

    /**
     * BT.601 YUV转 RGB
     * YCbCr => YUV
     * <p>
     * R = 1.164(Y-16)                 + 1.596(Cr-128)
     * G = 1.164(Y-16) - 0.391(Cb-128) - 0.813(Cr-128)
     * B = 1.164(Y-16) + 2.018(Cb-128)
     * <p>
     * 灰度图 = 0.299 * R + 0.578 * G + 0.114 * B
     */
    private static final String FRAGMENT_SHADER = "precision mediump float;\n"
            + "uniform sampler2D tex_y;\n"
            + "uniform sampler2D tex_u;\n"
            + "uniform sampler2D tex_v;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "vec4 Y = vec4(texture2D(tex_y, tc).r - 16./255.);\n"
            + "vec4 U = vec4(texture2D(tex_u, tc).r - 128./255.);\n"
            + "vec4 V = vec4(texture2D(tex_v, tc).r - 128./255.);\n"
            + "vec4 color;\n"
            + "color += Y * vec4(1.164, 1, 1, 1);\n"
            + "color += V * vec4(1.596, -0.813, 0, 0);\n"
            + "color += U * vec4(0, -0.392, 2.017, 0);\n"
            + "color.a = 1.0;\n"
            + "gl_FragColor = color;\n"
            + "}\n";

    /**
     * 与FRAGMENT_SHADER 等价
     */
    private static final String FRAGMENT_SHADER2 = "precision mediump float;\n"
            + "uniform sampler2D tex_y;\n"
            + "uniform sampler2D tex_u;\n"
            + "uniform sampler2D tex_v;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "float Y = texture2D(tex_y, tc).r - 16./255.;\n"
            + "float U = texture2D(tex_u, tc).r - 128./255.;\n"
            + "float V = texture2D(tex_v, tc).r - 128./255.;\n"
            + "float r = Y * 1.164 + V * 1.596;\n"
            + "float g = Y * 1.164 + U * -0.392 + V * -0.813;\n"
            + "float b = Y * 1.164 + U * 2.017;\n"
            + "vec4 color = vec4(r, g, b, 1.0);\n"
            + "gl_FragColor = color;\n"
            + "}\n";

    private static final String FRAGMENT_SHADER_GRAY = "precision mediump float;\n"
            + "uniform sampler2D tex_y;\n"
            + "uniform sampler2D tex_u;\n"
            + "uniform sampler2D tex_v;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "vec4 Y = vec4(texture2D(tex_y, tc).r - 16./255.);\n"
            + "vec4 U = vec4(texture2D(tex_u, tc).r - 128./255.);\n"
            + "vec4 V = vec4(texture2D(tex_v, tc).r - 128./255.);\n"
            + "vec4 color;\n"
            + "color += Y * vec4(1.164, 1, 1, 1);\n"
            + "color += V * vec4(1.596, -0.813, 0, 0);\n"
            + "color += U * vec4(0, -0.392, 2.017, 0);\n"
            + "color.a = 1.0;\n"
            + "float result = dot(color.rgb, vec3(0.2125, 0.7154, 0.0721));\n"
            + "gl_FragColor = vec4(vec3(result), 1.0);\n"
            + "}\n";

    private static final String FRAGMENT_SHADER_Y = "precision mediump float;\n"
            + "uniform sampler2D tex_y;\n"
            + "uniform sampler2D tex_u;\n"
            + "uniform sampler2D tex_v;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "vec4 Y = vec4(texture2D(tex_y, tc).r);\n"
            + "vec4 color = Y;\n"
            + "color.a = 1.0;\n"
            + "gl_FragColor = color;\n"
            + "}\n";

    private static final String FRAGMENT_SHADER_U = "precision mediump float;\n"
            + "uniform sampler2D tex_y;\n"
            + "uniform sampler2D tex_u;\n"
            + "uniform sampler2D tex_v;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "vec4 U = vec4(texture2D(tex_u, tc).r);\n"
            + "vec4 color = U;\n"
            + "color.a = 1.0;\n"
            + "gl_FragColor = color;\n"
            + "}\n";

    private static final String FRAGMENT_SHADER_V = "precision mediump float;\n"
            + "uniform sampler2D tex_y;\n"
            + "uniform sampler2D tex_u;\n"
            + "uniform sampler2D tex_v;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "vec4 V = vec4(texture2D(tex_v, tc).r);\n"
            + "vec4 color = V;\n"
            + "color.a = 1.0;\n"
            + "gl_FragColor = color;\n"
            + "}\n";

    private static final String FRAGMENT_SHADER_RGB = "precision mediump float;\n"
            + "uniform sampler2D tex_rgb;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "gl_FragColor = texture2D(tex_rgb, tc);\n"
            + "}\n";

    private static final String FRAGMENT_SHADER_RGBA = "precision mediump float;\n"
            + "uniform sampler2D tex_rgba;\n"
            + "varying vec2 tc;\n"
            + "void main() {\n"
            + "gl_FragColor = texture2D(tex_rgba, tc);\n"
            + "}\n";


}