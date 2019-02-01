package com.zyp.androidaudiovideostudy;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zyp.libmad.NativeMP3Decoder;

import java.io.File;

public class LibmadActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "LibmadActivity";
    private String assertFolderName = "Android";
//    private String assertMusicName = "rich.mp3";
    private String assertMusicName = "shinian.mp3";
    @SuppressLint("SdCardPath")
    private String targetFolderPath = Environment.getExternalStorageDirectory()
            .getPath() + File.separator + assertFolderName;
    private String decodeFilePath = Environment.getExternalStorageDirectory()
            .getPath()
            + File.separator
            + assertFolderName
            + File.separator
            + assertMusicName;

    private Thread mThread;
    private boolean mThreadFlag;
    private short[] audioBuffer;
    private AudioTrack mAudioTrack;

    private int samplerate;
    private int mAudioMinBufSize;
    private int initRet;
    private NativeMP3Decoder mMP3Decoder;

    private Button PlayMusic, PauseMusic;
    private Button leftSoundChannel, lightSoundChannel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libmad);

        FileHelper.copyFolderFromAssets(LibmadActivity.this, assertFolderName,targetFolderPath);

        PlayMusic = (Button) findViewById(R.id.playMusic);
        PauseMusic = (Button) findViewById(R.id.PauseMusic);
        leftSoundChannel = (Button) findViewById(R.id.leftsoundchannel);
        lightSoundChannel = (Button) findViewById(R.id.rightsoundchannel);

        PlayMusic.setOnClickListener(this);
        PauseMusic.setOnClickListener(this);
        leftSoundChannel.setOnClickListener(this);
        lightSoundChannel.setOnClickListener(this);

        mMP3Decoder = new NativeMP3Decoder();

        initRet = mMP3Decoder.initAudioPlayer(decodeFilePath, 0);

        if (initRet == -1) {
            Log.i(TAG, "Couldn't open file '" + decodeFilePath + "'");
        } else {
            Log.i(TAG, "open file else'" + decodeFilePath + "'");
            mThreadFlag = true;
            initAudioPlayer();
            audioBuffer = new short[1024 * 20];

            mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (mThreadFlag) {

                        if (mAudioTrack != null) {
                            if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED) {
                                if (mAudioTrack != null) {
                                    if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
//                                        Log.d(TAG, "run: play :");
                                        mMP3Decoder.getAudioBuf(audioBuffer,
                                                mAudioMinBufSize);
                                        mAudioTrack.write(audioBuffer, 0,
                                                mAudioMinBufSize);
                                    }
                                }
                            } else {
                                Log.d(TAG, "run: sleep 1s");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }
            });

            mThread.start();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAudioTrack.play();
        }
    }

    public void setChannel(boolean left, boolean right) {
        if (null != mAudioTrack) {
            mAudioTrack.setStereoVolume(left ? 1 : 0, right ? 1 : 0);
            mAudioTrack.play();
        }
    }

    @SuppressWarnings("deprecation")
    private void initAudioPlayer() {
        samplerate = mMP3Decoder.getAudioSamplerate();
        System.out.println("samplerate = " + samplerate);
        Log.d("bfp", "samplerate:" + samplerate);
        samplerate = samplerate / 2;

        mAudioMinBufSize = AudioTrack.getMinBufferSize(samplerate,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,

                samplerate, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, mAudioMinBufSize,
                AudioTrack.MODE_STREAM);

    }

    @Override
    protected void onDestroy() {
        mThreadFlag = false;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.stop();
                mAudioTrack.release();
            }
        }

        mAudioTrack = null;
        audioBuffer = null;
        mMP3Decoder.closeAduioFile();
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playMusic:

                if (initRet == -1) {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't open file '" + decodeFilePath + "'",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
                        mAudioTrack.play();
                    } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                        mAudioTrack.play();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Already in play", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.PauseMusic:
                if (initRet == -1) {
                    Log.i("conowen", "Couldn't open file '" + decodeFilePath + "'");
                    Toast.makeText(getApplicationContext(),
                            "Couldn't open file '" + decodeFilePath + "'",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        mAudioTrack.pause();
                    } else {
                        Toast.makeText(getApplicationContext(), "Already stop",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.leftsoundchannel:
                setChannel(true, false);
                break;
            case R.id.rightsoundchannel:
                setChannel(false, true);
                break;

            default:
                break;
        }
    }
}
