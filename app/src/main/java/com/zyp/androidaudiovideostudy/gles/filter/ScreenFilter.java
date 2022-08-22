package com.zyp.androidaudiovideostudy.gles.filter;

import android.content.Context;

import com.zyp.androidaudiovideostudy.R;

/**
 * 负责往屏幕上渲染
 */
public class ScreenFilter extends AbstractFilter {

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }

}
