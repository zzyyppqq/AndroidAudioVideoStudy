package com.zyp.androidaudiovideostudy;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zyp.androidaudiovideostudy.util.RecMicToMp3;

public class LiblameActivity extends AppCompatActivity {

    private RecMicToMp3 mRecMicToMp3 = new RecMicToMp3(
            Environment.getExternalStorageDirectory() + "/bbb.mp3", 8000);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liblame);

        final TextView statusTextView = (TextView) findViewById(R.id.StatusTextView);

        mRecMicToMp3.setHandle(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RecMicToMp3.MSG_REC_STARTED:
                        statusTextView.setText("˜^‰¹’†");
                        break;
                    case RecMicToMp3.MSG_REC_STOPPED:
                        statusTextView.setText("");
                        break;
                    case RecMicToMp3.MSG_ERROR_GET_MIN_BUFFERSIZE:
                        statusTextView.setText("");
                        Toast.makeText(LiblameActivity.this,
                                "˜^‰¹‚ªŠJŽn‚Å‚«‚Ü‚¹‚ñ‚Å‚µ‚½B‚±‚Ì’[––‚ª˜^‰¹‚ðƒTƒ|[ƒg‚",
                                Toast.LENGTH_LONG).show();
                        break;
                    case RecMicToMp3.MSG_ERROR_CREATE_FILE:
                        statusTextView.setText("");
                        Toast.makeText(LiblameActivity.this, "ƒtƒ@ƒCƒ‹‚ª¶¬‚Å‚«‚Ü‚¹‚ñ‚Å‚µ‚½",
                                Toast.LENGTH_LONG).show();
                        break;
                    case RecMicToMp3.MSG_ERROR_REC_START:
                        statusTextView.setText("");
                        Toast.makeText(LiblameActivity.this, "˜^‰¹‚ªŠJŽn‚Å‚«‚Ü‚¹‚ñ‚Å‚µ‚½",
                                Toast.LENGTH_LONG).show();
                        break;
                    case RecMicToMp3.MSG_ERROR_AUDIO_RECORD:
                        statusTextView.setText("");
                        Toast.makeText(LiblameActivity.this, "˜^‰¹‚ª‚Å‚«‚Ü‚¹‚ñ‚Å‚µ‚½",
                                Toast.LENGTH_LONG).show();
                        break;
                    case RecMicToMp3.MSG_ERROR_AUDIO_ENCODE:
                        statusTextView.setText("");
                        Toast.makeText(LiblameActivity.this, "ƒGƒ“ƒR[ƒh‚ÉŽ¸”s‚µ‚Ü‚µ‚½",
                                Toast.LENGTH_LONG).show();
                        break;
                    case RecMicToMp3.MSG_ERROR_WRITE_FILE:
                        statusTextView.setText("");
                        Toast.makeText(LiblameActivity.this, "ƒtƒ@ƒCƒ‹‚Ì‘‚«ž‚Ý‚ÉŽ¸”s‚µ‚Ü‚µ‚½",
                                Toast.LENGTH_LONG).show();
                        break;
                    case RecMicToMp3.MSG_ERROR_CLOSE_FILE:
                        statusTextView.setText("");
                        Toast.makeText(LiblameActivity.this, "ƒtƒ@ƒCƒ‹‚Ì‘‚«ž‚Ý‚ÉŽ¸”s‚µ‚Ü‚µ‚½",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        });

        Button startButton = (Button) findViewById(R.id.StartButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecMicToMp3.start();
            }
        });
        Button stopButton = (Button) findViewById(R.id.StopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecMicToMp3.stop();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecMicToMp3.stop();
    }
}
