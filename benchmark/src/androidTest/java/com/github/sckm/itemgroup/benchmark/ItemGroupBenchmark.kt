package com.github.sckm.itemgroup.benchmark

import android.widget.TextView
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.runner.AndroidJUnit4
import com.github.sckm.itemgroup.ItemGroup
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemGroupBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun benchmarkItemGroupUpdateShuffled() {
        val items = generateItems()
        val shuffledItems = items.shuffled()

        benchmarkRule.measureRepeated {
            val itemGroup = ItemGroup()
            itemGroup.addAll(items)
            itemGroup.update(shuffledItems)
        }
    }

    @Test
    fun benchmarkItemGroupUpdateNoChanged() {
        val items1 = generateItems()
        val items2 = generateItems()

        benchmarkRule.measureRepeated {
            val itemGroup = ItemGroup()
            itemGroup.addAll(items1)
            itemGroup.update(items2)
        }
    }

    @Test
    fun benchmarkSectionUpdateShuffled() {
        val items = generateItems()
        val shuffledItems = items.shuffled()

        benchmarkRule.measureRepeated {
            val section = Section()
            section.addAll(items)
            section.update(shuffledItems)
        }
    }

    @Test
    fun benchmarkSectionUpdateNoChanged() {
        val items1 = generateItems()
        val items2 = generateItems()

        benchmarkRule.measureRepeated {
            val section = Section()
            section.addAll(items1)
            section.update(items2)
        }
    }

    @Test
    fun benchmarkItemGroupGetItem() {
        val items = generateItems()
        val section = ItemGroup()
        section.addAll(items)

        benchmarkRule.measureRepeated {
            (0 until items.size).forEach {
                section.getItem(it)
            }
        }
    }


    @Test
    fun benchmarkSectionGetItem() {
        val items = generateItems()
        val section = Section()
        section.addAll(items)

        benchmarkRule.measureRepeated {
            (0 until items.size).forEach {
                section.getItem(it)
            }
        }
    }


    private fun generateItems(): List<Item<*>> {
        return (0L..100L).map { id -> BenchmarkItem(Data(id, id.toString())) }
    }

    private data class Data(
        val id: Long,
        val text: String
    )

    private class BenchmarkItem(private val data: Data) : Item<ViewHolder>(data.id) {
        override fun getLayout(): Int = R.layout.layout_benchmark

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val tv = viewHolder.root.findViewById<TextView>(R.id.text_view)
            tv.text = id.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is BenchmarkItem) return false
            return data == other.data
        }

        override fun hashCode(): Int {
            return data.hashCode()
        }
    }
}