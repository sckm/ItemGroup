package com.github.sckm.itemgroup.example.items

import android.widget.TextView
import androidx.annotation.ColorInt
import com.github.sckm.itemgroup.example.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

data class ColorfulGridItem(
    private val body: String,
    @ColorInt private val color: Int
) : Item<GroupieViewHolder>(body.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_colorful_grid

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        (viewHolder.root as TextView).apply {
            text = body
            setBackgroundColor(color)
        }
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int = 1
}