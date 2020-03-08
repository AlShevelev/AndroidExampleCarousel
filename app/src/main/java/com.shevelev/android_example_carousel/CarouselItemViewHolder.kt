package com.shevelev.android_example_carousel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class CarouselItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: CarouselListItem, position: Int) {
        view.listItemIcon.setImageResource(item.icon)
        view.root.tag = CarouselItemTag(id = item.id, position = position)
    }

    fun recycle() {
    }
}