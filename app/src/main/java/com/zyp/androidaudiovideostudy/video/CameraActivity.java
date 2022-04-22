package com.zyp.androidaudiovideostudy.video;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = CameraActivity.class.getSimpleName();

    private SurfaceView surfaceView;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private YuvToFile yuvToFile;
    private volatile boolean isRecord;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        surfaceView = findViewById(R.id.surfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);

        yuvToFile = new YuvToFile();
        camera = Camera.open(cameraId);//后置

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);


        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
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

        final Camera.Size previewSize = parameters.getPreviewSize();
        Log.d(TAG, "onCreate: cur previewSize: width=" + previewSize.width + " , height=" + previewSize.height);
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : supportedPreviewSizes) {
            Log.d(TAG, "onCreate: support size: width=" + size.width + " , height=" + size.height);
        }
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        final YuvToImage yuvToImage = new YuvToImage();
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (isRecord) {
                    Log.d(TAG, "onPreviewFrame: data: " + data.length);
//                    yuvToImage.image(data,previewSize.width,previewSize.height);
                    data = YuvRotate.rotateYUVDegree90(data, previewSize.width, previewSize.height);
                    yuvToFile.append(data, "aaa" + timeStamp + ".yuv");
                }

            }
        });


        List<String> focusMode = parameters.getSupportedFocusModes();
        if (focusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.cancelAutoFocus();
        }

        camera.setParameters(parameters);

        findViewById(R.id.bt_start_record_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecord = true;
                startPreviewDisplay(surfaceHolder);
            }
        });

        findViewById(R.id.bt_stop_record_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecord = false;
                camera.stopPreview();
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
                camera.lock();         // take camera access back from MediaRecorder

            }
        });



    }


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated: ");
            startPreviewDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged: ");
            try {
                camera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

            startPreviewDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed: ");

        }
    };

    private void startPreviewDisplay(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private MediaRecorder mMediaRecorder;

    private void prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        camera.unlock();
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile profile = null;
//        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA)){
//            profile =CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
//        }else {
            profile =CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        }
        profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        profile.videoCodec = MediaRecorder.VideoEncoder.H264;
        Log.d(TAG, "prepareVideoRecorder: "+profile.videoBitRate+" , "+profile.audioBitRate);
//        profile.videoBitRate = VideoMakerConfig.VIDEO_BIT;
//        profile.audioBitRate = VideoMakerConfig.AUDIO_BIT;

        mMediaRecorder.setProfile(profile);

        mMediaRecorder.setMaxDuration(10000);
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){

                }
            }
        });

        if (getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE){
            if (cameraId==Camera.CameraInfo.CAMERA_FACING_FRONT){
                mMediaRecorder.setOrientationHint(270);
            }else {
                mMediaRecorder.setOrientationHint(90);
            }
        }else {
            mMediaRecorder.setOrientationHint(0);
        }

        //设置视频保存到文件
        File fileDemo=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        if (!fileDemo.exists()){
            if (!fileDemo.mkdirs()){
                return;
            }
        }
        File videoFile=new File(Environment.getExternalStorageDirectory(),"aaa"+System.currentTimeMillis()+"_myVideo.mp4");
        mMediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        //将视频显示到SurfaceView上
        mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        try {
            mMediaRecorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            if (camera != null) {
                camera.lock();
            }// lock camera for later use
        }
    }

    private void releaseCamera(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (yuvToFile != null) {
            yuvToFile.close();
        }
        releaseMediaRecorder();
        releaseCamera();

    }


    private boolean checkCameraHardware(Context context) {
        // 支持所有版本
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        //  Android 2.3 (API Level 9) 及以上的
        // return  Camera.getNumberOfCameras() > 0;
    }
}
