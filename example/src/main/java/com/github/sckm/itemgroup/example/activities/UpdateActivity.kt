package com.github.sckm.itemgroup.example.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.github.sckm.itemgroup.ItemGroup
import com.github.sckm.itemgroup.example.R
import com.github.sckm.itemgroup.example.items.ColorfulGridItem
import com.github.sckm.itemgroup.example.items.GridItemsHeader
import com.github.sckm.itemgroup.example.items.SimpleItemHeader
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_item_group.*
import kotlin.system.measureTimeMillis

class UpdateActivity : AppCompatActivity() {
    companion object {
        fun startActivity(context: Context, useItemGroup: Boolean) {
            val intent = Intent(context, UpdateActivity::class.java).apply {
                putExtra("use-item-group", useItemGroup)
            }
            context.startActivity(intent)
        }
    }

    private val colors by lazy {
        listOf(
            Color.rgb(0xff, 0xd6, 0xd6),
            Color.rgb(0xd6, 0xff, 0xff),
            Color.rgb(0xd6, 0xff, 0xd6),
            Color.rgb(0xd6, 0xea, 0xff),
            Color.rgb(0xff, 0xff, 0xd6)
        )
    }

    private val items by lazy {
        (1..200).map { ColorfulGridItem(it.toString(), colors[it % colors.size]) }
    }

    private var updateCount = 0
    private var totalMills = 0L

    private val useItemGroup by lazy { intent.getBooleanExtra("use-item-group", true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_group)

        setupGroupieItems()
    }

    private fun setupGroupieItems() {
        val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
            val group: Group = if (useItemGroup) {
                val itemGroup = ItemGroup()
                itemGroup.add(GridItemsHeader("shuffle items"))
                itemGroup.addAll(items)

                setOnItemClickListener(OnItemClickListener { item, _ ->
                    if (item !is GridItemsHeader) return@OnItemClickListener

                    val shuffled = items.shuffled()
                    val simpleItemHeader = SimpleItemHeader("ItemGroup#update example", 0, 0)
                    measureTimeMillis {
                        val newItems = mutableListOf<Item<*>>()
                        newItems += GridItemsHeader("shuffle items")
                        newItems += simpleItemHeader
                        newItems.addAll(shuffled)

                        itemGroup.update(newItems)
                    }.also { time ->
                        Toast.makeText(
                            this@UpdateActivity,
                            "Updated: took ${time}ms",
                            Toast.LENGTH_SHORT
                        ).show()

                        updateCount += 1
                        totalMills += time
                        simpleItemHeader.count = updateCount
                        simpleItemHeader.totalMills = totalMills
                        simpleItemHeader.notifyChanged()
                    }
                })

                itemGroup
            } else {
                val section = Section()
                section.add(GridItemsHeader("shuffle items"))
                section.addAll(items)

                setOnItemClickListener(OnItemClickListener { item, _ ->
                    if (item !is GridItemsHeader) return@OnItemClickListener

                    val simpleItemHeader =
                        SimpleItemHeader("Section#update example", updateCount, totalMills)
                    val shuffled = items.shuffled()
                    measureTimeMillis {
                        val newItems = mutableListOf<Item<*>>()
                        newItems += GridItemsHeader("shuffle items")
                        newItems += simpleItemHeader
                        newItems.addAll(shuffled)

                        section.update(newItems)
                    }.also { time ->
                        Toast.makeText(
                            this@UpdateActivity,
                            "Updated: took ${time}ms",
                            Toast.LENGTH_SHORT
                        ).show()

                        updateCount += 1
                        totalMills += time
                        simpleItemHeader.count = updateCount
                        simpleItemHeader.totalMills = totalMills
                        simpleItemHeader.notifyChanged()
                    }
                })

                section
            }

            add(group)
            spanCount = 3
        }

        recycler_view.apply {
            layoutManager =
                GridLayoutManager(this@UpdateActivity, groupAdapter.spanCount).apply {
                    spanSizeLookup = groupAdapter.spanSizeLookup
                }
            adapter = groupAdapter
        }
    }
}
