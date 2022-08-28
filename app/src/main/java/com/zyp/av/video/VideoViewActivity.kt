package com.zyp.av.video

import android.os.Bundle
import android.os.Environment
import android.widget.MediaController
import com.zyp.av.base.BaseActivity
import androidx.core.content.FileProvider
import com.zyp.av.databinding.ActivityVideoViewBinding
import com.zyp.av.util.Const
import java.io.File
import java.lang.Exception

class VideoViewActivity : BaseActivity() {
    private var _binding: ActivityVideoViewBinding? = null
    private val mBinding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.videoView.setMediaController(MediaController(this));

        mBinding.etText.setText("${Const.sdPath}/test_video.mp4")

        val sharedFile = File(mBinding.etText.text.toString())
        val sharedFileUri =
            FileProvider.getUriForFile(this, "com.zyp.androidaudiovideostudy.provider", sharedFile);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mBinding.videoView.setVideoURI(sharedFileUri)
//            mBinding.videoView.setVideoPath(sharedFile.path)
//            mBinding.videoView.setVideoURI(Uri.parse("android.resource://com.zyp.androidaudiovideostudy/" + R.raw.test_video))
        }

        mBinding.btnPlay.setOnClickListener {
            try {
                mBinding.videoView.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}