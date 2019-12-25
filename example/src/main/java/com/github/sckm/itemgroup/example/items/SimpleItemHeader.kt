package com.github.sckm.itemgroup.example.items

import android.widget.TextView
import com.github.sckm.itemgroup.example.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

data class SimpleItemHeader(
    val title: String,
    var count: Int,
    var totalMills: Long
) : Item<GroupieViewHolder>(title.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_simple_items_header

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val text = "$title: update count=$count, average=${totalMills / count}ms"
        (viewHolder.root as TextView).text = text
    }
}

