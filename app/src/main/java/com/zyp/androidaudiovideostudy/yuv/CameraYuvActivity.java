package com.zyp.androidaudiovideostudy.yuv;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.zyp.androidaudiovideostudy.R;
import com.zyp.androidaudiovideostudy.util.CameraUtil;
import com.zyp.androidaudiovideostudy.util.ToastUtil;
import com.zyp.androidaudiovideostudy.yuv.util.YuvToFile;
import com.zyp.androidaudiovideostudy.yuv.util.YuvToImage;
import com.zyp.yuvlib.YuvLib;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraYuvActivity extends AppCompatActivity {

    private static final String TAG = CameraYuvActivity.class.getSimpleName();

    private SurfaceView surfaceView;

    private SurfaceHolder surfaceHolder;
    private YuvToFile yuvToFile = new YuvToFile();
    private YuvToImage yuvToImage = new YuvToImage();
    private volatile boolean isRecord;

    private String fileName = "camera_video.mp4";

    private CameraUtil cameraUtil = new CameraUtil();

    private YuvLib yuvLib = new YuvLib();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv_camera);

        surfaceView = findViewById(R.id.surfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);

        cameraUtil.init(this);
        cameraUtil.setPreviewCallback(previewCallback);

        findViewById(R.id.bt_start_record_yuv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.Size size = cameraUtil.camera.getParameters().getPreviewSize();
                int width = size.width;
                int height = size.height;
                String fileName = String.format("camera_%dx%d_yuv420p.yuv",height ,width);
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                boolean isExist = file.exists();
                if (isExist) {
                    file.delete();
                }
                isRecord = true;
                if (isExist) {
                    ToastUtil.INSTANCE.show("delete exist file and start record yuv");
                } else {
                    ToastUtil.INSTANCE.show("start record yuv");
                }
                //cameraUtil.startPreviewDisplay(surfaceHolder);
            }
        });

        findViewById(R.id.bt_stop_record_yuv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecord = false;
                ToastUtil.INSTANCE.show("stop record yuv");
                //cameraUtil.stopPreviewDisplay();
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

    private byte[] mI420Data;

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

    private void startPreviewFrame(byte[] data, Camera camera) {
        if (isRecord) {
            Log.d(TAG, "onPreviewFrame Record data: " + data.length);

            Camera.Size size = camera.getParameters().getPreviewSize();
            int width = size.width;
            int height = size.height;
            // yuv转换图片保存
            // yuvToImage.image(data, width, height);
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
            yuvToFile.append(data, String.format("camera_%dx%d_yuv420p.yuv", height, width));
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
            cameraUtil.lock(); // lock camera for later use
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecord = false;
        if (yuvToFile != null) {
            yuvToFile.close();
        }
        releaseMediaRecorder();
        cameraUtil.releaseCamera();
    }


    private boolean checkCameraHardware(Context context) {
        // 支持所有版本
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        //  Android 2.3 (API Level 9) 及以上的
        // return  Camera.getNumberOfCameras() > 0;
    }
}
