/*
 * Copyright (C) 2019 NaLong. All Rights Reserved.
 * NaLong group reserve all right of the client.
 * Without authorization, no individual or organization may copy, extract or modify it.
 * If you find any infringement, please contact us.
 */
package com.zyp.androidaudiovideostudy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zyp.androidaudiovideostudy.AbstractViewHolder
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2020/6/22 6:16 PM
 */
abstract class AbstractAdapter<T> : RecyclerView.Adapter<AbstractViewHolder<T>>() {

    val datas = CopyOnWriteArrayList<T>()

    private var mItemClickListener: OnRecyclerItemClickListener<T>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(getItemLayoutId(), parent, false)
        return createBaseViewHolder(view)
    }

    fun setOnRecyclerViewItemClickListener(itemClickListener: OnRecyclerItemClickListener<T>) {
        this.mItemClickListener = itemClickListener
    }

    abstract fun createBaseViewHolder(view: View): AbstractViewHolder<T>

    abstract fun getItemLayoutId(): Int

    override fun onBindViewHolder(holder: AbstractViewHolder<T>, position: Int) {
        holder.bindData(datas[position], position)
        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(holder.itemView, position, datas[position])
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun addItem(t: T) {
        datas.add(t)
        notifyItemInserted(datas.size - 1)
    }

    fun addItem(t: T, position: Int) {
        datas.add(position, t)
        notifyItemInserted(position)
    }

    fun addAll(t: List<T>) {
        datas.clear()
        datas.addAll(t)
        notifyDataSetChanged()
    }

    fun modifyItem(t: T, position: Int) {
        datas[position] = t
        notifyItemChanged(position)
    }

    fun clear() {
        datas.clear()
    }

    fun clearAndNotifyDataSetChanged() {
        clear()
        notifyDataSetChanged()
    }

    open fun removeItem(item: T) {
        for (index in 0 until datas.size) {
            if (item == datas[index]) {
                removeItem(index)
                return
            }
        }
    }

    fun removeItem(position: Int) {
        datas.removeAt(position)
        notifyDataSetChanged()
    }

    interface OnRecyclerItemClickListener<T> {
        fun onItemClick(itemView: View, position: Int, data: T)
    }

}