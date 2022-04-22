package com.zyp.androidaudiovideostudy.audio;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.zyp.androidaudiovideostudy.R;
import com.zyp.androidaudiovideostudy.util.AudioRecordManager;
import com.zyp.androidaudiovideostudy.util.MediaRecordManager;
import com.zyp.androidaudiovideostudy.util.PcmUtil;
import com.zyp.liblame.NativeLameMP3Encoder;

import java.io.File;

public class AudioActivity extends AppCompatActivity {


    private static final String TAG = AudioActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(NativeLameMP3Encoder.getLameVersion());

        AudioRecordManager.init();



        findViewById(R.id.bt_start_audio_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioRecordManager.getInstance().startRecord();
            }
        });


        findViewById(R.id.bt_stop_audio_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioRecordManager.getInstance().stopRecord();
            }
        });


        findViewById(R.id.bt_start_audio_trace).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                AudioRecordManager.getInstance().playRecord();
            }
        });


        findViewById(R.id.bt_stop_audio_trace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioRecordManager.getInstance().stopPlayRecord();
            }
        });

        findViewById(R.id.bt_start_media_record).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                MediaRecordManager.getInstance().startMediaRecord();
            }
        });


        findViewById(R.id.bt_stop_media_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaRecordManager.getInstance().stopMediaRecord();
            }
        });

        findViewById(R.id.bt_start_media_player).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                MediaRecordManager.getInstance().startPlayMedia(AudioActivity.this);
            }
        });


        findViewById(R.id.bt_stop_media_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaRecordManager.getInstance().stopPlayMedia();
            }
        });


        findViewById(R.id.bt_pcm_to_wav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "run: start");
                        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                                + File.separator + "arm"+ File.separator;
                        PcmUtil.convertPcm2Wav(dir+"aaa.pcm",dir+"aaa_pcm.wav",SAMPLE_RATE_HERTZ);

                        Log.d(TAG, "run: end");

                    }
                }).start();
            }
        });

        findViewById(R.id.bt_pcm_to_mp3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "run: start");

                        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                                + File.separator + "arm"+ File.separator;
                        NativeLameMP3Encoder.convertPcmToMp3(dir+"aaa.pcm",dir+"aaa_pcm.mp3",SAMPLE_RATE_HERTZ);
                        Log.d(TAG, "run: end");

                    }
                }).start();
            }
        });

        findViewById(R.id.bt_wav_to_mp3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "run: start");

                        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                                + File.separator + "arm"+ File.separator;
                        NativeLameMP3Encoder.convertWavToMp3(dir+"aaa.wav",dir+"aaa_wav.mp3",SAMPLE_RATE_HERTZ);
                        Log.d(TAG, "run: end");

                    }
                }).start();
            }
        });


    }
    public static final int SAMPLE_RATE_HERTZ = 44100;


    private SoundPool soundPool;//播放短的反应速度要求高的声音，比如游戏爆破音，使用独立线程载入音乐文件

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaRecordManager.getInstance().destory();
    }
}
