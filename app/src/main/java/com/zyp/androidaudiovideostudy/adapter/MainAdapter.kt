package com.zyp.androidaudiovideostudy.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyp.androidaudiovideostudy.AbstractAdapter
import com.zyp.androidaudiovideostudy.AbstractViewHolder
import com.zyp.androidaudiovideostudy.R

open class MainAdapter(datas: List<MainItem>) : AbstractAdapter<MainItem>() {
    init {
        addAll(datas)
    }
    override fun createBaseViewHolder(view: View): AbstractViewHolder<MainItem> {
        return MainViewHolder(view)
    }

    override fun getItemLayoutId(): Int = R.layout.main_item

}

class MainViewHolder(item: View) : AbstractViewHolder<MainItem>(item) {
    private val tvLabel: TextView

    init {
        tvLabel = item.findViewById<TextView>(R.id.tv_label)
    }

    override fun bindData(data: MainItem, position: Int) {
        tvLabel.text = data.className
    }
}

data class MainItem(val clazz: Class<*>) {
    var className = clazz.simpleName
}
