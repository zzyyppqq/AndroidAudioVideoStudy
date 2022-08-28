package com.zyp.av.fdkaac

import com.zyp.av.base.BaseActivity
import android.os.Bundle
import android.widget.Button
import com.zyp.av.R
import com.zyp.av.util.Const
import com.zyp.av.util.FileUtils
import com.zyp.fdkaaclib.FdkAACLib
import java.io.File
import java.util.concurrent.Executors

class FdkAACActivity : BaseActivity() {
    private var aacPath = Const.sdPath + File.separator + "mine.aac"
    private var pcmPath = Const.sdPath + File.separator + "mine.pcm"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fdk_aac_activity)
        findViewById<Button>(R.id.btn_aac_pcm).setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                FileUtils.copyFilesAssets(this, "mine.aac", pcmPath)
                FdkAACLib().aacDecoder(aacPath, pcmPath);
            }
        }
    }
}