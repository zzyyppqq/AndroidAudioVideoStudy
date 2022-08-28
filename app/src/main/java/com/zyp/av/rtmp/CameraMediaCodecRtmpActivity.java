package com.zyp.av.rtmp;

import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.PREVIEW_FPS_MAX_INDEX;
import static android.hardware.Camera.Parameters.PREVIEW_FPS_MIN_INDEX;
import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.zyp.av.R;
import com.zyp.rtmplib.RtmpHandle;
import com.zyp.rtmplib.flv.FlvPacker;
import com.zyp.rtmplib.flv.Packer;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraMediaCodecRtmpActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "CameraMediaCodecRtmpActivity";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    //采集到每帧数据时间
    long previewTime = 0;
    //每帧开始编码时间
    long encodeTime = 0;
    //采集数量
    int count = 0;
    //编码数量
    int encodeCount = 0;

    private MediaCodec mMediaCodec;
    private static final String VCODEC_MIME = "video/avc";
    private final String DATA_DIR = Environment.getExternalStorageDirectory() + File.separator + "AndroidVideo";
    private FlvPacker mFlvPacker;
    private final int FRAME_RATE = 15;
    private OutputStream mOutStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_rtmp);
        init();
        int version = RtmpHandle.INSTANCE.getVersion();
        Log.i(TAG, "rmtp version(16进制): " + Integer.toHexString(version));
    }

    ExecutorService pushExecutor = Executors.newSingleThreadExecutor();
    private SurfaceHolder mSurfaceHolder;
    private void init() {
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceView.setKeepScreenOn(true);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void initFlvPacker() {
        if (mFlvPacker != null) {
            return;
        }
        mFlvPacker = new FlvPacker();
        mFlvPacker.initVideoParams(previewSize.width, previewSize.height, FRAME_RATE);
        mFlvPacker.setPacketListener(new Packer.OnPacketListener() {
            @Override
            public void onPacket(final byte[] data, final int packetType) {
                pushExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        int ret = RtmpHandle.INSTANCE.push(data, data.length);
                        Log.w(TAG, "type：" + packetType + "  length:" + data.length + "  推流结果:" + ret);
                    }
                });
            }
        });
    }

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Camera.PreviewCallback mPreviewCallback =  new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {
            long endTime = System.currentTimeMillis();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    encodeTime = System.currentTimeMillis();
                    flvPackage(data);
                    Log.w(TAG, "编码第:" + (encodeCount++) + "帧，耗时:" + (System.currentTimeMillis() - encodeTime));
                }
            });
            Log.d(TAG, "采集第:" + (++count) + "帧，距上一帧间隔时间:"
                    + (endTime - previewTime) + "  " + Thread.currentThread().getName());
            previewTime = endTime;
        }
    };

    private Camera mCamera;
    private Camera.Size previewSize;
    private void initCamera() {
        openCamera();
        setParameters();
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mCamera.addCallbackBuffer(new byte[calculateLength(ImageFormat.NV21)]);
        mCamera.setPreviewCallback(mPreviewCallback);

        initMediaCodec();
        initFlvPacker();
    }


    private void openCamera() throws RuntimeException {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                Log.e(TAG, "摄像头打开失败");
                e.printStackTrace();
                Toast.makeText(this, "摄像头不可用!", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                }
                throw new RuntimeException(e);
            }
        }
    }

    private int calculateLength(int format) {
        return previewSize.width * previewSize.height
                * ImageFormat.getBitsPerPixel(format) / 8;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
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
        camera.setDisplayOrientation(result);
    }

    private void setParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);

        // Set preview size.
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : supportedPreviewSizes) {
            if (size.width >= 240 && size.width <= 680) {
                previewSize = size;
                Log.d(TAG, "select preview size width=" + size.width + ",height=" + size.height);
                break;
            }
        }
        previewSize.width = 480;
        previewSize.height = 320;
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        int defFps = 20 * 1000;
        int[] dstRange = {defFps, defFps};

        //set fps range.
        List<int[]> supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();
        for (int[] fps : supportedPreviewFpsRange) {
            if (fps[PREVIEW_FPS_MAX_INDEX] > defFps && fps[PREVIEW_FPS_MIN_INDEX] < defFps) {
                dstRange = fps;
                Log.d(TAG, "find fps:" + Arrays.toString(dstRange));

                break;
            }
        }
        parameters.setPreviewFpsRange(dstRange[PREVIEW_FPS_MIN_INDEX],
                dstRange[PREVIEW_FPS_MAX_INDEX]);
        parameters.setFocusMode(FOCUS_MODE_AUTO);

        mCamera.setParameters(parameters);
    }



    private void initMediaCodec() {
        if (mMediaCodec != null) {
            return;
        }
        int width = previewSize.width;
        int height = previewSize.height;
        int bitrate = 2 * width * height * FRAME_RATE / 20;
        try {
            MediaCodecInfo mediaCodecInfo = selectCodec(VCODEC_MIME);
            if (mediaCodecInfo == null) {
                Toast.makeText(this, "mMediaCodec null", Toast.LENGTH_LONG).show();
                throw new RuntimeException("mediaCodecInfo is Empty");
            }
            int colorFormat = getColorFormat(mediaCodecInfo);
            Log.w(TAG, "MediaCodecInfo " + mediaCodecInfo.getName() + ", colorFormat: " + colorFormat);
            mMediaCodec = MediaCodec.createByCodecName(mediaCodecInfo.getName());
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(VCODEC_MIME, width, height);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int getColorFormat(MediaCodecInfo mediaCodecInfo) {
        int matchedFormat = 0;
        MediaCodecInfo.CodecCapabilities codecCapabilities =
                mediaCodecInfo.getCapabilitiesForType(VCODEC_MIME);
        for (int i = 0; i < codecCapabilities.colorFormats.length; i++) {
            int format = codecCapabilities.colorFormats[i];
            if (format >= codecCapabilities.COLOR_FormatYUV420Planar &&
                    format <= codecCapabilities.COLOR_FormatYUV420PackedSemiPlanar) {
                if (format >= matchedFormat) {
                    matchedFormat = format;
                    logColorFormatName(format);
                    break;
                }
            }
        }
        return matchedFormat;
    }

    private void logColorFormatName(int format) {
        switch (format) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible:
                Log.d(TAG, "COLOR_FormatYUV420Flexible");
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                Log.d(TAG, "COLOR_FormatYUV420PackedPlanar");
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                Log.d(TAG, "COLOR_FormatYUV420Planar");
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                Log.d(TAG, "COLOR_FormatYUV420PackedSemiPlanar");
                break;
            case COLOR_FormatYUV420SemiPlanar:
                Log.d(TAG, "COLOR_FormatYUV420SemiPlanar");
                break;
        }
    }


    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            //是否是编码器
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            Log.w(TAG, Arrays.toString(types));
            for (String type : types) {
                Log.e(TAG, "equal " + mimeType.equalsIgnoreCase(type));
                if (mimeType.equalsIgnoreCase(type)) {
                    Log.e(TAG, "codecInfo " + codecInfo.getName());
                    return codecInfo;
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mFlvPacker.start();
        pushExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int ret = RtmpHandle.INSTANCE.connect("rtmp://192.168.1.102/mytv");
                Log.w(TAG, "打开RTMP连接: " + ret);
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mFlvPacker.stop();
       
        int ret = RtmpHandle.INSTANCE.close();
        Log.w(TAG, "关闭RTMP连接：" + ret);
//        IOUtils.close(mOutStream);
    }

    private void flvPackage(byte[] buf) {
        int width = previewSize.width;
        int height = previewSize.height;
        final int LENGTH = width * height;
        //YV12数据转化成COLOR_FormatYUV420Planar
        Log.d(TAG, LENGTH + "  " + (buf.length - LENGTH));
        for (int i = LENGTH; i < (LENGTH + LENGTH / 4); i++) {
            byte temp = buf[i];
            buf[i] = buf[i + LENGTH / 4];
            buf[i + LENGTH / 4] = temp;
//            char x = 128;
//            buf[i] = (byte) x;
        }
        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
        try {
            //查找可用的的input buffer用来填充有效数据
            int bufferIndex = mMediaCodec.dequeueInputBuffer(-1);
            if (bufferIndex >= 0) {
                //数据放入到inputBuffer中
                ByteBuffer inputBuffer = inputBuffers[bufferIndex];
                inputBuffer.clear();
                inputBuffer.put(buf, 0, buf.length);
                //把数据传给编码器并进行编码
                mMediaCodec.queueInputBuffer(bufferIndex, 0,
                        inputBuffers[bufferIndex].position(),
                        System.nanoTime() / 1000, 0);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                //输出buffer出队，返回成功的buffer索引。
                int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                while (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    //进行flv封装
                    mFlvPacker.onVideoData(outputBuffer, bufferInfo);
                    mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                }
            } else {
                Log.w(TAG, "No buffer available !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
