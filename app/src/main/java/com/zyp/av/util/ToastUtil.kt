package com.zyp.av.util

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.zyp.av.app

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2022/5/11 3:32 下午
 */
object ToastUtil {
    private val mHandler = Handler(Looper.getMainLooper())

    fun show(msg: String) {
        mHandler.post {
            Toast.makeText(app(), msg, Toast.LENGTH_LONG).show()
        }
    }
}