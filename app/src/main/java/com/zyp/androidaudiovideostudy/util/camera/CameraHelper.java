package com.zyp.androidaudiovideostudy.util.camera;

import static android.view.OrientationEventListener.ORIENTATION_UNKNOWN;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CameraHelper implements Camera.PreviewCallback {

    private static final String TAG = "CameraHelper";
    private int mCameraId;
    private int mRotation;
    private int mDisplayOrientation;
    private Camera mCamera;
    public Camera.Size previewSize;
    private byte[] buffer;
    private Camera.PreviewCallback mPreviewCallback;
    private SurfaceTexture mSurfaceTexture;
    private CameraListener cameraListener;

    /**
     * 屏幕的长宽，在选择最佳相机比例时用到
     */
    private Point previewViewSize;
    /**
     * 指定的预览宽高，若系统支持则会以这个预览宽高进行预览
     */
    private Point specificPreviewSize;
    /**
     * 额外的旋转角度（用于适配一些定制设备）
     */
    private int additionalRotation;

    /**
     * 指定的相机ID
     */
    private Integer specificCameraId = null;

    public CameraHelper(int cameraId, int rotation) {
        specificCameraId = cameraId;
        mRotation = rotation;
    }

    public void startPreview(SurfaceTexture surfaceTexture, int width, int height) {  //这里使用从外面传过来的surfacetextture
        synchronized (this) {
            mSurfaceTexture = surfaceTexture;
            previewViewSize = new Point(width, height);
            Log.d(TAG, "startPreview width: " + width + ", height: " + height);
            try {
                if (mCamera != null) {
                    return;
                }
                //相机数量为2则打开1,1则打开0,相机ID 1为前置，0为后置
                mCameraId = Camera.getNumberOfCameras() - 1;
                //若指定了相机ID且该相机存在，则打开指定的相机
                if (specificCameraId != null && specificCameraId <= mCameraId) {
                    mCameraId = specificCameraId;
                }
                //没有相机
                if (mCameraId == -1) {
                    if (cameraListener != null) {
                        cameraListener.onCameraError(new Exception("camera not found"));
                    }
                    return;
                }
                //获得camera对象
                if (mCamera == null) {
                    mCamera = Camera.open(mCameraId);
                }
                mDisplayOrientation = getCameraDisplayOrientation(mCameraId, mRotation);
                mCamera.setDisplayOrientation(mDisplayOrientation);
                Log.d(TAG, "startPreview mRotation: " + mRotation + ", mDisplayOrientation: " + mDisplayOrientation);
                //配置camera的属性
                Camera.Parameters parameters = mCamera.getParameters();
                //设置预览数据格式为nv21
                parameters.setPreviewFormat(ImageFormat.NV21);
                previewSize = parameters.getPreviewSize();
                Log.d(TAG, "previewSize: width=" + previewSize.width + " , height=" + previewSize.height);
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                if (supportedPreviewSizes != null && supportedPreviewSizes.size() > 0) {
                previewSize = getBestSupportedSize(supportedPreviewSizes, previewViewSize);
                    Log.d(TAG, "best previewSize: width=" + previewSize.width + " , height=" + previewSize.height);
                    StringBuilder sb = new StringBuilder();
                    for (Camera.Size size : supportedPreviewSizes) {
                        sb.append("width=" + size.width + " height=" + size.height).append("\r\n");
                    }
                    Log.d(TAG, "support size: \r\n" + sb);
                }
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                //对焦模式设置
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
                    if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                }

                // 设置摄像头 图像传感器的角度、方向
                mCamera.setParameters(parameters);
                buffer = new byte[previewSize.width * previewSize.height * 3 / 2];
                //数据缓存区
                mCamera.addCallbackBuffer(buffer);
                mCamera.setPreviewCallbackWithBuffer(this);
                //设置预览画面
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
                if (cameraListener != null) {
                    cameraListener.onCameraOpened(mCamera, mCameraId, mDisplayOrientation);
                }
            } catch (Exception e) {
                if (cameraListener != null) {
                    cameraListener.onCameraError(e);
                }
            }
        }
    }


    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, Point previewViewSize) {
        if (sizes == null || sizes.size() == 0) {
            return mCamera.getParameters().getPreviewSize();
        }
        Camera.Size[] tempSizes = sizes.toArray(new Camera.Size[0]);
        Arrays.sort(tempSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                if (o1.width > o2.width) {
                    return -1;
                } else if (o1.width == o2.width) {
                    return o1.height > o2.height ? -1 : 1;
                } else {
                    return 1;
                }
            }
        });
        sizes = Arrays.asList(tempSizes);

        Camera.Size bestSize = sizes.get(0);
        float previewViewRatio;
        if (previewViewSize != null) {
            previewViewRatio = (float) previewViewSize.x / (float) previewViewSize.y;
        } else {
            previewViewRatio = (float) bestSize.width / (float) bestSize.height;
        }

        if (previewViewRatio > 1) {
            previewViewRatio = 1 / previewViewRatio;
        }
        boolean isNormalRotate = (additionalRotation % 180 == 0);

        for (Camera.Size s : sizes) {
            if (specificPreviewSize != null && specificPreviewSize.x == s.width && specificPreviewSize.y == s.height) {
                return s;
            }
            if (isNormalRotate) {
                if (Math.abs((s.height / (float) s.width) - previewViewRatio) < Math.abs(bestSize.height / (float) bestSize.width - previewViewRatio)) {
                    bestSize = s;
                }
            } else {
                if (Math.abs((s.width / (float) s.height) - previewViewRatio) < Math.abs(bestSize.width / (float) bestSize.height - previewViewRatio)) {
                    bestSize = s;
                }
            }
        }
        return bestSize;
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public List<Camera.Size> getSupportedPictureSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPictureSizes();
    }

    /**
     * 预览显示适配
     * GOOGLE建议的方法
     *
     * @param cameraId
     * @param rotation int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
     * @param camera
     */
    public int getCameraDisplayOrientation(int cameraId, int rotation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * 拍照保存图片适配
     * 一般有两种方法：
     * 1、使用setRotation直接旋转拍照帧
     * 2、读取拍照帧的exif中的orientation信息进行旋转
     *
     * @param orientation
     * @param parameters
     */
    public void onOrientationChanged(int orientation, Camera.Parameters parameters) {
        if (orientation == ORIENTATION_UNKNOWN) {
            return;
        }
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraId, info);
        orientation = (orientation + 45) / 90 * 90;
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        parameters.setRotation(rotation);
    }

    /**
     * 以通过从exif中读取orientation信息，再对图片进行旋转
     * ExifInterface.TAG_ORIENTATION
     * 一些android机型Camera拍摄帧本身不进行旋转，而将旋转角度写入exif中。
     * EXIF：可交换图像文件格式（英语：Exchangeable image file format，官方简称Exif）， 是专门为数码相机的照片设定的，可以记录数码照片的属性信息和拍摄数据。
     * 只有JPEG格式的图片才会携带exif数据，像PNG，WebP这类的图片就不会有这些数据。
     * <p>
     * 链接：https://juejin.cn/post/6990174702186528799
     *
     * @return
     */
    public Bitmap getOrientationFromExif(String path, BitmapFactory.Options options) {
        Matrix mat = new Matrix();
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void switchCamera(int cameraId, int width, int height) {
        if (cameraId == specificCameraId) {
            return;
        }
        specificCameraId = cameraId;
        stopPreview();
        startPreview(mSurfaceTexture, width, height);
    }

    public void switchCamera(int width, int height) {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview(mSurfaceTexture, width, height);
    }

    public int getCameraId() {
        return mCameraId;
    }

    public Camera.Size getPreviewSize() {
        return previewSize;
    }

    public void lock() {
        mCamera.lock();
    }

    public void unlock() {
        mCamera.unlock();
    }

    public void setCameraListener(CameraListener cameraListener) {
        this.cameraListener = cameraListener;
    }

    public void changeDisplayOrientation(int rotation) {
        if (mCamera != null) {
            this.mRotation = rotation;
            mDisplayOrientation = getCameraDisplayOrientation(mCameraId, rotation);
            mCamera.setDisplayOrientation(mDisplayOrientation);
            if (cameraListener != null) {
                cameraListener.onCameraConfigurationChanged(mCameraId, mDisplayOrientation);
            }
        }
    }

    /**
     * onStop()时调用
     */
    public void stopPreview() {
        synchronized (this) {
            if (mCamera != null) {
                //预览数据回调接口
                mCamera.setPreviewCallback(null);
                //停止预览
                mCamera.stopPreview();
                //释放摄像头
                mCamera.release();
                mCamera = null;
                if (cameraListener != null) {
                    cameraListener.onCameraClosed();
                }
            }
        }
    }

    public boolean isStopped() {
        synchronized (this) {
            return mCamera == null;
        }
    }


    /**
     * onDestory()时调用
     */
    public void release() {
        synchronized (this) {
            stopPreview();
            mSurfaceTexture = null;
            specificCameraId = null;
            cameraListener = null;
            previewViewSize = null;
            specificPreviewSize = null;
            previewSize = null;
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // data数据依然是倒的
        if (null != mPreviewCallback) {
            mPreviewCallback.onPreviewFrame(data, camera);
        }
        camera.addCallbackBuffer(buffer);
    }

}