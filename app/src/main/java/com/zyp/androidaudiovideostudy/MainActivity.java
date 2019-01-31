package com.zyp.androidaudiovideostudy;

import android.content.Context;
import android.media.AudioFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.AccessNetworkConstants;
import android.view.View;
import android.widget.TextView;

import com.zyp.androidaudiovideostudy.audio.AudioRecordManager;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(LameNative.stringFromJNI());

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


        findViewById(R.id.bt_pcm_to_wav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        LameNative.init(SAMPLE_RATE_HERTZ, CHANNEL_CONFIG, SAMPLE_RATE_HERTZ, AudioFormat.ENCODING_MP3);
//                        LameNative.encode()


                    }
                }).start();


            }
        });
    }
}
