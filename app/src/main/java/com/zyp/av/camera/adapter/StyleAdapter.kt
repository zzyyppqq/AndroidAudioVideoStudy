package com.zyp.av.camera.adapter

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zyp.av.AbstractAdapter
import com.zyp.av.AbstractViewHolder
import com.zyp.av.R

open class StyleAdapter() : AbstractAdapter<StyleItem>() {

    override fun createBaseViewHolder(view: View): AbstractViewHolder<StyleItem> {
        return StyleViewHolder(view)
    }

    override fun getItemLayoutId(): Int = R.layout.style_adapter_item

}

class StyleViewHolder(item: View) : AbstractViewHolder<StyleItem>(item) {
    private val tvLabel: TextView
    private val ivView: ImageView

    init {
        tvLabel = item.findViewById<TextView>(R.id.tv_label)
        ivView = item.findViewById<ImageView>(R.id.iv_view)
    }

    override fun bindData(data: StyleItem, position: Int) {
        tvLabel.text = data.name
        ivView.setImageBitmap(BitmapFactory.decodeFile(data.iconPath));
    }
}

data class StyleItem(
    var iconPath: String,
    var name: String,
    var stylePath: String
)
