package com.zyp.androidaudiovideostudy.audio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioRouting;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.zyp.androidaudiovideostudy.R;

import java.io.File;
import java.io.IOException;

public class MediaPlayService extends Service {
    public static final String TAG = MediaPlayService.class.getSimpleName();
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;//播放视频、音频，适合播放长语音
    private SoundPool soundPool;//播放短的反应速度要求高的声音，比如游戏爆破音，使用独立线程载入音乐文件
    private MediaRecorder mediaRecorder;//录制wav、mp3并保存到文件
    private AudioRecord audioRecord;//录制pcm
    private AudioTrack audioTrack;//播放pcm
    //Ringtone和RingtoneManager播放铃声
    //JetPlay播放音频，用于控制游戏声音特效
    //AudioEffect音效控制
    //TextToSpeech语音识别技术
    //Vibrator震动
    //AlarmManage闹钟

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: ");

        initAudio();

        initAudioTrack();

        initSoundPool();



    }

    private void initSoundPool() {
        soundPool = new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
    }

    private void playSoundPool(){
        final int sourceId = soundPool.load(this, R.raw.in_call_alarm, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                soundPool.play(sourceId,1,1,0,-1,-1);
            }
        });

    }

    private void initAudio() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.fallbackring);
        final int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "initAudio: streamVolume: " + streamVolume);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion: streamVolume: " + streamVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, 0);
            }
        });
    }

    private void openRecorderFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), getMimeType(file));
        startActivity(intent);

    }

    private String getMimeType(File file) {

        return "audio";
    }

    public static final String ACTION_PLAY_AUDIO = "play.audio";
    public static final String ACTION_STOP_AUDIO = "stop.audio";
    public static final String ACTION_START_MEDIA_RECORDER = "start.media.recorder";
    public static final String ACTION_STOP_MEDIA_RECORDER = "stop.media.recorder";
    public static final String ACTION_START_RECORD_PALY = "start.record.play";
    public static final String ACTION_STOP_RECORD_PALY = "stop.record.play";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_PLAY_AUDIO:
                    playAudio();
                    break;
                case ACTION_STOP_AUDIO:
                    stopAudio();
                    break;
                case ACTION_START_MEDIA_RECORDER:
                    startMediaRecorder();
                    break;
                case ACTION_STOP_MEDIA_RECORDER:
                    stopMediaRecorder();
                    break;

                case ACTION_START_RECORD_PALY:
                    startRecordPlay();
                    break;
                case ACTION_STOP_RECORD_PALY:
                    stopRecordPlay();
                    break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private int playBufSize, recordBufSize;
    private int sampleRateInHz = 16000;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private void initAudioTrack() {

        recordBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, audioFormat);
        playBufSize = AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO,audioFormat);
        Log.d(TAG, "initAudioTrack: recordBufSize: "+recordBufSize+" , playBufSize: "+playBufSize);

//        MediaRecorder.AudioSource.VOICE_COMMUNICATION
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, audioFormat,
                recordBufSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, audioFormat,
                playBufSize, AudioTrack.MODE_STREAM);
        audioTrack.setStereoVolume(0.5f, 0.5f);
    }

    private void startRecordPlay(){
        isRecording = true;
        new RecordPlayThread().start();
    }

    private void stopRecordPlay(){
        isRecording = false;
    }

    private void releaseRecordPlay(){
        audioRecord.release();
        audioTrack.release();
    }

    private volatile boolean isRecording = false;
    private class RecordPlayThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                Log.d(TAG, "run: recordBufSize : "+recordBufSize);
                byte[] buffer = new byte[recordBufSize];
                audioRecord.startRecording();//开始录制
                audioTrack.play();//开始播放

                while (isRecording){
                    int readSize = audioRecord.read(buffer, 0, recordBufSize);
                    byte[] tmpBuf = new byte[readSize];
                    System.arraycopy(buffer,0,tmpBuf,0,readSize);
                    //写入数据即播放
                    audioTrack.write(tmpBuf,0,tmpBuf.length);
                }
                audioTrack.stop();
                audioRecord.stop();
            }catch (Exception e){
                Log.d(TAG, "run: "+e.getMessage());
                e.printStackTrace();
            }

        }
    }


    /**
     * 每次录音必须重新new MediaRecorder（）
     */
    private void startMediaRecorder() {
        Log.d(TAG, "startMediaRecorder: ");
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//录音源麦克风
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                    Log.d(TAG, "onInfo: " + i);

                }
            });

            File recordAudioFile = File.createTempFile("aaa", ".amr", Environment.getExternalStorageDirectory());
            mediaRecorder.setOutputFile(recordAudioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMediaRecorder() {
        Log.d(TAG, "stopMediaRecorder: ");
        mediaRecorder.stop();
        mediaRecorder.reset();
        releaseMediaRecorder();
    }

    private void releaseMediaRecorder() {
        Log.d(TAG, "releaseMediaRecorder: ");
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void playAudio() {
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "playAudio: streamMaxVolume: " + streamMaxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamMaxVolume, 0);
        mediaPlayer.start();
    }

    private void stopAudio() {
        Log.d(TAG, "stopAudio: ");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void releaseAudio() {
        Log.d(TAG, "releaseAudio: ");
        mediaPlayer.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        releaseAudio();
        releaseMediaRecorder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
