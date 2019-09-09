package com.github.sckm.itemgroup.example.items

import android.widget.TextView
import com.github.sckm.itemgroup.example.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

data class GridItemsHeader(
    val title: String
) : Item<ViewHolder>(title.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_grid_items_header

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.root.findViewById<TextView>(R.id.title).text = title
    }
}