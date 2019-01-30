package com.zyp.androidaudiovideostudy.audio;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zyp.androidaudiovideostudy.R;

public class BellService extends Service {
    public static final String TAG = BellService.class.getSimpleName();
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SMS_RECEIVED_ACTION);
        Log.d(TAG, "onCreate: ");

        registerReceiver(messageReceiver, filter);


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = MediaPlayer.create(this,R.raw.fallbackring);
        final int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "onCreate: streamVolume: "+streamVolume);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion: streamVolume: "+streamVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,streamVolume,0);
            }
        });


        mediaRecorder();
    }

    private void mediaRecorder() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        playAudio();

        return super.onStartCommand(intent, flags, startId);
    }
    
    private void playAudio() {
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "playAudio: streamMaxVolume: "+streamMaxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,streamMaxVolume,0);
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mediaPlayer.release();
        unregisterReceiver(messageReceiver);
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: 收到短信");
        }
    };


}
