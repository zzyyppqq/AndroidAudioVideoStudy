package com.zyp.av.video

import com.zyp.av.base.BaseActivity
import android.os.Bundle
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.zyp.av.util.ToastUtil

/**
 * 使用Android中的SurfaceView播放RGB视频数据
 */
class RgbaPlayerActivity : BaseActivity() {

    private lateinit var mMySurfaceView: MySurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_rgba_player)
        mMySurfaceView = MySurfaceView(this)
        setContentView(mMySurfaceView);
    }

    override fun onDestroy() {
        super.onDestroy()
        mMySurfaceView.m_flag = false
    }
}

class MySurfaceView(context: Context?) : SurfaceView(context), SurfaceHolder.Callback {

    companion object {
        private const val TAG = "MySurfaceView"
        private const val RGB_FILE_NAME = "/sdcard/aecg/dongfengpo_352x240_rgba.rgb"
        private const val PICTURE_WIDTH = 352
        private const val PICTURE_HEIGHT = 240
        private const val PICTURE_SIZE = PICTURE_WIDTH * PICTURE_HEIGHT * 4
    }

    private var m_srcRect: Rect
    private var m_dstRect: Rect
    private var m_surfaceHolder: SurfaceHolder
    private lateinit var m_canvas: Canvas
    private var m_fileInputStream: FileInputStream? = null
    var m_flag: Boolean = false
    var m_pixel = ByteArray(PICTURE_SIZE)

    init {
        Log.i(TAG, "MySurfaceView Constructor")
        m_flag = false
        m_surfaceHolder = this.holder
        m_surfaceHolder.addCallback(this)
        m_srcRect = Rect(0, 0, PICTURE_WIDTH, PICTURE_HEIGHT)
        m_dstRect = Rect(PICTURE_WIDTH, 0, PICTURE_WIDTH + PICTURE_WIDTH, PICTURE_HEIGHT)
    }

    private val m_thread = Thread {
        while (m_flag) {
            m_canvas = m_surfaceHolder.lockCanvas()
            m_canvas.drawColor(Color.BLACK)
            try {
                if (-1 == m_fileInputStream!!.read(m_pixel)) {
                    break
                }
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            val buffer = ByteBuffer.wrap(m_pixel)
            val videoBitmap = Bitmap.createBitmap(
                PICTURE_WIDTH,
                PICTURE_HEIGHT,
                Bitmap.Config.ARGB_8888
            )
            videoBitmap.copyPixelsFromBuffer(buffer)
            m_canvas.drawBitmap(videoBitmap, m_srcRect, m_dstRect, null)
            if (m_canvas != null) {
                m_surfaceHolder.unlockCanvasAndPost(m_canvas)
            }
            try {
                Thread.sleep(5)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.i(TAG, "surfaceCtreated")
        m_flag = true
        try {
            m_fileInputStream = FileInputStream(RGB_FILE_NAME)
            m_thread.start()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            ToastUtil.show("FileNotFoundException: $RGB_FILE_NAME")
        }
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
        Log.i(TAG, "surfaceChanged")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.i(TAG, "surfaceDestroyed")
        m_flag = false
    }
}


