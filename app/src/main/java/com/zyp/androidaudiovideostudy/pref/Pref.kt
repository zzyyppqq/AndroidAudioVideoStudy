package com.zyp.androidaudiovideostudy.pref


/**
 * 描述：sp操作类
 * 作者：@author alex
 * 创建时间：2021/3/1 4:51 PM
 */
object ECGPref {
    /**
     * 以ECGPref类名作为sp文件名、new_key字段属性作为键
     */
    var native_window_agba_url by prefName("native_window_agba_url","", "pref_audio_video_study")

    var yuv_view_url by prefName("yuv_view_url","", "pref_audio_video_study")

    var y_view_url by prefName("y_view_url","", "pref_audio_video_study")

    var rgb_view_url by prefName("rgb_view_url","", "pref_audio_video_study")

}


