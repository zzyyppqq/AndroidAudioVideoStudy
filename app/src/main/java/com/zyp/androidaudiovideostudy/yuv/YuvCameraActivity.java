package com.zyp.androidaudiovideostudy.yuv;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;

import com.zyp.androidaudiovideostudy.base.BaseActivity;

import android.os.Bundle;
import android.os.WorkSource;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.zyp.androidaudiovideostudy.R;
import com.zyp.androidaudiovideostudy.util.CameraUtil;
import com.zyp.androidaudiovideostudy.util.ToastUtil;
import com.zyp.androidaudiovideostudy.yuv.util.RgbToBitmap;
import com.zyp.androidaudiovideostudy.yuv.util.FileSaver;
import com.zyp.androidaudiovideostudy.yuv.util.YuvImageUtil;
import com.zyp.yuvlib.YuvLib;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YuvCameraActivity extends BaseActivity {

    private static final String TAG = YuvCameraActivity.class.getSimpleName();

    private SurfaceView surfaceView;
    private ImageView ivFrame;

    private SurfaceHolder surfaceHolder;
    private FileSaver fileSaver = new FileSaver();
    private YuvImageUtil yuvImageUtil = new YuvImageUtil();
    private volatile boolean isRecord;

    private String fileName = "camera_video.mp4";

    private CameraUtil cameraUtil = new CameraUtil();

    private YuvLib yuvLib = new YuvLib();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv_camera);

        surfaceView = findViewById(R.id.surfaceView);
        ivFrame = findViewById(R.id.iv_frame);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);

        cameraUtil.init(this);
        cameraUtil.setPreviewCallback(previewCallback);

        findViewById(R.id.bt_start_record_yuv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile();
                isRecord = true;
            }
        });

        findViewById(R.id.bt_stop_record_yuv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecord = false;
                ToastUtil.INSTANCE.show("stop record yuv");
            }
        });


        findViewById(R.id.bt_start_media_record_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareVideoRecorder();
                mMediaRecorder.start();
            }
        });

        findViewById(R.id.bt_stop_media_record_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                cameraUtil.lock();      // take camera access back from MediaRecorder
            }
        });
    }

    int scaleWidth = 720, scaleHeight = 1280;
    int cropWidth = 720, cropHeight = 720;
    private void deleteFile() {
        Camera.Size size = cameraUtil.camera.getParameters().getPreviewSize();
        int width = size.width;
        int height = size.height;
        File yuvFile = new File(Environment.getExternalStorageDirectory(), String.format("camera_%dx%d_yuv420p.yuv",height ,width));
        new File(Environment.getExternalStorageDirectory(),String.format("camera_%dx%d_rgb.rgb",width ,height)).delete();
        new File(Environment.getExternalStorageDirectory(),String.format("camera_%dx%d_rgba.rgb",width ,height)).delete();
        new File(Environment.getExternalStorageDirectory(),String.format("camera_%dx%d_rgb.rgb",scaleWidth ,scaleHeight)).delete();
        new File(Environment.getExternalStorageDirectory(),String.format("camera_%dx%d_rgba.rgb",scaleWidth ,scaleHeight)).delete();
        new File(Environment.getExternalStorageDirectory(),String.format("camera_%dx%d_rgb.rgb",cropWidth ,cropHeight)).delete();
        new File(Environment.getExternalStorageDirectory(),String.format("camera_%dx%d_rgba.rgb",cropWidth ,cropHeight)).delete();
        if (yuvFile.exists()) {
            ToastUtil.INSTANCE.show("delete exist file and start record yuv");
            yuvFile.delete();
        } else {
            ToastUtil.INSTANCE.show("start record yuv");
        }
    }


    private ExecutorService executors = Executors.newSingleThreadExecutor();

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // Main
            executors.execute(new Runnable() {
                @Override
                public void run() {
                    if (data != null) {
                        // data.length = width * height * 3/2
                        Log.d(TAG, "onPreviewFrame data: " + data + ", data size: " + data.length);
                        startPreviewFrame(data, camera);
                    }
                }
            });
        }
    };

    private boolean isConvertBitmap = true;
    private FileSaver rgbaToFile = new FileSaver();
    private FileSaver rgbToFile = new FileSaver();
    private FileSaver rgbScaleToFile = new FileSaver();
    private FileSaver rgbaScaleToFile = new FileSaver();
    private FileSaver rgbCropToFile = new FileSaver();
    private FileSaver rgbaCropToFile = new FileSaver();
    private void closeStream() {
        if (fileSaver != null) {
            fileSaver.close();
        }
        if (rgbaToFile != null) {
            rgbaToFile.close();
        }
        if (rgbToFile != null) {
            rgbToFile.close();
        }
        if (rgbScaleToFile != null) {
            rgbScaleToFile.close();
        }
        if (rgbaScaleToFile != null) {
            rgbaScaleToFile.close();
        }
        if (rgbCropToFile != null) {
            rgbCropToFile.close();
        }
        if (rgbaCropToFile != null) {
            rgbaCropToFile.close();
        }
    }


    private byte[] mI420Data;
    private byte[] mRGBData;
    private byte[] mRGBAData;
    private byte[] mScaleData;
    private byte[] mScaleRGBData;
    private byte[] mScaleRGBAData;
    private byte[] mCropData;
    private byte[] mCropRGBData;
    private byte[] mCropRGBAData;

    private void startPreviewFrame(byte[] data, Camera camera) {
        if (isRecord) {
            Log.d(TAG, "onPreviewFrame Record data: " + data.length);

            Camera.Size size = camera.getParameters().getPreviewSize();
            int width = size.width;
            int height = size.height;
            // yuv转换图片保存
            // yuvImageUtil.image(data, width, height);
            // yuv旋转90度
            // data = YuvRotate.rotateYUVDegree90(data, width, height);

            if (mI420Data == null || mI420Data.length != data.length) {
                mI420Data = new byte[data.length];
            }

            yuvLib.nv21ToI420(data, mI420Data, width, height);

            if (cameraUtil.cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                yuvLib.rotateI420(mI420Data, data, width, height, 90);
            } else {
                yuvLib.rotateI420(mI420Data, data, width, height, 270);
            }
            // 旋转后名称宽高要互换
            fileSaver.append(data, String.format("camera_%dx%d_yuv420p.yuv", height, width));

            if (isConvertBitmap) {
                isConvertBitmap = false;
                if (mRGBData == null) {
                    mRGBData = new byte[width * height * 3];
                }
                if (mRGBAData == null) {
                    mRGBAData = new byte[width * height * 4];
                }
                yuvLib.i420ToRGB24(data, mRGBData, height, width);
                /** libyuv中，rgba表示abgr abgr abgr这样的顺序写入文件，java使用的时候习惯rgba表示rgba rgba rgba写入文件 */
                yuvLib.i420ToABGR(data, mRGBAData, height, width);
                rgbToFile.append(mRGBData, String.format("camera_%dx%d_rgb.rgb", height, width));
                rgbaToFile.append(mRGBAData, String.format("camera_%dx%d_rgba.rgb", height, width));
                Bitmap originBitmap = RgbToBitmap.rgbaToBitmap(mRGBAData, height, width);
                // 缩放实现
                if (mScaleData == null) {
                    mScaleData = new byte[scaleWidth * scaleHeight * 3 / 2];
                }
                if (mScaleRGBData == null) {
                    mScaleRGBData = new byte[scaleWidth * scaleHeight * 3];
                }
                if (mScaleRGBAData == null) {
                    mScaleRGBAData = new byte[scaleWidth * scaleHeight * 4];
                }
                yuvLib.i420ToScale(data, height, width, mScaleData, scaleWidth, scaleHeight);
                yuvLib.i420ToRGB24(mScaleData, mScaleRGBData, scaleWidth, scaleHeight);
                yuvLib.i420ToABGR(mScaleData, mScaleRGBAData, scaleWidth, scaleHeight);

                rgbScaleToFile.append(mScaleRGBData, String.format("camera_%dx%d_rgb.rgb", scaleWidth, scaleHeight));
                rgbaScaleToFile.append(mScaleRGBAData, String.format("camera_%dx%d_rgba.rgb", scaleWidth, scaleHeight));
                Bitmap scaleBitmap = RgbToBitmap.rgbaToBitmap(mScaleRGBAData, scaleWidth, scaleHeight);
                // 裁剪实现
                if (mCropData == null) {
                    mCropData = new byte[cropWidth * cropHeight * 3 / 2];
                }
                if (mCropRGBData == null) {
                    mCropRGBData = new byte[cropWidth * cropHeight * 3];
                }
                if (mCropRGBAData == null) {
                    mCropRGBAData = new byte[cropWidth * cropHeight * 4];
                }
                // 用缩放的data去裁剪
                yuvLib.i420ToCrop(mScaleData, scaleWidth, scaleHeight, mCropData, cropWidth, cropHeight, 0, 0);
                yuvLib.i420ToRGB24(mCropData, mCropRGBData, cropWidth, cropHeight);
                yuvLib.i420ToABGR(mCropData, mCropRGBAData, cropWidth, cropHeight);

                rgbCropToFile.append(mCropRGBData, String.format("camera_%dx%d_rgb.rgb", cropWidth, cropHeight));
                rgbaCropToFile.append(mCropRGBAData, String.format("camera_%dx%d_rgba.rgb", cropWidth, cropHeight));
                Bitmap cropBitmap = RgbToBitmap.rgbaToBitmap(mCropRGBAData, cropWidth, cropHeight);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivFrame.setImageBitmap(cropBitmap);
                    }
                });
            }
        }
    }


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated: ");
            cameraUtil.startPreviewDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged: ");
            cameraUtil.stopPreviewDisplay();
            cameraUtil.startPreviewDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed: ");
            cameraUtil.stopPreviewDisplay();
        }
    };


    private MediaRecorder mMediaRecorder;

    private void prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        cameraUtil.unlock();
        mMediaRecorder.setCamera(cameraUtil.camera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile profile = null;
//        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA)){
//            profile =CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
//        }else {
        profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        }
        profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        profile.videoCodec = MediaRecorder.VideoEncoder.H264;
        Log.d(TAG, "prepareVideoRecorder: " + profile.videoBitRate + " , " + profile.audioBitRate);
//        profile.videoBitRate = VideoMakerConfig.VIDEO_BIT;
//        profile.audioBitRate = VideoMakerConfig.AUDIO_BIT;

        mMediaRecorder.setProfile(profile);

        mMediaRecorder.setMaxDuration(10000);
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

                }
            }
        });

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            if (cameraUtil.cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mMediaRecorder.setOrientationHint(270);
            } else {
                mMediaRecorder.setOrientationHint(90);
            }
        } else {
            mMediaRecorder.setOrientationHint(0);
        }

        //设置视频保存到文件
        File fileDemo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        if (!fileDemo.exists()) {
            if (!fileDemo.mkdirs()) {
                return;
            }
        }
        File videoFile = new File(Environment.getExternalStorageDirectory(), fileName);
        mMediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        //将视频显示到SurfaceView上
        mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecord = false;
        closeStream();
        releaseMediaRecorder();
        surfaceHolder.removeCallback(callback);
        cameraUtil.releaseCamera();
    }


    private boolean checkCameraHardware(Context context) {
        // 支持所有版本
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        //  Android 2.3 (API Level 9) 及以上的
        // return  Camera.getNumberOfCameras() > 0;
    }
}
