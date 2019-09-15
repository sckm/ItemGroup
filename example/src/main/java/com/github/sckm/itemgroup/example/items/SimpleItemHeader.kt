package com.github.sckm.itemgroup.example.items

import android.widget.TextView
import com.github.sckm.itemgroup.example.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

data class SimpleItemHeader(
    val title: String,
    var count: Int,
    var totalMills: Long
) : Item<ViewHolder>(title.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_simple_items_header

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val text = "$title: update count=$count, average=${totalMills / count}ms"
        (viewHolder.root as TextView).text = text
    }
}

