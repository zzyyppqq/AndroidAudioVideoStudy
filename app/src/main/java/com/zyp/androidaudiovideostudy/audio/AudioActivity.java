package com.zyp.androidaudiovideostudy.audio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zyp.androidaudiovideostudy.R;

public class AudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        final Intent intent = new Intent(AudioActivity.this, MediaPlayService.class);
        findViewById(R.id.bt_start_play_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction(MediaPlayService.ACTION_PLAY_AUDIO);
                startService(intent);
            }
        });

        findViewById(R.id.bt_stop_play_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction(MediaPlayService.ACTION_STOP_AUDIO);
                startService(intent);
            }
        });

        findViewById(R.id.bt_start_media_recorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction(MediaPlayService.ACTION_START_MEDIA_RECORDER);
                startService(intent);
            }
        });
        findViewById(R.id.bt_stop_media_recorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction(MediaPlayService.ACTION_STOP_MEDIA_RECORDER);
                startService(intent);
            }
        });

        findViewById(R.id.bt_start_record_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction(MediaPlayService.ACTION_START_RECORD_PALY);
                startService(intent);
            }
        });

        findViewById(R.id.bt_stop_record_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction(MediaPlayService.ACTION_STOP_RECORD_PALY);
                startService(intent);
            }
        });
    }
}
