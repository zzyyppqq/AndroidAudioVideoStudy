package com.zyp.androidaudiovideostudy.gles;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

import com.zyp.androidaudiovideostudy.gles.filter.CameraFilter;
import com.zyp.androidaudiovideostudy.gles.filter.ScreenFilter;
import com.zyp.androidaudiovideostudy.gles.filter.TimeFilter;
import com.zyp.androidaudiovideostudy.util.CameraHelper;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2022/4/25 3:55 下午
 */
public class CameraOpenGLRenderer implements GLSurfaceView.Renderer {
    private CameraGLSurfaceView mGLSurfaceView;
    private int mWidth;
    private int mHeight;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private int[] mTextures;
    private CameraHelper mCameraHelper;

    private ScreenFilter mScreenFilter;
    private CameraFilter mCameraFilter;
    private TimeFilter timeFilter;

    private CameraMediaRecorder mCameraMediaRecorder;
    public CameraOpenGLRenderer(CameraGLSurfaceView cameraGLSurfaceView) {
        this.mGLSurfaceView = cameraGLSurfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Context context = mGLSurfaceView.getContext();
        Log.i("ZYP", "surfaceCreate()");
        mTextures = new int[1];
        GLES20.glGenTextures(1, mTextures, 0); // 创建一个纹理id
        //将纹理id传入成功创建SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.i("ZYP", "onFrameAvailable()");
                mGLSurfaceView.requestRender();
                Log.i("ZYP", "requestRender()");
            }
        });
        //这个是我的camera工具类
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCameraHelper.setPreviewCallback(mPreviewCallback);
        mCameraHelper.startPreview(mSurfaceTexture); //这个是设置相机的预览画面

        //注意：必须在gl线程操作opengl
        mCameraFilter = new CameraFilter(context);
        mScreenFilter = new ScreenFilter(context);
        timeFilter = new TimeFilter(context);

        //MediaRecorder
        mCameraMediaRecorder = new CameraMediaRecorder(context, CameraHelper.HEIGHT, CameraHelper.WIDTH);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        mCameraFilter.onReady(width, height);
        mScreenFilter.onReady(width, height);
        timeFilter.onReady(width, height);
        Log.i("ZYP", "surfaceChanged() " + width + ", " + height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        long time0 = SystemClock.elapsedRealtime();
        // 配置屏幕
        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        GLES20.glClearColor(0, 0, 0, 0);
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 把摄像头的数据先输出来
        // 更新纹理，然后我们才能够使用opengl从SurfaceTexure当中获得数据 进行渲染
        mSurfaceTexture.updateTexImage();
        //surfaceTexture 比较特殊，在opengl当中 使用的是特殊的采样器 samplerExternalOES （不是sampler2D）
        //获得变换矩阵
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMatrix(mtx);
        //责任链
        int id = mCameraFilter.onDrawFrame(mTextures[0]);
        //加效果滤镜
        // id  = 效果1.onDrawFrame(id);
        // id = 效果2.onDrawFrame(id);
        //....

        id = timeFilter.onDrawFrame(id);

        //加完之后再显示到屏幕中去
        mScreenFilter.onDrawFrame(id);

        //进行录制
        mCameraMediaRecorder.encodeFrame(id, mSurfaceTexture.getTimestamp());
    }

    public void startRecord(String path, float speed) {
        try {
            mCameraMediaRecorder.start(path, speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        mCameraMediaRecorder.stop();
    }


    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // 相机预览数据，可做人脸识别
        }
    };

    public void onSurfaceDestroyed() {
        mCameraHelper.stopPreview();
    }

}
