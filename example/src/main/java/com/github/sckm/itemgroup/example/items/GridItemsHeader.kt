package com.github.sckm.itemgroup.example.items

import android.widget.TextView
import com.github.sckm.itemgroup.example.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

data class GridItemsHeader(
    val title: String
) : Item<GroupieViewHolder>(title.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_grid_items_header

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.root.findViewById<TextView>(R.id.title).text = title
    }
}