package com.zyp.ffmpeglib;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2022/4/25 7:53 下午
 */
public class FFVideoPlayer extends SurfaceView {
    public FFVideoPlayer(Context context) {
        this(context, null);
    }

    public FFVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FFVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().setFormat(PixelFormat.RGBA_8888);
    }

    public void play(final String url) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new FFmpegLib().render(url, FFVideoPlayer.this.getHolder().getSurface());
            }
        }).start();
    }

}

