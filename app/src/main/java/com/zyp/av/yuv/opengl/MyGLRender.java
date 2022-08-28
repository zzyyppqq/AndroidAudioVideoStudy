package com.zyp.av.yuv.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyGLRender implements GLSurfaceView.Renderer {
    static final String TAG = "MyGLRender";

    public static final int YUV_TYPE = 0;
    public static final int Y_TYPE = 1;
    public static final int U_TYPE = 2;
    public static final int V_TYPE = 3;
    public static final int YUV_GRAY_TYPE = 4;
    public static final int RGB_TYPE = 5;
    public static final int RGBA_TYPE = 6;

    private GLSurfaceView mTargetSurface;
    private GLProgram prog = new GLProgram(1);
    private int mVideoWidth = -1, mVideoHeight = -1;
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;
    private ByteBuffer rgb;
    private ByteBuffer rgba;

    private int mYUVType = YUV_TYPE;

    public MyGLRender(GLSurfaceView surface) {
        mTargetSurface = surface;
    }

    public void updateProgram(int yuvType) {
        this.mYUVType = yuvType;
        prog.buildProgram(mYUVType);
        Log.d(TAG, "GLFrameRenderer :: buildProgram done");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "GLFrameRenderer :: onSurfaceCreated");
        if (!prog.isProgramBuilt()) {
            prog.buildProgram(mYUVType);
            Log.d(TAG, "GLFrameRenderer :: buildProgram done");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "GLFrameRenderer :: onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (y != null) {
                // reset position, have to be done
                y.position(0);
                u.position(0);
                v.position(0);
                prog.buildTextures(y, u, v, mVideoWidth, mVideoHeight);
                rgb.position(0);
                rgba.position(0);
                v.position(0);
                prog.buildTexturesRGB(rgb, rgba, mVideoWidth, mVideoHeight);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                prog.drawFrame();
            }
        }
    }

    /**
     * this method will be called from native code, it happens when the video is about to play or
     * the video size changes.
     */
    public void update(int w, int h) {
        Log.d(TAG, "update window size=> w:" + w + " h:" + h);
        if (w > 0 && h > 0) {
            if (w != mVideoWidth && h != mVideoHeight) {
                this.mVideoWidth = w;
                this.mVideoHeight = h;
                int yarraySize = w * h;
                int uvarraySize = yarraySize / 4;
                synchronized (this) {
                    y = ByteBuffer.allocate(yarraySize);
                    u = ByteBuffer.allocate(uvarraySize);
                    v = ByteBuffer.allocate(uvarraySize);
                    rgb = ByteBuffer.allocate(w * h * 3);
                    rgba = ByteBuffer.allocate(w * h * 4);
                }
            }
        }
    }

    /**
     * this method will be called from native code, it's used for passing yuv data to me.
     */
    public void update(byte[] ydata, byte[] udata, byte[] vdata) {
        synchronized (this) {
            if (ydata != null) {
                y.clear();
                y.put(ydata, 0, ydata.length);
            }
            if (udata != null) {
                u.clear();
                u.put(udata, 0, udata.length);
            }
            if (vdata != null) {
                v.clear();
                v.put(vdata, 0, vdata.length);
            }
        }

        // request to render
        mTargetSurface.requestRender();
    }

    /**
     * this method is for update yuv420 data
     */
    public void update(byte[] yuvdata) {
        synchronized (this) {
            int ylen = this.mVideoHeight * this.mVideoWidth;
            y.clear();
            u.clear();
            v.clear();
            y.put(yuvdata, 0, ylen);
            u.put(yuvdata, ylen, ylen / 4);
            v.put(yuvdata, ylen * 5 / 4, ylen / 4);
        }

        // request to render
        mTargetSurface.requestRender();
    }

    /**
     * this method is for update yuv420 data
     */
    public void updateRGB(byte[] rgbdata) {
        synchronized (this) {
            int rgbLen = this.mVideoHeight * this.mVideoWidth * 3;
            rgb.clear();
            rgb.put(rgbdata, 0, rgbLen);
        }
        // request to render
        mTargetSurface.requestRender();
    }

    /**
     * this method is for update yuv420 data
     */
    public void updateRGBA(byte[] rgbadata) {
        synchronized (this) {
            int rgbaLen = this.mVideoHeight * this.mVideoWidth * 4;
            rgba.clear();
            rgba.put(rgbadata, 0, rgbaLen);
        }

        // request to render
        mTargetSurface.requestRender();
    }

}
