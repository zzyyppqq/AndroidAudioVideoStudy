package com.zyp.androidaudiovideostudy.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MediaRecordManager {

    private static final String TAG = MediaRecordManager.class.getSimpleName();

    private MediaRecorder mediaRecorder;//录制wav、mp3并保存到文件

    private static MediaRecordManager instance;
    private File recordAudioFile;


    private MediaRecordManager() {

    }

    public static MediaRecordManager getInstance() {
        if (instance == null) {
            synchronized (AudioRecordManager.class) {
                if (instance == null) {
                    instance = new MediaRecordManager();
                }
            }
        }
        return instance;
    }


    /**
     * 每次录音必须重新new MediaRecorder（）
     */
    public void startMediaRecord() {
        Log.d(TAG, "startMediaRecorder: ");
        try {
            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            }
            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//录音源麦克风
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                    Log.d(TAG, "onInfo: " + i);

                }
            });

            recordAudioFile = File.createTempFile("aaa", ".amr", Environment.getExternalStorageDirectory());
            mediaRecorder.setOutputFile(recordAudioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopMediaRecord() {
        Log.d(TAG, "stopMediaRecorder: ");
        mediaRecorder.stop();
        mediaRecorder.reset();
    }

    public void releaseMediaRecord() {
        Log.d(TAG, "releaseMediaRecorder: ");
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;//播放视频、音频，适合播放长语音
    private int curStreamVolume;

    public void startPlayMedia(Context context) {
        if (recordAudioFile == null){
            Toast.makeText(context, "请先录音", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (audioManager == null) {
                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                curStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Log.d(TAG, "initMedia: streamVolume: " + curStreamVolume);
            }
            if (mediaPlayer == null) {
//              mediaPlayer = MediaPlayer.create(context, Uri.fromFile(recordAudioFile));
                mediaPlayer = new MediaPlayer();
                this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.d(TAG, "onCompletion: streamVolume: " + curStreamVolume);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curStreamVolume, 0);
                    }
                });
            }


            mediaPlayer.reset();
            mediaPlayer.setDataSource(recordAudioFile.getAbsolutePath());
            int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "playMedia: streamMaxVolume: " + streamMaxVolume);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamMaxVolume, 0);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopPlayMedia() {
        Log.d(TAG, "stopMedia: ");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

    }

    public void releaseMedia() {
        Log.d(TAG, "releaseMedia: ");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void destory() {
        releaseMediaRecord();
        releaseMedia();
    }


}
