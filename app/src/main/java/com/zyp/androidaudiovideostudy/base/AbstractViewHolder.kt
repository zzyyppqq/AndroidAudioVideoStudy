/*
 * Copyright (C) 2019 NaLong. All Rights Reserved.
 * NaLong group reserve all right of the client.
 * Without authorization, no individual or organization may copy, extract or modify it.
 * If you find any infringement, please contact us.
 */
package com.zyp.androidaudiovideostudy

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2020/6/22 6:31 PM
 */
abstract class AbstractViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindData(data: T, position: Int)
}