package com.zyp.av.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * 描述：Camera Util
 * 作者：@author alex
 * 创建时间：2022/4/24 8:37 下午
 */
public class CameraUtil {
    private static final String TAG = CameraUtil.class.getSimpleName();
    public int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    public Camera camera;
    public Camera.Size previewSize;

    public void init(Context context) {
        camera = Camera.open(cameraId);//后置
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            //在exif数据中，旋转90°
            parameters.setRotation(90);
        } else {
            parameters.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            //在exif数据中，旋转0°
            parameters.setRotation(0);
        }
        previewSize = parameters.getPreviewSize();
        Log.d(TAG, "onCreate: cur previewSize: width=" + previewSize.width + " , height=" + previewSize.height);
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : supportedPreviewSizes) {
            Log.d(TAG, "onCreate: support size: width=" + size.width + " , height=" + size.height);
        }
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        List<String> focusMode = parameters.getSupportedFocusModes();
        if (focusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.cancelAutoFocus();
        }
        camera.setParameters(parameters);
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        camera.setPreviewCallback(previewCallback);
    }

    public void startPreviewDisplay(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPreviewDisplay() {
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lock() {
        camera.lock();
    }

    public void unlock() {
        camera.unlock();
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.lock(); // lock camera for later use
            camera.release();
            camera = null;
        }
    }
}
