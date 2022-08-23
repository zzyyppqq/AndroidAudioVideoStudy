package com.zyp.androidaudiovideostudy.gles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;


public class CameraGLSurfaceView extends GLSurfaceView {

    private CameraOpenGLRenderer cameraRenderer;

    public CameraGLSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 配置GLSurfaceView
     */
    public void init(int rotation) {
        //设置EGL版本
        setEGLContextClientVersion(2);
        cameraRenderer = new CameraOpenGLRenderer(this, rotation);
        setRenderer(cameraRenderer);
        //设置按需渲染 当我们调用 requestRender 请求GLThread 回调一次 onDrawFrame
        // 连续渲染 就是自动的回调onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        cameraRenderer.onSurfaceDestroyed();
    }


    public void startRecord(String path, float speed) {
        cameraRenderer.startRecord(path, speed);
    }

    public void stopRecord() {
        cameraRenderer.stopRecord();
    }

}
