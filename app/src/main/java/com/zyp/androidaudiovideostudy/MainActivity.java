package com.zyp.androidaudiovideostudy;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zyp.androidaudiovideostudy.util.AudioRecordManager;
import com.zyp.androidaudiovideostudy.util.MediaRecordManager;
import com.zyp.androidaudiovideostudy.util.PcmUtil;
import com.zyp.liblame.NativeLameMP3Encoder;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                MediaRecordManager.getInstance().startPlayMedia(MainActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaRecordManager.getInstance().destory();
    }
}
