package com.zyp.androidaudiovideostudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        tv.setText(AudioLameNative.stringFromJNI());

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
                AudioRecordManager.getInstance().pcmToWav();
            }
        });

    }


}
